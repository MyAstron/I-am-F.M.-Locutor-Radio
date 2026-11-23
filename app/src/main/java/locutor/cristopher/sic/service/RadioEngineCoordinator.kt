package locutor.cristopher.sic.service

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import locutor.cristopher.sic.data.audio.AudioFadeController
import locutor.cristopher.sic.data.media.MediaSessionRepository
import locutor.cristopher.sic.data.preferences.RadioPreferences
import locutor.cristopher.sic.data.voice.AndroidTtsVoiceEngine
import locutor.cristopher.sic.data.voice.VoiceEngine
import locutor.cristopher.sic.domain.model.AnnouncementLog
import locutor.cristopher.sic.domain.model.AnnouncerConfig
import locutor.cristopher.sic.domain.model.AudioWaveState
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

    init {
        voiceEngine.initialize()

        // Monitorear cambios de canción para evaluar locución horaria
        mediaRepository.setOnTrackChangedListener { previousTrack, currentTrack ->
            onTrackChanged(previousTrack, currentTrack)
        }
    }

    private fun onTrackChanged(previousTrack: TrackInfo, currentTrack: TrackInfo) {
        val config = preferences.config.value
        val shouldAnnounce = evaluateHourlyUseCase.shouldAnnounce(
            isEnabled = config.isEnabled,
            hasCompletedTrack = previousTrack.hasTrack,
            lastAnnouncedHour = config.lastAnnouncedHour
        )

        if (shouldAnnounce) {
            val currentHour = evaluateHourlyUseCase.getCurrentHour()
            preferences.setLastAnnouncedHour(currentHour)

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
        val script = scriptUseCase.execute(
            template = config.scriptTemplate,
            trackTitle = trackToAnnounce.title,
            artist = trackToAnnounce.artist
        )

        val calendar = Calendar.getInstance()
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val timeLabel = timeFormat.format(calendar.time)

        try {
            // 1. Fade out / Ducking de la música externa
            _duckingActive.value = true
            fadeController.fadeOutForVoice(
                fadeDurationMs = config.fadeDownDurationMs,
                duckingDepthPercent = config.duckingDepthPercent
            )

            // 2. Síntesis y locución por voz
            voiceEngine.speak(
                text = script,
                pitch = config.pitch,
                speechRate = config.speechRate,
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
                trackTitle = trackToAnnounce.title,
                artist = trackToAnnounce.artist,
                appName = trackToAnnounce.appName,
                speechText = script,
                isAutomatic = isAutomatic,
                status = "EXITOSO"
            )
            _historyLogs.value = listOf(newLog) + _historyLogs.value
        } catch (e: Exception) {
            Log.e("RadioEngineCoordinator", "Error durante anuncio", e)
        } finally {
            // 3. Fade in / Restauración del audio
            fadeController.fadeInAfterVoice(fadeDurationMs = config.fadeUpDurationMs)
            _duckingActive.value = false
            _isExecutingAnnouncement.value = false
        }
    }

    /**
     * Prueba instantánea del simulador de cabina sin esperar la hora en punto.
     */
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

    fun updateConfig(config: AnnouncerConfig) {
        preferences.updateConfig(config)
    }

    fun getUptimeFormatted(): String {
        val diffMs = System.currentTimeMillis() - _serviceStartTime
        val seconds = (diffMs / 1000) % 60
        val minutes = (diffMs / (1000 * 60)) % 60
        val hours = (diffMs / (1000 * 60 * 60))
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
