package locutor.cristopher.sic.data.voice

import kotlinx.coroutines.flow.StateFlow
import locutor.cristopher.sic.domain.model.AudioWaveState

data class SystemVoiceInfo(
    val name: String,
    val displayName: String,
    val locale: String
)

interface VoiceEngine {
    val waveState: StateFlow<AudioWaveState>
    val availableVoices: StateFlow<List<SystemVoiceInfo>>
    fun initialize(onReady: () -> Unit = {})
    suspend fun speak(
        text: String,
        pitch: Float = 1.0f,
        speechRate: Float = 1.1f,
        voiceName: String? = null,
        onStart: () -> Unit = {},
        onDone: () -> Unit = {},
        onError: (String) -> Unit = {}
    )
    fun stop()
    fun release()
}
