package locutor.cristopher.sic.data.preferences

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import locutor.cristopher.sic.domain.model.AnnouncementScheduleMode
import locutor.cristopher.sic.domain.model.AnnouncerConfig
import locutor.cristopher.sic.domain.model.BackgroundServiceMode

class RadioPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("radio_locutor_prefs", Context.MODE_PRIVATE)

    private val _config = MutableStateFlow(loadConfig())
    val config: StateFlow<AnnouncerConfig> = _config.asStateFlow()

    private fun loadConfig(): AnnouncerConfig {
        val scheduleModeStr = prefs.getString("schedule_mode", AnnouncementScheduleMode.DEFAULT_TOP_OF_HOUR.name)
        val scheduleMode = try {
            AnnouncementScheduleMode.valueOf(scheduleModeStr ?: AnnouncementScheduleMode.DEFAULT_TOP_OF_HOUR.name)
        } catch (_: Exception) {
            AnnouncementScheduleMode.DEFAULT_TOP_OF_HOUR
        }

        val bgModeStr = prefs.getString("background_service_mode", BackgroundServiceMode.AUTO.name)
        val bgMode = try {
            BackgroundServiceMode.valueOf(bgModeStr ?: BackgroundServiceMode.AUTO.name)
        } catch (_: Exception) {
            BackgroundServiceMode.AUTO
        }

        val hoursRaw = prefs.getStringSet("custom_scheduled_hours", setOf("8", "12", "14", "18", "20")) ?: setOf("8", "12", "14", "18", "20")
        val customHours = hoursRaw.mapNotNull { it.toIntOrNull() }.toSet()

        return AnnouncerConfig(
            isEnabled = prefs.getBoolean("is_enabled", true),
            voiceStyle = prefs.getString("voice_style", "Radio DJ Dinámico") ?: "Radio DJ Dinámico",
            selectedVoiceName = prefs.getString("selected_voice_name", "") ?: "",
            pitch = prefs.getFloat("pitch", 1.1f),
            speechRate = prefs.getFloat("speech_rate", 1.15f),
            scriptTemplate = prefs.getString(
                "script_template",
                "Acabas de escuchar {canción} de {artista}. En I AM-FM son exactamente {hora}. ¡Seguimos en ruta!"
            ) ?: "Acabas de escuchar {canción} de {artista}. En I AM-FM son exactamente {hora}. ¡Seguimos en ruta!",
            fadeDownDurationMs = prefs.getLong("fade_down_ms", 1500L),
            fadeUpDurationMs = prefs.getLong("fade_up_ms", 2000L),
            duckingDepthPercent = prefs.getInt("ducking_depth", 15),
            lastAnnouncedHour = prefs.getInt("last_announced_hour", -1),
            scheduleMode = scheduleMode,
            songsInterval = prefs.getInt("songs_interval", 3),
            customScheduledHours = if (customHours.isNotEmpty()) customHours else setOf(8, 12, 14, 18, 20),
            backgroundServiceMode = bgMode
        )
    }

    fun updateConfig(newConfig: AnnouncerConfig) {
        val hoursStringSet = newConfig.customScheduledHours.map { it.toString() }.toSet()
        prefs.edit()
            .putBoolean("is_enabled", newConfig.isEnabled)
            .putString("voice_style", newConfig.voiceStyle)
            .putString("selected_voice_name", newConfig.selectedVoiceName)
            .putFloat("pitch", newConfig.pitch)
            .putFloat("speech_rate", newConfig.speechRate)
            .putString("script_template", newConfig.scriptTemplate)
            .putLong("fade_down_ms", newConfig.fadeDownDurationMs)
            .putLong("fade_up_ms", newConfig.fadeUpDurationMs)
            .putInt("ducking_depth", newConfig.duckingDepthPercent)
            .putInt("last_announced_hour", newConfig.lastAnnouncedHour)
            .putString("schedule_mode", newConfig.scheduleMode.name)
            .putInt("songs_interval", newConfig.songsInterval)
            .putStringSet("custom_scheduled_hours", hoursStringSet)
            .putString("background_service_mode", newConfig.backgroundServiceMode.name)
            .apply()
        _config.value = newConfig
    }

    fun setEnabled(enabled: Boolean) {
        updateConfig(_config.value.copy(isEnabled = enabled))
    }

    fun setLastAnnouncedHour(hour: Int) {
        updateConfig(_config.value.copy(lastAnnouncedHour = hour))
    }

    fun setBackgroundServiceMode(mode: BackgroundServiceMode) {
        updateConfig(_config.value.copy(backgroundServiceMode = mode))
    }

    fun setScheduleMode(mode: AnnouncementScheduleMode) {
        updateConfig(_config.value.copy(scheduleMode = mode))
    }

    fun setSongsInterval(interval: Int) {
        updateConfig(_config.value.copy(songsInterval = interval))
    }

    fun setCustomScheduledHours(hours: Set<Int>) {
        updateConfig(_config.value.copy(customScheduledHours = hours))
    }
}
