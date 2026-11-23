package locutor.cristopher.sic.domain.model

data class AudioWaveState(
    val intensity: Float = 0f,
    val isEmitting: Boolean = false,
    val statusText: String = "En espera"
)
