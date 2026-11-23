package locutor.cristopher.sic.data.media

import android.content.Context
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import locutor.cristopher.sic.domain.model.TrackInfo

class MediaSessionRepository private constructor(private val context: Context) {

    private val mediaSessionManager =
        context.getSystemService(Context.MEDIA_SESSION_SERVICE) as? MediaSessionManager

    val musicLinker = MusicServiceLinker(context)

    private var activeController: MediaController? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    private val _currentTrack = MutableStateFlow(
        TrackInfo(
            title = "Sin reproducción activa",
            artist = "Abre tu música o pulsa Vincular Servicio",
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

    fun updateActiveSessions(controllers: List<MediaController>) {
        val selectedPkg = musicLinker.selectedService.value
        val isAuto = musicLinker.isAutoDetect()

        val matchingController = if (!isAuto) {
            controllers.firstOrNull { it.packageName == selectedPkg }
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
                activeController?.unregisterCallback(controllerCallback)
                activeController = matchingController
                activeController?.registerCallback(controllerCallback, mainHandler)
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

        val newTrack = TrackInfo(
            title = title,
            artist = artist,
            album = album,
            packageName = controller.packageName ?: "",
            appName = PlatformResolver.resolveAppName(controller.packageName ?: ""),
            isPlaying = isPlaying,
            durationMs = duration,
            positionMs = position,
            playbackState = state?.state ?: 0
        )

        val oldTrack = _currentTrack.value
        if (oldTrack.hasTrack && oldTrack.title != newTrack.title && newTrack.hasTrack) {
            _previousTrack.value = oldTrack
            onTrackChangedListener?.invoke(oldTrack, newTrack)
        }

        _currentTrack.value = newTrack
    }

    private fun updatePlaybackState(state: PlaybackState?) {
        val isPlaying = state?.state == PlaybackState.STATE_PLAYING
        val pos = state?.position ?: _currentTrack.value.positionMs
        _currentTrack.value = _currentTrack.value.copy(
            isPlaying = isPlaying,
            positionMs = pos,
            playbackState = state?.state ?: 0
        )
    }

    /*
     * Controles funcionales con doble estrategia:
     * 1. MediaController.transportControls
     * 2. Fallback de evento directo AudioManager.dispatchMediaKeyEvent
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

    fun playPause() {
        try {
            val isPlaying = _currentTrack.value.isPlaying
            if (activeController != null) {
                if (isPlaying) {
                    activeController?.transportControls?.pause()
                    _currentTrack.value = _currentTrack.value.copy(isPlaying = false)
                } else {
                    activeController?.transportControls?.play()
                    _currentTrack.value = _currentTrack.value.copy(isPlaying = true)
                }
            } else {
                musicLinker.sendDirectMediaKey(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)
                _currentTrack.value = _currentTrack.value.copy(isPlaying = !isPlaying)
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
