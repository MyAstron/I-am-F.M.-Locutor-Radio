package locutor.cristopher.sic.data.voice

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import locutor.cristopher.sic.domain.model.AudioWaveState
import java.util.Locale
import java.util.UUID
import kotlin.coroutines.resume

class AndroidTtsVoiceEngine(
    private val context: Context,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) : VoiceEngine, TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var pendingReadyCallback: (() -> Unit)? = null

    private val _waveState = MutableStateFlow(AudioWaveState())
    override val waveState: StateFlow<AudioWaveState> = _waveState.asStateFlow()

    private var waveAnimationJob: Job? = null

    override fun initialize(onReady: () -> Unit) {
        if (isInitialized && tts != null) {
            onReady()
            return
        }
        pendingReadyCallback = onReady
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.forLanguageTag("es-ES"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Si español de España no está, probar español genérico o default del sistema
                tts?.setLanguage(Locale.forLanguageTag("es"))
            }

            // Configurar canal de audio como asistencia de navegación (estilo Waze/Maps)
            val audioAttributes = android.media.AudioAttributes.Builder()
                .setUsage(android.media.AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE)
                .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()
            tts?.setAudioAttributes(audioAttributes)

            isInitialized = true
            pendingReadyCallback?.invoke()
            pendingReadyCallback = null
            Log.d("AndroidTtsVoiceEngine", "TextToSpeech inicializado con éxito como USAGE_ASSISTANCE_NAVIGATION_GUIDANCE.")
        } else {
            Log.e("AndroidTtsVoiceEngine", "Error al inicializar TextToSpeech. Estado: $status")
        }
    }

    override suspend fun speak(
        text: String,
        pitch: Float,
        speechRate: Float,
        onStart: () -> Unit,
        onDone: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (!isInitialized || tts == null) {
            initialize {
                scope.launch {
                    speak(text, pitch, speechRate, onStart, onDone, onError)
                }
            }
            return
        }

        suspendCancellableCoroutine { continuation ->
            val utteranceId = UUID.randomUUID().toString()

            tts?.setPitch(pitch)
            tts?.setSpeechRate(speechRate)

            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(id: String?) {
                    if (id == utteranceId) {
                        scope.launch {
                            startWaveAnimation()
                            onStart()
                        }
                    }
                }

                override fun onDone(id: String?) {
                    if (id == utteranceId) {
                        scope.launch {
                            stopWaveAnimation()
                            onDone()
                            if (continuation.isActive) {
                                continuation.resume(Unit)
                            }
                        }
                    }
                }

                @Deprecated("Deprecated in Java")
                override fun onError(id: String?) {
                    if (id == utteranceId) {
                        scope.launch {
                            stopWaveAnimation()
                            onError("Error desconocido en TTS")
                            if (continuation.isActive) {
                                continuation.resume(Unit)
                            }
                        }
                    }
                }

                override fun onError(id: String?, errorCode: Int) {
                    if (id == utteranceId) {
                        scope.launch {
                            stopWaveAnimation()
                            onError("Error TTS código: $errorCode")
                            if (continuation.isActive) {
                                continuation.resume(Unit)
                            }
                        }
                    }
                }
            })

            val params = Bundle().apply {
                putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f)
            }

            val queueMode = TextToSpeech.QUEUE_FLUSH
            val res = tts?.speak(text, queueMode, params, utteranceId)
            if (res != TextToSpeech.SUCCESS) {
                stopWaveAnimation()
                onError("No se pudo iniciar la locución por voz")
                if (continuation.isActive) {
                    continuation.resume(Unit)
                }
            }

            continuation.invokeOnCancellation {
                stop()
            }
        }
    }

    private fun startWaveAnimation() {
        waveAnimationJob?.cancel()
        waveAnimationJob = scope.launch {
            _waveState.value = AudioWaveState(intensity = 0.5f, isEmitting = true, statusText = "Transmitiendo")
            val intensities = listOf(0.4f, 0.85f, 0.6f, 1.0f, 0.75f, 0.9f, 0.5f, 0.95f)
            var index = 0
            while (isActive) {
                val current = intensities[index % intensities.size]
                _waveState.value = _waveState.value.copy(intensity = current)
                index++
                delay(120)
            }
        }
    }

    private fun stopWaveAnimation() {
        waveAnimationJob?.cancel()
        waveAnimationJob = null
        _waveState.value = AudioWaveState(intensity = 0f, isEmitting = false, statusText = "En espera")
    }

    override fun stop() {
        stopWaveAnimation()
        tts?.stop()
    }

    override fun release() {
        stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}
