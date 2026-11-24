package locutor.cristopher.sic.data.media

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.AudioManager
import android.util.Log
import android.view.KeyEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class MusicServiceOption(
    val name: String,
    val packageName: String,
    val isAuto: Boolean = false,
    val isInstalled: Boolean = true
)

class MusicServiceLinker(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("music_linker_prefs", Context.MODE_PRIVATE)

    private val _selectedService = MutableStateFlow(
        prefs.getString("selected_package", AUTO_DETECT_PACKAGE) ?: AUTO_DETECT_PACKAGE
    )
    val selectedService: StateFlow<String> = _selectedService.asStateFlow()

    private val knownMusicPackages = listOf(
        Pair("Spotify", "com.spotify.music"),
        Pair("YouTube Music", "com.google.android.apps.youtube.music"),
        Pair("Apple Music", "com.apple.android.music"),
        Pair("Deezer", "deezer.android.app"),
        Pair("Amazon Music", "com.amazon.mp3"),
        Pair("Tidal", "com.aspiro.tidal"),
        Pair("SoundCloud", "com.soundcloud.android"),
        Pair("YouTube", "com.google.android.youtube"),
        Pair("VLC", "org.videolan.vlc"),
        Pair("Samsung Music", "com.sec.android.app.music"),
        Pair("Poweramp", "com.maxmpz.audioplayer"),
        Pair("Musicolet", "in.krosbits.musicolet")
    )

    val availableServices: List<MusicServiceOption>
        get() = getDetectedServices()

    fun getDetectedServices(): List<MusicServiceOption> {
        val list = mutableListOf<MusicServiceOption>()

        // 1. Opción predeterminada: Detección Automática Waze
        list.add(
            MusicServiceOption(
                name = "Detección Automática",
                packageName = AUTO_DETECT_PACKAGE,
                isAuto = true,
                isInstalled = true
            )
        )

        val processedPackages = mutableSetOf<String>()

        // 2. Apps conocidas: ÚNICAMENTE se listan las que están REALMENTE instaladas en el dispositivo
        knownMusicPackages.forEach { (name, pkg) ->
            if (isAppInstalled(pkg)) {
                list.add(
                    MusicServiceOption(
                        name = name,
                        packageName = pkg,
                        isAuto = false,
                        isInstalled = true
                    )
                )
                processedPackages.add(pkg)
            }
        }

        // 3. Descubrir reproductores de música adicionales instalados en el teléfono
        try {
            val musicIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_MUSIC)
            val resolvedApps = context.packageManager.queryIntentActivities(musicIntent, 0)
            for (resolveInfo in resolvedApps) {
                val pkg = resolveInfo.activityInfo?.packageName ?: continue
                if (pkg != context.packageName && !processedPackages.contains(pkg)) {
                    val appLabel = resolveInfo.loadLabel(context.packageManager).toString()
                    list.add(
                        MusicServiceOption(
                            name = appLabel,
                            packageName = pkg,
                            isAuto = false,
                            isInstalled = true
                        )
                    )
                    processedPackages.add(pkg)
                }
            }
        } catch (e: Exception) {
            Log.w("MusicServiceLinker", "Error descubriendo reproductores por CATEGORY_APP_MUSIC", e)
        }

        // 4. Opción genérica local
        list.add(
            MusicServiceOption(
                name = "Reproductor Local MP3",
                packageName = "default.local.player",
                isAuto = false,
                isInstalled = true
            )
        )

        return list
    }

    private fun isAppInstalled(packageName: String): Boolean {
        return try {
            val pm = context.packageManager
            pm.getPackageInfo(packageName, 0)
            true
        } catch (_: Exception) {
            try {
                context.packageManager.getLaunchIntentForPackage(packageName) != null
            } catch (_: Exception) {
                false
            }
        }
    }

    fun selectService(packageName: String) {
        prefs.edit().putString("selected_package", packageName).apply()
        _selectedService.value = packageName
    }

    fun isAutoDetect(): Boolean = _selectedService.value == AUTO_DETECT_PACKAGE

    fun getSelectedServiceOption(): MusicServiceOption {
        val all = getDetectedServices()
        return all.find { it.packageName == _selectedService.value } ?: all.first()
    }

    /**
     * Intenta abrir la app de música si no está en ejecución.
     */
    fun launchMusicApp(): Boolean {
        val pkg = _selectedService.value
        if (pkg == AUTO_DETECT_PACKAGE || pkg.isBlank() || pkg == "default.local.player") return false
        val intent = context.packageManager.getLaunchIntentForPackage(pkg) ?: return false
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return try {
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Respaldo por emisión de eventos directos de hardware para controles básicos.
     */
    fun sendDirectMediaKey(keyCode: Int) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return
        val downEvent = KeyEvent(KeyEvent.ACTION_DOWN, keyCode)
        val upEvent = KeyEvent(KeyEvent.ACTION_UP, keyCode)
        audioManager.dispatchMediaKeyEvent(downEvent)
        audioManager.dispatchMediaKeyEvent(upEvent)
    }

    companion object {
        const val AUTO_DETECT_PACKAGE = "auto.detect.media"
    }
}
