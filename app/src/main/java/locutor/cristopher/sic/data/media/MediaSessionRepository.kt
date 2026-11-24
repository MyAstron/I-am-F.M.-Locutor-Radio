package locutor.cristopher.sic.data.media

import android.content.ComponentName
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.KeyEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import locutor.cristopher.sic.domain.model.TrackInfo
import locutor.cristopher.sic.service.MediaMonitorNotificationListener

class MediaSessionRepository private constructor(private val context: Context) {

    private val mediaSessionManager =
        context.getSystemService(Context.MEDIA_SESSION_SERVICE) as? MediaSessionManager

    val musicLinker = MusicServiceLinker(context)

    private var activeController: MediaController? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    private val _currentTrack = MutableStateFlow(
        TrackInfo(
            title = "Sin reproducción activa",
            artist = "Abre tu app de música o pulsa Vincular",
            album = "",
            packageName = "",
            appName = "Sistema Android",
            isPlaying = false,
            durationMs = 0L,
            positionMs = 0L
        )
    )
    val currentTrack: StateFlow<TrackInfo> = _currentTrack.asStateFlow()

    private val _previousTrack = MutableStateFlow<TrackInfo?>(null)
    val previousTrack: StateFlow<TrackInfo?> = _previousTrack.asStateFlow()

    private val controllerCallback = object : MediaController.Callback() {
        override fun onMetadataChanged(metadata: MediaMetadata?) {
            updateTrackFromMetadata(metadata, activeController?.playbackState)
        }

        override fun onPlaybackStateChanged(state: PlaybackState?) {
            updatePlaybackState(state)
        }

        override fun onSessionDestroyed() {
            super.onSessionDestroyed()
            activeController = null
        }
    }

    private var onTrackChangedListener: ((previous: TrackInfo, current: TrackInfo) -> Unit)? = null

    fun setOnTrackChangedListener(listener: (previous: TrackInfo, current: TrackInfo) -> Unit) {
        onTrackChangedListener = listener
    }

    /**
     * Verifica si el usuario concedió el permiso de Acceso a Notificaciones en Ajustes de Android.
     */
    fun isNotificationAccessGranted(): Boolean {
        val pkgName = context.packageName
        val flat = Settings.Secure.getString(
            context.contentResolver,
            "enabled_notification_listeners"
        )
        return flat != null && flat.contains(pkgName)
    }

    /**
     * Consulta activamente las sesiones activas mediante el ComponentName del NotificationListener.
     */
    fun refreshActiveSessions() {
        try {
            val component = ComponentName(context, MediaMonitorNotificationListener::class.java)
            mediaSessionManager?.let { mgr ->
                val sessions = mgr.getActiveSessions(component)
                updateActiveSessions(sessions)
            }
        } catch (e: SecurityException) {
            Log.w("MediaSessionRepository", "Permiso de acceso a notificaciones pendiente", e)
        } catch (e: Exception) {
            Log.e("MediaSessionRepository", "Error al consultar getActiveSessions", e)
        }
    }

    /**
     * Vincula el MediaController correcto según la app vinculada o la sesión activa actual.
     * Limpia callbacks previos y re-registra en la sesión activa para recibir eventos en vivo.
     */
    fun updateActiveSessions(controllers: List<MediaController>) {
        val selectedPkg = musicLinker.selectedService.value
        val isAuto = musicLinker.isAutoDetect()

        val matchingController = if (!isAuto) {
            controllers.firstOrNull { it.packageName == selectedPkg }
                ?: controllers.firstOrNull { it.packageName != context.packageName }
        } else {
            controllers.firstOrNull { controller ->
                val pkg = controller.packageName ?: ""
                pkg != context.packageName && (
                        controller.playbackState?.state == PlaybackState.STATE_PLAYING ||
                                controller.playbackState?.state == PlaybackState.STATE_PAUSED
                        )
            } ?: controllers.firstOrNull { it.packageName != context.packageName }
        }

        if (matchingController != null) {
            if (activeController != matchingController) {
                try {
                    activeController?.unregisterCallback(controllerCallback)
                } catch (e: Exception) {
                    Log.w("MediaSessionRepository", "Error al desregistrar callback anterior", e)
                }
                activeController = matchingController
                try {
                    activeController?.registerCallback(controllerCallback, mainHandler)
                } catch (e: Exception) {
                    Log.w("MediaSessionRepository", "Error al registrar callback en controlador activo", e)
                }
            }
            updateTrackFromMetadata(activeController?.metadata, activeController?.playbackState)
        } else if (!isAuto) {
            val opt = musicLinker.getSelectedServiceOption()
            _currentTrack.value = _currentTrack.value.copy(
                packageName = opt.packageName,
                appName = opt.name
            )
        }
    }

    /**
     * Captura información de la canción directamente desde la notificación multimedia
     * (útil para YouTube Music, Spotify y reproductores donde getActiveSessions pueda demorarse o limitarse).
     */
    fun updateFromNotification(
        packageName: String,
        title: String,
        artist: String,
        album: String,
        artworkBitmap: Bitmap?,
        controller: MediaController?
    ) {
        val selectedPkg = musicLinker.selectedService.value
        val isAuto = musicLinker.isAutoDetect()

        if (!isAuto && packageName != selectedPkg) {
            return
        }

        if (controller != null && controller != activeController) {
            try {
                activeController?.unregisterCallback(controllerCallback)
            } catch (e: Exception) {
                Log.w("MediaSessionRepository", "Error desregistrando callback previo", e)
            }
            activeController = controller
            try {
                activeController?.registerCallback(controllerCallback, mainHandler)
            } catch (e: Exception) {
                Log.w("MediaSessionRepository", "Error registrando callback en controller notificado", e)
            }
        }

        val meta = activeController?.metadata
        val state = activeController?.playbackState

        val cleanTitle = meta?.getString(MediaMetadata.METADATA_KEY_TITLE)
            ?: meta?.getString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE)
            ?: title

        val cleanArtist = meta?.getString(MediaMetadata.METADATA_KEY_ARTIST)
            ?: meta?.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST)
            ?: artist

        val cleanAlbum = meta?.getString(MediaMetadata.METADATA_KEY_ALBUM)
            ?: album

        val isPlaying = state?.state == PlaybackState.STATE_PLAYING || (state == null)

        val finalBitmap = artworkBitmap
            ?: meta?.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART)
            ?: meta?.getBitmap(MediaMetadata.METADATA_KEY_ART)
            ?: _currentTrack.value.albumArtBitmap

        val newTrack = TrackInfo(
            title = cleanTitle,
            artist = cleanArtist,
            album = cleanAlbum,
            packageName = packageName,
            appName = PlatformResolver.resolveAppName(packageName),
            isPlaying = isPlaying,
            durationMs = meta?.getLong(MediaMetadata.METADATA_KEY_DURATION) ?: _currentTrack.value.durationMs,
            positionMs = state?.position ?: _currentTrack.value.positionMs,
            playbackState = state?.state ?: PlaybackState.STATE_PLAYING,
            albumArtBitmap = finalBitmap
        )

        val oldTrack = _currentTrack.value
        if (oldTrack.hasTrack && oldTrack.title != newTrack.title && newTrack.hasTrack) {
            _previousTrack.value = oldTrack
            onTrackChangedListener?.invoke(oldTrack, newTrack)
        }

        _currentTrack.value = newTrack
    }

    fun onNotificationRemoved(packageName: String?) {
        if (packageName == null) return
        if (_currentTrack.value.packageName == packageName) {
            _currentTrack.value = _currentTrack.value.copy(isPlaying = false)
        }
    }

    /**
     * Extrae título, artista, álbum y carátula (Bitmap o URI decodificado)
     * compatibles con YouTube Music, Spotify, Apple Music y reproductores locales.
     */
    private fun updateTrackFromMetadata(metadata: MediaMetadata?, state: PlaybackState?) {
        val controller = activeController ?: return

        val title = metadata?.getString(MediaMetadata.METADATA_KEY_TITLE)
            ?: metadata?.getString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE)
            ?: "Reproduciendo"

        val artist = metadata?.getString(MediaMetadata.METADATA_KEY_ARTIST)
            ?: metadata?.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST)
            ?: "Artista externo"

        val album = metadata?.getString(MediaMetadata.METADATA_KEY_ALBUM) ?: ""
        val duration = metadata?.getLong(MediaMetadata.METADATA_KEY_DURATION) ?: 0L
        val position = state?.position ?: 0L
        val isPlaying = state?.state == PlaybackState.STATE_PLAYING

        // Extracción de carátula en Bitmap
        var artBitmap: Bitmap? = try {
            metadata?.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART)
                ?: metadata?.getBitmap(MediaMetadata.METADATA_KEY_ART)
        } catch (e: Exception) {
            Log.w("MediaSessionRepository", "Error al leer bitmap de metadatos", e)
            null
        }

        val artUri = metadata?.getString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI)
            ?: metadata?.getString(MediaMetadata.METADATA_KEY_ART_URI)

        if (artBitmap == null && !artUri.isNullOrBlank()) {
            try {
                val parsedUri = Uri.parse(artUri)
                context.contentResolver.openInputStream(parsedUri)?.use { stream ->
                    artBitmap = BitmapFactory.decodeStream(stream)
                }
            } catch (e: Exception) {
                Log.w("MediaSessionRepository", "No se pudo cargar carátula desde URI: $artUri", e)
            }
        }

        val resolvedAppName = PlatformResolver.resolveAppName(controller.packageName ?: "")

        val newTrack = TrackInfo(
            title = title,
            artist = artist,
            album = album,
            packageName = controller.packageName ?: "",
            appName = resolvedAppName,
            isPlaying = isPlaying,
            durationMs = duration,
            positionMs = position,
            playbackState = state?.state ?: 0,
            albumArtUri = artUri,
            albumArtBitmap = artBitmap ?: _currentTrack.value.albumArtBitmap
        )

        val oldTrack = _currentTrack.value
        if (oldTrack.hasTrack && oldTrack.title != newTrack.title && newTrack.hasTrack) {
            _previousTrack.value = oldTrack
            onTrackChangedListener?.invoke(oldTrack, newTrack)
        }

        _currentTrack.value = newTrack
    }

    /**
     * Única fuente de la verdad para el estado de reproducción:
     * Actualiza isPlaying directamente del PlaybackState real de la sesión (ej. YouTube Music).
     */
    private fun updatePlaybackState(state: PlaybackState?) {
        if (state == null) return
        val isPlaying = state.state == PlaybackState.STATE_PLAYING
        val pos = state.position
        _currentTrack.value = _currentTrack.value.copy(
            isPlaying = isPlaying,
            positionMs = pos,
            playbackState = state.state
        )
    }

    /*
     * Controles funcionales:
     * 1. MediaController.transportControls (prioridad y sincronización nativa)
     * 2. Fallback de evento directo AudioManager.dispatchMediaKeyEvent si no hay sesión
     */
    fun skipToPrevious() {
        try {
            if (activeController != null) {
                activeController?.transportControls?.skipToPrevious()
            } else {
                musicLinker.sendDirectMediaKey(KeyEvent.KEYCODE_MEDIA_PREVIOUS)
            }
        } catch (e: Exception) {
            musicLinker.sendDirectMediaKey(KeyEvent.KEYCODE_MEDIA_PREVIOUS)
            Log.e("MediaSessionRepository", "Fallback skipToPrevious", e)
        }
    }

    /**
     * Corrige la desincronización de Play/Pausa con YouTube Music y otras apps:
     * Evalúa el estado real del PlaybackState (STATE_PLAYING) para enviar pause() o play(),
     * y NO invierte especulativamente el estado local. onPlaybackStateChanged actualizará la UI.
     */
    fun playPause() {
        try {
            val controller = activeController
            val currentState = controller?.playbackState?.state

            if (controller != null) {
                if (currentState == PlaybackState.STATE_PLAYING) {
                    controller.transportControls.pause()
                } else {
                    controller.transportControls.play()
                }
            } else {
                musicLinker.sendDirectMediaKey(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)
            }
        } catch (e: Exception) {
            musicLinker.sendDirectMediaKey(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)
            Log.e("MediaSessionRepository", "Fallback playPause", e)
        }
    }

    fun skipToNext() {
        try {
            if (activeController != null) {
                activeController?.transportControls?.skipToNext()
            } else {
                musicLinker.sendDirectMediaKey(KeyEvent.KEYCODE_MEDIA_NEXT)
            }
        } catch (e: Exception) {
            musicLinker.sendDirectMediaKey(KeyEvent.KEYCODE_MEDIA_NEXT)
            Log.e("MediaSessionRepository", "Fallback skipToNext", e)
        }
    }

    companion object {
        @Volatile
        private var instance: MediaSessionRepository? = null

        fun getInstance(context: Context): MediaSessionRepository {
            return instance ?: synchronized(this) {
                instance ?: MediaSessionRepository(context.applicationContext).also { instance = it }
            }
        }
    }
}
