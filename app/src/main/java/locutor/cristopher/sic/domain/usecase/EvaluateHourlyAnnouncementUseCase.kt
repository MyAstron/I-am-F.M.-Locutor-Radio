package locutor.cristopher.sic.domain.usecase

import java.util.Calendar

class EvaluateHourlyAnnouncementUseCase {

    /**
     * Evalúa si corresponde emitir la locución horaria.
     * Criterio:
     * 1. El locutor debe estar habilitado (isEnabled == true).
     * 2. Hubo un cierre de canción previo con información válida.
     * 3. La hora actual está en punto (minuto 0 o dentro de los primeros 2 minutos)
     *    y dicha hora no fue anunciada previamente en este ciclo.
     */
    fun shouldAnnounce(
        isEnabled: Boolean,
        hasCompletedTrack: Boolean,
        lastAnnouncedHour: Int,
        calendar: Calendar = Calendar.getInstance()
    ): Boolean {
        if (!isEnabled || !hasCompletedTrack) {
            return false
        }

        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        // Se considera momento en punto si el minuto es 0 o 1 (ventana de cierre de canción)
        val isTopOfHour = currentMinute in 0..1

        return isTopOfHour && (currentHour != lastAnnouncedHour)
    }

    /**
     * Retorna la hora actual del sistema en formato entero de 24 horas.
     */
    fun getCurrentHour(calendar: Calendar = Calendar.getInstance()): Int {
        return calendar.get(Calendar.HOUR_OF_DAY)
    }
}
