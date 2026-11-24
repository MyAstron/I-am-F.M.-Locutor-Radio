package locutor.cristopher.sic.domain.usecase

import java.util.Calendar
import locutor.cristopher.sic.domain.model.AnnouncementScheduleMode

class EvaluateHourlyAnnouncementUseCase {

    /**
     * Evalúa si corresponde emitir el anuncio según el modo configurado por el usuario.
     * Criterios soportados:
     * 1. DEFAULT_TOP_OF_HOUR: En la hora en punto (:00 o primeros 2 minutos) al terminar tema o por reloj.
     * 2. SONG_COUNT: Cada que se completan N canciones reproducidas.
     * 3. EXACT_TIME_INTERRUPT: En la hora en punto interrumpiendo inmediatamente la música en reproducción.
     * 4. CUSTOM_HOURS: En las horas exactas programadas por el usuario (ej. 08:00, 14:00, 20:00).
     */
    fun shouldAnnounce(
        isEnabled: Boolean,
        lastAnnouncedHour: Int,
        scheduleMode: AnnouncementScheduleMode = AnnouncementScheduleMode.DEFAULT_TOP_OF_HOUR,
        customScheduledHours: Set<Int> = setOf(8, 12, 14, 18, 20),
        completedSongsCount: Int = 0,
        songsInterval: Int = 3,
        isTriggeredBySongEnd: Boolean = false,
        calendar: Calendar = Calendar.getInstance()
    ): Boolean {
        if (!isEnabled) {
            return false
        }

        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)
        val isTopOfHour = currentMinute in 0..1

        return when (scheduleMode) {
            AnnouncementScheduleMode.DEFAULT_TOP_OF_HOUR -> {
                isTopOfHour && (currentHour != lastAnnouncedHour)
            }
            AnnouncementScheduleMode.SONG_COUNT -> {
                isTriggeredBySongEnd && (completedSongsCount >= songsInterval)
            }
            AnnouncementScheduleMode.EXACT_TIME_INTERRUPT -> {
                isTopOfHour && (currentHour != lastAnnouncedHour)
            }
            AnnouncementScheduleMode.CUSTOM_HOURS -> {
                customScheduledHours.contains(currentHour) && isTopOfHour && (currentHour != lastAnnouncedHour)
            }
        }
    }

    /**
     * Sobrecarga de conveniencia para evaluación básica horaria.
     */
    fun shouldAnnounce(
        isEnabled: Boolean,
        lastAnnouncedHour: Int,
        calendar: Calendar = Calendar.getInstance()
    ): Boolean {
        return shouldAnnounce(
            isEnabled = isEnabled,
            lastAnnouncedHour = lastAnnouncedHour,
            scheduleMode = AnnouncementScheduleMode.DEFAULT_TOP_OF_HOUR,
            calendar = calendar
        )
    }

    /**
     * Sobrecarga de compatibilidad anterior.
     */
    fun shouldAnnounce(
        isEnabled: Boolean,
        hasCompletedTrack: Boolean,
        lastAnnouncedHour: Int,
        calendar: Calendar = Calendar.getInstance()
    ): Boolean {
        return shouldAnnounce(
            isEnabled = isEnabled,
            lastAnnouncedHour = lastAnnouncedHour,
            scheduleMode = AnnouncementScheduleMode.DEFAULT_TOP_OF_HOUR,
            calendar = calendar
        )
    }

    fun getCurrentHour(calendar: Calendar = Calendar.getInstance()): Int {
        return calendar.get(Calendar.HOUR_OF_DAY)
    }
}
