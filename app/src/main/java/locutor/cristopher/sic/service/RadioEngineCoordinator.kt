package locutor.cristopher.sic.service

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import locutor.cristopher.sic.data.audio.AudioFadeController
import locutor.cristopher.sic.data.media.MediaSessionRepository
import locutor.cristopher.sic.data.phrases.PhraseCrudRepository
import locutor.cristopher.sic.data.phrases.RadioPhrase
import locutor.cristopher.sic.data.preferences.RadioPreferences
import locutor.cristopher.sic.data.voice.AndroidTtsVoiceEngine
import locutor.cristopher.sic.data.voice.VoiceEngine
import locutor.cristopher.sic.data.weather.LocalWeatherProvider
import locutor.cristopher.sic.domain.model.AnnouncementLog
import locutor.cristopher.sic.domain.model.AnnouncementScheduleMode
import locutor.cristopher.sic.domain.model.AnnouncerConfig
import locutor.cristopher.sic.domain.model.BackgroundServiceMode
import locutor.cristopher.sic.domain.model.TrackInfo
import locutor.cristopher.sic.domain.usecase.EvaluateHourlyAnnouncementUseCase
import locutor.cristopher.sic.domain.usecase.GenerateRadioScriptUseCase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RadioEngineCoordinator private constructor(private val context: Context) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    val preferences = RadioPreferences(context)
    val mediaRepository = MediaSessionRepository.getInstance(context)
    val fadeController = AudioFadeController(context)
    val voiceEngine: VoiceEngine = AndroidTtsVoiceEngine(context, scope)
    val phraseRepository = PhraseCrudRepository(context)
    val weatherProvider = LocalWeatherProvider(context)

    private val evaluateHourlyUseCase = EvaluateHourlyAnnouncementUseCase()
    private val scriptUseCase = GenerateRadioScriptUseCase()

    private val _historyLogs = MutableStateFlow<List<AnnouncementLog>>(
        listOf(
            AnnouncementLog(
                timeLabel = "13:00",
                trackTitle = "Midnight City",
                artist = "M83",
                appName = "Spotify",
                speechText = "Terminaba Midnight City de M83. Son la 1 en punto en I AM-FM. Seguimos con buena música en el camino.",
                isAutomatic = true,
                status = "EXITOSO"
            )
        )
    )
    val historyLogs: StateFlow<List<AnnouncementLog>> = _historyLogs.asStateFlow()

    private val _isExecutingAnnouncement = MutableStateFlow(false)
    val isExecutingAnnouncement: StateFlow<Boolean> = _isExecutingAnnouncement.asStateFlow()

    private val _duckingActive = MutableStateFlow(false)
    val duckingActive: StateFlow<Boolean> = _duckingActive.asStateFlow()

    private val _serviceStartTime = System.currentTimeMillis()
    val serviceStartTime: Long get() = _serviceStartTime

    // Contador de canciones reproducidas para el modo de intervalo por número de canciones
    private var completedSongsCount: Int = 0

    init {
        voiceEngine.initialize()

        // 1. Monitorear cambios de canción para evaluar locución horaria cuando termina un tema
        mediaRepository.setOnTrackChangedListener { previousTrack, currentTrack ->
            onTrackChanged(previousTrack, currentTrack)
        }

        // 2. Ticker de reloj continuo: asegura que suenen los anuncios a la hora en punto
        // o según la programación de horarios personalizados incluso sin música sonando.
        scope.launch {
            while (true) {
                delay(2000)
                checkClockHourlyAnnouncement()
            }
        }
    }

    private fun checkClockHourlyAnnouncement() {
        val config = preferences.config.value
        if (config.backgroundServiceMode == BackgroundServiceMode.OFF) return

        // Para reloj continuo revisamos modos horarios (DEFAULT, EXACT_TIME_INTERRUPT, CUSTOM_HOURS)
        val shouldAnnounce = evaluateHourlyUseCase.shouldAnnounce(
            isEnabled = config.isEnabled,
            lastAnnouncedHour = config.lastAnnouncedHour,
            scheduleMode = config.scheduleMode,
            customScheduledHours = config.customScheduledHours,
            completedSongsCount = completedSongsCount,
            songsInterval = config.songsInterval,
            isTriggeredBySongEnd = false
        )

        if (shouldAnnounce && !_isExecutingAnnouncement.value) {
            val currentHour = evaluateHourlyUseCase.getCurrentHour()
            preferences.setLastAnnouncedHour(currentHour)

            scope.launch {
                val current = mediaRepository.currentTrack.value
                val previous = mediaRepository.previousTrack.value
                val trackToUse = if (current.hasTrack) current else (previous ?: current)
                executeAnnouncement(
                    trackToAnnounce = trackToUse,
                    isAutomatic = true
                )
            }
        }
    }

    private fun onTrackChanged(previousTrack: TrackInfo, currentTrack: TrackInfo) {
        val config = preferences.config.value
        if (config.backgroundServiceMode == BackgroundServiceMode.OFF) return

        if (previousTrack.hasTrack && previousTrack.title != currentTrack.title) {
            completedSongsCount++
        }

        val shouldAnnounce = evaluateHourlyUseCase.shouldAnnounce(
            isEnabled = config.isEnabled,
            lastAnnouncedHour = config.lastAnnouncedHour,
            scheduleMode = config.scheduleMode,
            customScheduledHours = config.customScheduledHours,
            completedSongsCount = completedSongsCount,
            songsInterval = config.songsInterval,
            isTriggeredBySongEnd = true
        )

        if (shouldAnnounce && !_isExecutingAnnouncement.value) {
            if (config.scheduleMode == AnnouncementScheduleMode.SONG_COUNT) {
                completedSongsCount = 0
            } else {
                val currentHour = evaluateHourlyUseCase.getCurrentHour()
                preferences.setLastAnnouncedHour(currentHour)
            }

            scope.launch {
                executeAnnouncement(
                    trackToAnnounce = previousTrack,
                    isAutomatic = true
                )
            }
        }
    }

    suspend fun executeAnnouncement(
        trackToAnnounce: TrackInfo,
        isAutomatic: Boolean = true
    ) {
        if (_isExecutingAnnouncement.value) return
        _isExecutingAnnouncement.value = true

        val config = preferences.config.value
        val weatherEnabled = weatherProvider.isWeatherEnabled.value

        // Selección de frase con rotación anti-repetición y filtro de clima
        val selectedPhrase = phraseRepository.getNextNonRepeatingPhrase(weatherEnabled = weatherEnabled)
        val weatherText = if (weatherEnabled) weatherProvider.getWeatherReport() else ""

        // Se evalúa la hora exacta en este instante
        val exactNow = Calendar.getInstance()
        val script = scriptUseCase.execute(
            template = selectedPhrase.template,
            trackTitle = trackToAnnounce.title,
            artist = trackToAnnounce.artist,
            weatherReport = weatherText,
            calendar = exactNow
        )

        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val timeLabel = timeFormat.format(exactNow.time)

        val isMusicPlaying = trackToAnnounce.isPlaying

        try {
            // 1. Ducking de la música externa si hay música reproduciéndose
            if (isMusicPlaying) {
                _duckingActive.value = true
                fadeController.fadeOutForVoice(
                    fadeDurationMs = config.fadeDownDurationMs,
                    duckingDepthPercent = config.duckingDepthPercent
                )
            }

            // 2. Síntesis y locución por voz
            voiceEngine.speak(
                text = script,
                pitch = config.pitch,
                speechRate = config.speechRate,
                voiceName = config.selectedVoiceName,
                onStart = {
                    Log.d("RadioEngineCoordinator", "Inició locución: $script")
                },
                onDone = {
                    Log.d("RadioEngineCoordinator", "Finalizó locución")
                },
                onError = { err ->
                    Log.e("RadioEngineCoordinator", "Error locución: $err")
                }
            )

            // Registrar en historial
            val newLog = AnnouncementLog(
                timeLabel = timeLabel,
                trackTitle = if (trackToAnnounce.hasTrack) trackToAnnounce.title else "Sin música (Boletín horario)",
                artist = if (trackToAnnounce.hasTrack) trackToAnnounce.artist else "Emisión I AM-FM",
                appName = trackToAnnounce.appName,
                speechText = script,
                isAutomatic = isAutomatic,
                status = "EXITOSO"
            )
            _historyLogs.value = listOf(newLog) + _historyLogs.value
        } catch (e: Exception) {
            Log.e("RadioEngineCoordinator", "Error durante anuncio", e)
        } finally {
            // 3. Fade in / Restauración del audio si estaba sonando música
            if (isMusicPlaying) {
                fadeController.fadeInAfterVoice(fadeDurationMs = config.fadeUpDurationMs)
                _duckingActive.value = false
            }
            _isExecutingAnnouncement.value = false
        }
    }

    fun triggerSimulationNow() {
        scope.launch {
            val track = mediaRepository.currentTrack.value
            executeAnnouncement(trackToAnnounce = track, isAutomatic = false)
        }
    }

    fun toggleAnnouncer() {
        val current = preferences.config.value.isEnabled
        preferences.setEnabled(!current)
    }

    fun updateConfig(newConfig: AnnouncerConfig) {
        preferences.updateConfig(newConfig)
    }

    fun setBackgroundServiceMode(mode: BackgroundServiceMode) {
        preferences.setBackgroundServiceMode(mode)
    }

    fun setWeatherEnabled(enabled: Boolean) {
        weatherProvider.setWeatherEnabled(enabled)
        phraseRepository.onWeatherSettingChanged(enabled)
    }

    fun getUptimeFormatted(): String {
        val elapsedMillis = System.currentTimeMillis() - _serviceStartTime
        val seconds = (elapsedMillis / 1000) % 60
        val minutes = (elapsedMillis / (1000 * 60)) % 60
        val hours = (elapsedMillis / (1000 * 60 * 60))
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
    }

    companion object {
        @Volatile
        private var instance: RadioEngineCoordinator? = null

        fun getInstance(context: Context): RadioEngineCoordinator {
            return instance ?: synchronized(this) {
                instance ?: RadioEngineCoordinator(context.applicationContext).also { instance = it }
            }
        }
    }
}
