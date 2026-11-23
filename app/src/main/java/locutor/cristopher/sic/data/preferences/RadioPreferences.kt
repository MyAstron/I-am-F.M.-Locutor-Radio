package locutor.cristopher.sic.data.preferences

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import locutor.cristopher.sic.domain.model.AnnouncerConfig

class RadioPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("radio_locutor_prefs", Context.MODE_PRIVATE)

    private val _config = MutableStateFlow(loadConfig())
    val config: StateFlow<AnnouncerConfig> = _config.asStateFlow()

    private fun loadConfig(): AnnouncerConfig {
        return AnnouncerConfig(
            isEnabled = prefs.getBoolean("is_enabled", true),
            voiceStyle = prefs.getString("voice_style", "Radio DJ Dinámico") ?: "Radio DJ Dinámico",
            pitch = prefs.getFloat("pitch", 1.0f),
            speechRate = prefs.getFloat("speech_rate", 1.1f),
            scriptTemplate = prefs.getString(
                "script_template",
                "Acabas de escuchar {canción} de {artista}. En I AM-FM son exactamente las {hora en punto}. ¡Seguimos en ruta!"
            ) ?: "Acabas de escuchar {canción} de {artista}. En I AM-FM son exactamente las {hora en punto}. ¡Seguimos en ruta!",
            fadeDownDurationMs = prefs.getLong("fade_down_ms", 1500L),
            fadeUpDurationMs = prefs.getLong("fade_up_ms", 2000L),
            duckingDepthPercent = prefs.getInt("ducking_depth", 15),
            lastAnnouncedHour = prefs.getInt("last_announced_hour", -1)
        )
    }

    fun updateConfig(newConfig: AnnouncerConfig) {
        prefs.edit()
            .putBoolean("is_enabled", newConfig.isEnabled)
            .putString("voice_style", newConfig.voiceStyle)
            .putFloat("pitch", newConfig.pitch)
            .putFloat("speech_rate", newConfig.speechRate)
            .putString("script_template", newConfig.scriptTemplate)
            .putLong("fade_down_ms", newConfig.fadeDownDurationMs)
            .putLong("fade_up_ms", newConfig.fadeUpDurationMs)
            .putInt("ducking_depth", newConfig.duckingDepthPercent)
            .putInt("last_announced_hour", newConfig.lastAnnouncedHour)
            .apply()
        _config.value = newConfig
    }

    fun setEnabled(enabled: Boolean) {
        updateConfig(_config.value.copy(isEnabled = enabled))
    }

    fun setLastAnnouncedHour(hour: Int) {
        updateConfig(_config.value.copy(lastAnnouncedHour = hour))
    }
}
