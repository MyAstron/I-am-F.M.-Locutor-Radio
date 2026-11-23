package locutor.cristopher.sic.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import locutor.cristopher.sic.data.media.MusicServiceOption
import locutor.cristopher.sic.data.phrases.RadioPhrase
import locutor.cristopher.sic.domain.model.AnnouncementLog
import locutor.cristopher.sic.domain.model.AnnouncerConfig
import locutor.cristopher.sic.domain.model.AudioWaveState
import locutor.cristopher.sic.domain.model.TrackInfo
import locutor.cristopher.sic.domain.usecase.GenerateRadioScriptUseCase
import locutor.cristopher.sic.service.RadioEngineCoordinator
import java.util.Calendar

class RadioViewModel(application: Application) : AndroidViewModel(application) {

    private val coordinator = RadioEngineCoordinator.getInstance(application)
    private val scriptUseCase = GenerateRadioScriptUseCase()

    val currentTrack: StateFlow<TrackInfo> = coordinator.mediaRepository.currentTrack
    val announcerConfig: StateFlow<AnnouncerConfig> = coordinator.preferences.config
    val waveState: StateFlow<AudioWaveState> = coordinator.voiceEngine.waveState
    val historyLogs: StateFlow<List<AnnouncementLog>> = coordinator.historyLogs
    val isExecutingAnnouncement: StateFlow<Boolean> = coordinator.isExecutingAnnouncement
    val duckingActive: StateFlow<Boolean> = coordinator.duckingActive

    val phrases: StateFlow<List<RadioPhrase>> = coordinator.phraseRepository.phrases
    val selectedService: StateFlow<String> = coordinator.mediaRepository.musicLinker.selectedService
    val availableServices: List<MusicServiceOption>
        get() = coordinator.mediaRepository.musicLinker.getDetectedServices()
    val isWeatherEnabled: StateFlow<Boolean> = coordinator.weatherProvider.isWeatherEnabled

    private val _isServiceSelectorOpen = MutableStateFlow(false)
    val isServiceSelectorOpen: StateFlow<Boolean> = _isServiceSelectorOpen.asStateFlow()

    fun openServiceSelector() {
        _isServiceSelectorOpen.value = true
    }

    fun closeServiceSelector() {
        _isServiceSelectorOpen.value = false
    }

    private val _nextBroadcastLabel = MutableStateFlow("14:00")
    val nextBroadcastLabel: StateFlow<String> = _nextBroadcastLabel.asStateFlow()

    private val _countdownSeconds = MutableStateFlow(0L)
    val countdownSeconds: StateFlow<Long> = _countdownSeconds.asStateFlow()

    private val _uptimeLabel = MutableStateFlow("00:00:00")
    val uptimeLabel: StateFlow<String> = _uptimeLabel.asStateFlow()

    init {
        viewModelScope.launch {
            while (true) {
                val cal = Calendar.getInstance()
                _nextBroadcastLabel.value = scriptUseCase.getNextBroadcastTimeLabel(cal)
                _countdownSeconds.value = scriptUseCase.getRemainingSecondsToNextHour(cal)
                _uptimeLabel.value = coordinator.getUptimeFormatted()
                delay(1000)
            }
        }
    }

    // Controles multimedia estilo Waze
    fun onPreviousClicked() {
        coordinator.mediaRepository.skipToPrevious()
    }

    fun onPlayPauseClicked() {
        coordinator.mediaRepository.playPause()
    }

    fun onNextClicked() {
        coordinator.mediaRepository.skipToNext()
    }

    // Selección de servicio de música
    fun selectMusicService(packageName: String) {
        coordinator.mediaRepository.musicLinker.selectService(packageName)
        coordinator.mediaRepository.refreshActiveSessions()
    }

    fun launchMusicApp(): Boolean {
        return coordinator.mediaRepository.musicLinker.launchMusicApp()
    }

    // Botón maestro y simulación
    fun toggleAnnouncer() {
        coordinator.toggleAnnouncer()
    }

    fun triggerSimulationNow() {
        coordinator.triggerSimulationNow()
    }

    fun updateConfig(newConfig: AnnouncerConfig) {
        coordinator.updateConfig(newConfig)
    }

    // CRUD de frases
    fun addPhrase(template: String) {
        coordinator.phraseRepository.addPhrase(template)
    }

    fun updatePhrase(id: String, template: String, isActive: Boolean) {
        coordinator.phraseRepository.updatePhrase(id, template, isActive)
    }

    fun togglePhraseActive(id: String) {
        coordinator.phraseRepository.togglePhraseActive(id)
    }

    fun deletePhrase(id: String) {
        coordinator.phraseRepository.deletePhrase(id)
    }

    // Clima local
    fun setWeatherEnabled(enabled: Boolean) {
        coordinator.weatherProvider.setWeatherEnabled(enabled)
    }

    fun hasLocationPermission(): Boolean {
        return coordinator.weatherProvider.hasLocationPermission()
    }

    fun testVoiceSpeech(sampleText: String) {
        viewModelScope.launch {
            val config = announcerConfig.value
            coordinator.voiceEngine.speak(
                text = sampleText,
                pitch = config.pitch,
                speechRate = config.speechRate
            )
        }
    }
}
