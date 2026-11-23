package locutor.cristopher.sic.data.voice

import kotlinx.coroutines.flow.StateFlow
import locutor.cristopher.sic.domain.model.AudioWaveState

interface VoiceEngine {
    val waveState: StateFlow<AudioWaveState>
    fun initialize(onReady: () -> Unit = {})
    suspend fun speak(
        text: String,
        pitch: Float = 1.0f,
        speechRate: Float = 1.1f,
        onStart: () -> Unit = {},
        onDone: () -> Unit = {},
        onError: (String) -> Unit = {}
    )
    fun stop()
    fun release()
}
