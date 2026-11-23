package locutor.cristopher.sic.domain.model

data class AnnouncementLog(
    val id: String = java.util.UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val timeLabel: String = "",
    val trackTitle: String = "",
    val artist: String = "",
    val appName: String = "",
    val speechText: String = "",
    val isAutomatic: Boolean = true,
    val status: String = "EXITOSO"
)
