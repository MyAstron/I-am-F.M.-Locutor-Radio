package locutor.cristopher.sic.domain.model

enum class AnnouncementScheduleMode {
    DEFAULT_TOP_OF_HOUR,      // Por defecto FM: Hora en punto al terminar canción o en reloj
    SONG_COUNT,                // Cada N canciones terminadas
    EXACT_TIME_INTERRUPT,      // Interrumpir en la hora exacta de inmediato
    CUSTOM_HOURS               // Horas específicas del día seleccionadas por el usuario
}

enum class BackgroundServiceMode {
    ON,   // Siempre activo en primer plano
    OFF,  // Desactivado
    AUTO  // Inteligente: se activa al detectar música
}

data class AnnouncerConfig(
    val isEnabled: Boolean = true,
    val voiceStyle: String = "Radio DJ Dinámico",
    val selectedVoiceName: String = "",
    val pitch: Float = 1.1f,
    val speechRate: Float = 1.15f,
    val scriptTemplate: String = "Acabas de escuchar {canción} de {artista}. En I AM-FM son exactamente {hora}. ¡Seguimos en ruta!",
    val fadeDownDurationMs: Long = 1500L,
    val fadeUpDurationMs: Long = 2000L,
    val duckingDepthPercent: Int = 15,
    val lastAnnouncedHour: Int = -1,
    val scheduleMode: AnnouncementScheduleMode = AnnouncementScheduleMode.DEFAULT_TOP_OF_HOUR,
    val songsInterval: Int = 3,
    val customScheduledHours: Set<Int> = setOf(8, 12, 14, 18, 20),
    val backgroundServiceMode: BackgroundServiceMode = BackgroundServiceMode.AUTO
)
