package locutor.cristopher.sic.data.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.util.Log
import kotlinx.coroutines.delay

class AudioFadeController(private val context: Context) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var audioFocusRequest: AudioFocusRequest? = null

    private val focusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        Log.d("AudioFadeController", "AudioFocus changed: $focusChange")
    }

    /**
     * Aplica atenuación previa (Fade / Ducking) EXCLUSIVAMENTE sobre la música externa
     * mediante el mecanismo nativo de AudioFocus con ducking (AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK).
     *
     * IMPORTANTE: No modifica el volumen general del teléfono (STREAM_RING / STREAM_MUSIC global)
     * para preservar intacto el control de volumen del usuario (efecto tipo WhatsApp / Waze).
     */
    suspend fun fadeOutForVoice(fadeDurationMs: Long = 1500L, duckingDepthPercent: Int = 15) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val playbackAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()

                val builder = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
                    .setAudioAttributes(playbackAttributes)
                    .setAcceptsDelayedFocusGain(true)
                    .setOnAudioFocusChangeListener(focusChangeListener)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    builder.setWillPauseWhenDucked(false)
                }

                audioFocusRequest = builder.build()
                val focusResult = audioManager.requestAudioFocus(audioFocusRequest!!)
                Log.d("AudioFadeController", "AudioFocus Ducking concedido con resultado: $focusResult")
            } else {
                @Suppress("DEPRECATION")
                val focusResult = audioManager.requestAudioFocus(
                    focusChangeListener,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
                )
                Log.d("AudioFadeController", "AudioFocus Ducking legado concedido: $focusResult")
            }

            // Pausa breve para permitir la transición suave del ducking nativo de Spotify/YouTube Music
            delay((fadeDurationMs / 3).coerceIn(200L, 500L))
        } catch (e: Exception) {
            Log.e("AudioFadeController", "Error al solicitar foco con ducking", e)
        }
    }

    /**
     * Restaura el audio de la música abandonando el foco de audio.
     * Al liberar el foco, el reproductor externo (Spotify, YouTube Music, etc.)
     * devuelve automáticamente el volumen al 100% de manera fluida y suave.
     */
    suspend fun fadeInAfterVoice(fadeDurationMs: Long = 2000L) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                audioFocusRequest?.let {
                    val result = audioManager.abandonAudioFocusRequest(it)
                    Log.d("AudioFadeController", "AudioFocus abandonado con resultado: $result")
                }
                audioFocusRequest = null
            } else {
                @Suppress("DEPRECATION")
                val result = audioManager.abandonAudioFocus(focusChangeListener)
                Log.d("AudioFadeController", "AudioFocus legado abandonado con resultado: $result")
            }

            delay((fadeDurationMs / 4).coerceIn(150L, 400L))
        } catch (e: Exception) {
            Log.e("AudioFadeController", "Error al abandonar foco de audio", e)
        }
    }
}
