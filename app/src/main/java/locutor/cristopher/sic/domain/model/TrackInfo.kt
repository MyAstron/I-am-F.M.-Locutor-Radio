package locutor.cristopher.sic.domain.model

import android.graphics.Bitmap

data class TrackInfo(
    val title: String = "Sin reproducción activa",
    val artist: String = "Abre tu app de música (Spotify, YouTube Music...)",
    val album: String = "",
    val packageName: String = "",
    val appName: String = "Sistema Android",
    val isPlaying: Boolean = false,
    val durationMs: Long = 0L,
    val positionMs: Long = 0L,
    val playbackState: Int = 0,
    val albumArtUri: String? = null,
    val albumArtBitmap: Bitmap? = null
) {
    val hasTrack: Boolean
        get() = title.isNotBlank() && title != "Sin reproducción activa"

    val formattedDuration: String
        get() = formatTime(durationMs)

    val formattedPosition: String
        get() = formatTime(positionMs)

    val progressFraction: Float
        get() = if (durationMs > 0) (positionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f

    private fun formatTime(millis: Long): String {
        if (millis <= 0) return "00:00"
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}
