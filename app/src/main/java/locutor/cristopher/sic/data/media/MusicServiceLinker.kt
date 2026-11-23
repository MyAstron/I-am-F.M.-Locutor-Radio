package locutor.cristopher.sic.data.media

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.AudioManager
import android.view.KeyEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class MusicServiceOption(
    val name: String,
    val packageName: String,
    val isAuto: Boolean = false
)

class MusicServiceLinker(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("music_linker_prefs", Context.MODE_PRIVATE)

    private val _selectedService = MutableStateFlow(
        prefs.getString("selected_package", AUTO_DETECT_PACKAGE) ?: AUTO_DETECT_PACKAGE
    )
    val selectedService: StateFlow<String> = _selectedService.asStateFlow()

    val availableServices = listOf(
        MusicServiceOption("Detección Automática", AUTO_DETECT_PACKAGE, isAuto = true),
        MusicServiceOption("Spotify", "com.spotify.music"),
        MusicServiceOption("YouTube Music", "com.google.android.apps.youtube.music"),
        MusicServiceOption("Apple Music", "com.apple.android.music"),
        MusicServiceOption("Deezer", "deezer.android.app"),
        MusicServiceOption("Amazon Music", "com.amazon.mp3"),
        MusicServiceOption("Tidal", "com.aspiro.tidal"),
        MusicServiceOption("Reproductor Local MP3", "default.local.player")
    )

    fun selectService(packageName: String) {
        prefs.edit().putString("selected_package", packageName).apply()
        _selectedService.value = packageName
    }

    fun isAutoDetect(): Boolean = _selectedService.value == AUTO_DETECT_PACKAGE

    fun getSelectedServiceOption(): MusicServiceOption {
        return availableServices.find { it.packageName == _selectedService.value }
            ?: availableServices.first()
    }

    /**
     * Intenta abrir la app de música si no está en ejecución.
     */
    fun launchMusicApp(): Boolean {
        val pkg = _selectedService.value
        if (pkg == AUTO_DETECT_PACKAGE || pkg.isBlank()) return false
        val intent = context.packageManager.getLaunchIntentForPackage(pkg) ?: return false
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        return true
    }

    /**
     * Dispara eventos de tecla multimedia directos como respaldo si aún no hay sesión activa.
     */
    fun sendDirectMediaKey(keyCode: Int) {
        try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return
            val eventDown = KeyEvent(KeyEvent.ACTION_DOWN, keyCode)
            val eventUp = KeyEvent(KeyEvent.ACTION_UP, keyCode)
            audioManager.dispatchMediaKeyEvent(eventDown)
            audioManager.dispatchMediaKeyEvent(eventUp)
        } catch (_: Exception) {}
    }

    companion object {
        const val AUTO_DETECT_PACKAGE = "auto.detect"
    }
}
