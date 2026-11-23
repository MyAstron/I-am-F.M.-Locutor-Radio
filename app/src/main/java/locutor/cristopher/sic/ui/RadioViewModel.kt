package locutor.cristopher.sic.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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

    fun onPreviousClicked() {
        coordinator.mediaRepository.skipToPrevious()
    }

    fun onPlayPauseClicked() {
        coordinator.mediaRepository.playPause()
    }

    fun onNextClicked() {
        coordinator.mediaRepository.skipToNext()
    }

    fun toggleAnnouncer() {
        coordinator.toggleAnnouncer()
    }

    fun triggerSimulationNow() {
        coordinator.triggerSimulationNow()
    }

    fun updateConfig(newConfig: AnnouncerConfig) {
        coordinator.updateConfig(newConfig)
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
