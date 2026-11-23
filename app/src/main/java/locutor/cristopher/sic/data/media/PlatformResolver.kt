package locutor.cristopher.sic.data.media

object PlatformResolver {
    fun resolveAppName(packageName: String): String {
        return when {
            packageName.contains("spotify", ignoreCase = true) -> "Spotify"
            packageName.contains("youtube.music", ignoreCase = true) -> "YouTube Music"
            packageName.contains("youtube", ignoreCase = true) -> "YouTube"
            packageName.contains("apple.android.music", ignoreCase = true) -> "Apple Music"
            packageName.contains("amazon.mp3", ignoreCase = true) -> "Amazon Music"
            packageName.contains("deezer", ignoreCase = true) -> "Deezer"
            packageName.contains("soundcloud", ignoreCase = true) -> "SoundCloud"
            packageName.contains("tidal", ignoreCase = true) -> "Tidal"
            packageName.contains("poweramp", ignoreCase = true) -> "Poweramp"
            packageName.contains("musicolet", ignoreCase = true) -> "Musicolet"
            packageName.isNotBlank() -> "App de Música"
            else -> "Sistema Android"
        }
    }
}
