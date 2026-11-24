package locutor.cristopher.sic.domain.usecase

import locutor.cristopher.sic.domain.model.AnnouncementScheduleMode
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class EvaluateHourlyAnnouncementUseCaseTest {

    private val useCase = EvaluateHourlyAnnouncementUseCase()

    @Test
    fun shouldAnnounce_triggersAtTopOfHourEvenWithoutCompletedTrack() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 15)
            set(Calendar.MINUTE, 0)
        }

        val result = useCase.shouldAnnounce(
            isEnabled = true,
            hasCompletedTrack = false, // Sin canciones colocadas
            lastAnnouncedHour = 14,
            calendar = calendar
        )

        assertTrue("Debe sonar a la hora en punto incluso si no hay canciones sonando", result)
    }

    @Test
    fun shouldAnnounce_doesNotRepeatInSameHour() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 15)
            set(Calendar.MINUTE, 1)
        }

        val result = useCase.shouldAnnounce(
            isEnabled = true,
            lastAnnouncedHour = 15, // Ya fue anunciado en esta hora
            calendar = calendar
        )

        assertFalse("No debe repetir el anuncio en la misma hora", result)
    }

    @Test
    fun shouldAnnounce_doesNotAnnounceWhenDisabled() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 16)
            set(Calendar.MINUTE, 0)
        }

        val result = useCase.shouldAnnounce(
            isEnabled = false,
            lastAnnouncedHour = 15,
            calendar = calendar
        )

        assertFalse("No debe anunciar si el locutor está apagado", result)
    }

    @Test
    fun shouldAnnounce_songCountMode_triggersOnlyWhenThresholdReached() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.MINUTE, 34) // Minuto arbitrario, no en punto
        }

        // Caso 1: Aún no llega al número requerido de temas (2 de 3)
        val notYet = useCase.shouldAnnounce(
            isEnabled = true,
            lastAnnouncedHour = -1,
            scheduleMode = AnnouncementScheduleMode.SONG_COUNT,
            completedSongsCount = 2,
            songsInterval = 3,
            isTriggeredBySongEnd = true,
            calendar = calendar
        )
        assertFalse("No debe sonar si lleva 2 canciones y el intervalo es de 3", notYet)

        // Caso 2: Alcanzó el umbral de 3 canciones
        val reached = useCase.shouldAnnounce(
            isEnabled = true,
            lastAnnouncedHour = -1,
            scheduleMode = AnnouncementScheduleMode.SONG_COUNT,
            completedSongsCount = 3,
            songsInterval = 3,
            isTriggeredBySongEnd = true,
            calendar = calendar
        )
        assertTrue("Debe sonar al alcanzar las 3 canciones", reached)
    }

    @Test
    fun shouldAnnounce_customHoursMode_triggersOnlyAtConfiguredHours() {
        val customHours = setOf(8, 12, 18)

        // Hora 10:00 (No programada)
        val cal10 = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 10)
            set(Calendar.MINUTE, 0)
        }
        val result10 = useCase.shouldAnnounce(
            isEnabled = true,
            lastAnnouncedHour = 9,
            scheduleMode = AnnouncementScheduleMode.CUSTOM_HOURS,
            customScheduledHours = customHours,
            calendar = cal10
        )
        assertFalse("No debe sonar a las 10:00 porque no está en las horas personalizadas", result10)

        // Hora 12:00 (Sí programada)
        val cal12 = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 0)
        }
        val result12 = useCase.shouldAnnounce(
            isEnabled = true,
            lastAnnouncedHour = 11,
            scheduleMode = AnnouncementScheduleMode.CUSTOM_HOURS,
            customScheduledHours = customHours,
            calendar = cal12
        )
        assertTrue("Debe sonar a las 12:00 porque está en las horas programadas", result12)
    }
}
