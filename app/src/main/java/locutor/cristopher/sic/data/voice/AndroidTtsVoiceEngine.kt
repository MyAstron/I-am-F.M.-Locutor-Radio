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

    private val _availableVoices = MutableStateFlow<List<SystemVoiceInfo>>(emptyList())
    override val availableVoices: StateFlow<List<SystemVoiceInfo>> = _availableVoices.asStateFlow()

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
                tts?.setLanguage(Locale.forLanguageTag("es"))
            }

            // Configurar canal de audio como asistencia de navegación (estilo Waze/Maps)
            val audioAttributes = android.media.AudioAttributes.Builder()
                .setUsage(android.media.AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE)
                .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()
            tts?.setAudioAttributes(audioAttributes)

            // Cargar lista de voces del sistema disponibles
            loadAvailableVoices()

            isInitialized = true
            pendingReadyCallback?.invoke()
            pendingReadyCallback = null
            Log.d("AndroidTtsVoiceEngine", "TextToSpeech inicializado con éxito.")
        } else {
            Log.e("AndroidTtsVoiceEngine", "Error al inicializar TextToSpeech. Estado: $status")
        }
    }

    private fun loadAvailableVoices() {
        try {
            val voiceList = mutableListOf<SystemVoiceInfo>()
            tts?.voices?.forEach { voice ->
                val loc = voice.locale
                // Filtrar preferentemente voces en español o del sistema
                if (loc.language.startsWith("es") || loc == Locale.getDefault()) {
                    val country = loc.displayCountry.ifBlank { loc.displayName }
                    val shortName = voice.name.substringAfterLast("-").take(8)
                    voiceList.add(
                        SystemVoiceInfo(
                            name = voice.name,
                            displayName = "$country · $shortName",
                            locale = loc.toLanguageTag()
                        )
                    )
                }
            }
            if (voiceList.isNotEmpty()) {
                _availableVoices.value = voiceList.distinctBy { it.name }
            }
        } catch (e: Exception) {
            Log.w("AndroidTtsVoiceEngine", "No se pudieron listar las voces del sistema", e)
        }
    }

    override suspend fun speak(
        text: String,
        pitch: Float,
        speechRate: Float,
        voiceName: String?,
        onStart: () -> Unit,
        onDone: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (!isInitialized || tts == null) {
            initialize {
                scope.launch {
                    speak(text, pitch, speechRate, voiceName, onStart, onDone, onError)
                }
            }
            return
        }

        suspendCancellableCoroutine { continuation ->
            val utteranceId = UUID.randomUUID().toString()

            tts?.setPitch(pitch)
            tts?.setSpeechRate(speechRate)

            // Aplicar voz específica si fue seleccionada por el usuario
            if (!voiceName.isNullOrBlank()) {
                try {
                    val targetVoice = tts?.voices?.find { it.name == voiceName }
                    if (targetVoice != null) {
                        tts?.voice = targetVoice
                    }
                } catch (e: Exception) {
                    Log.w("AndroidTtsVoiceEngine", "No se pudo asignar voz $voiceName", e)
                }
            }

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
        }
    }

    private fun startWaveAnimation() {
        waveAnimationJob?.cancel()
        waveAnimationJob = scope.launch {
            var counter = 0
            while (isActive) {
                val cycle = (counter % 10) / 10f
                val amplitude = 0.5f + (0.5f * kotlin.math.sin(cycle * Math.PI.toFloat()))
                _waveState.value = AudioWaveState(
                    intensity = amplitude,
                    isEmitting = true,
                    statusText = "Transmitiendo"
                )
                counter++
                delay(80)
            }
        }
    }

    private fun stopWaveAnimation() {
        waveAnimationJob?.cancel()
        _waveState.value = AudioWaveState(
            intensity = 0f,
            isEmitting = false,
            statusText = "En espera"
        )
    }

    override fun stop() {
        tts?.stop()
        stopWaveAnimation()
    }

    override fun release() {
        stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}
