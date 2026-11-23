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
    private var originalMusicVolume: Int = -1

    private val focusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        Log.d("AudioFadeController", "AudioFocus changed: $focusChange")
    }

    /**
     * Aplica atenuación previa (Fade Out / Ducking) antes de la locución por voz
     * para garantizar prioridad acústica sobre la música externa.
     */
    suspend fun fadeOutForVoice(fadeDurationMs: Long = 1500L, duckingDepthPercent: Int = 15) {
        try {
            // Guardar volumen original para restaurarlo después
            originalMusicVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

            // Solicitar foco transitorio con ducking nativo
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val playbackAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()

                audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
                    .setAudioAttributes(playbackAttributes)
                    .setAcceptsDelayedFocusGain(true)
                    .setOnAudioFocusChangeListener(focusChangeListener)
                    .build()

                audioFocusRequest?.let { audioManager.requestAudioFocus(it) }
            } else {
                @Suppress("DEPRECATION")
                audioManager.requestAudioFocus(
                    focusChangeListener,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
                )
            }

            // Aplicar rampa suave de volumen hacia el nivel de ducking deseado
            val targetVolume = ((originalMusicVolume * duckingDepthPercent) / 100).coerceAtLeast(1)
            val steps = 6
            val stepDelay = (fadeDurationMs / steps).coerceAtLeast(20L)

            val currentVol = originalMusicVolume
            if (currentVol > targetVolume) {
                val volDiff = currentVol - targetVolume
                for (i in 1..steps) {
                    val intermediateVol = currentVol - (volDiff * i / steps)
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, intermediateVol, 0)
                    delay(stepDelay)
                }
            } else {
                delay(fadeDurationMs)
            }
        } catch (e: Exception) {
            Log.e("AudioFadeController", "Error al ejecutar fade out", e)
        }
    }

    /**
     * Restaura suavemente el volumen original de la música (Fade In) tras finalizar la locución.
     */
    suspend fun fadeInAfterVoice(fadeDurationMs: Long = 2000L) {
        try {
            if (originalMusicVolume >= 0) {
                val currentVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                val targetVol = originalMusicVolume
                val steps = 8
                val stepDelay = (fadeDurationMs / steps).coerceAtLeast(20L)

                if (targetVol > currentVol) {
                    val diff = targetVol - currentVol
                    for (i in 1..steps) {
                        val intermediateVol = currentVol + (diff * i / steps)
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, intermediateVol, 0)
                        delay(stepDelay)
                    }
                }
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, originalMusicVolume, 0)
                originalMusicVolume = -1
            }

            // Abandonar foco de audio
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                audioFocusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
                audioFocusRequest = null
            } else {
                @Suppress("DEPRECATION")
                audioManager.abandonAudioFocus(focusChangeListener)
            }
        } catch (e: Exception) {
            Log.e("AudioFadeController", "Error al ejecutar fade in", e)
        }
    }
}
