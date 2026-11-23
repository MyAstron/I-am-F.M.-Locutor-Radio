package locutor.cristopher.sic.domain.model

data class AnnouncerConfig(
    val isEnabled: Boolean = true,
    val voiceStyle: String = "Radio DJ Dinámico",
    val pitch: Float = 1.0f,
    val speechRate: Float = 1.1f,
    val scriptTemplate: String = "Acabas de escuchar {canción} de {artista}. En I AM-FM son exactamente las {hora en punto}. ¡Seguimos en ruta!",
    val fadeDownDurationMs: Long = 1500L,
    val fadeUpDurationMs: Long = 2000L,
    val duckingDepthPercent: Int = 15,
    val lastAnnouncedHour: Int = -1
)
