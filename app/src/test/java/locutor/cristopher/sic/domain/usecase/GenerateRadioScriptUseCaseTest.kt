package locutor.cristopher.sic.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class GenerateRadioScriptUseCaseTest {

    private val useCase = GenerateRadioScriptUseCase()

    @Test
    fun execute_atMinuteZero_outputsEnPuntoWithoutDuplicates() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 14)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        // Caso donde la plantilla tiene "Son {hora} en punto"
        val template = "Acabas de escuchar {cancion} de {artista}. Son {hora} en punto. {clima}"
        val result = useCase.execute(
            template = template,
            trackTitle = "Blinding Lights",
            artist = "The Weeknd",
            weatherReport = "",
            calendar = calendar
        )

        assertTrue("Debe contener el título", result.contains("Blinding Lights"))
        assertTrue("Debe contener el artista", result.contains("The Weeknd"))
        assertTrue("Debe contener las 2 de la tarde en punto", result.contains("las 2 de la tarde en punto"))
        assertFalse("NUNCA debe duplicar 'en punto en punto'", result.contains("en punto en punto"))
    }

    @Test
    fun execute_atMinuteOne_outputsExactMinuteWithoutSayingEnPunto() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 14)
            set(Calendar.MINUTE, 1)
            set(Calendar.SECOND, 20)
        }

        val template = "Son {hora}. ¡Seguimos en ruta!"
        val result = useCase.execute(
            template = template,
            trackTitle = "Song",
            artist = "Artist",
            calendar = calendar
        )

        assertTrue("A las :01 debe decir '1 minuto'", result.contains("las 2 y 1 minuto de la tarde"))
        assertFalse("A las :01 no debe decir 'en punto'", result.contains("en punto"))
    }

    @Test
    fun execute_atMinuteFifteenAndThirty_outputsCuartoAndMedia() {
        val cal15 = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 15)
            set(Calendar.MINUTE, 15)
        }
        val result15 = useCase.execute(
            template = "Son {hora}",
            trackTitle = "Song",
            artist = "Artist",
            calendar = cal15
        )
        assertTrue("A los 15 minutos debe decir 'y cuarto'", result15.contains("las 3 y cuarto de la tarde"))

        val cal30 = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 15)
            set(Calendar.MINUTE, 30)
        }
        val result30 = useCase.execute(
            template = "Son {hora}",
            trackTitle = "Song",
            artist = "Artist",
            calendar = cal30
        )
        assertTrue("A los 30 minutos debe decir 'y media'", result30.contains("las 3 y media de la tarde"))
    }

    @Test
    fun execute_handlesEmptyTrackAndArtistGracefully() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
        }

        val template = "Tema: {cancion} por {artista}"
        val result = useCase.execute(
            template = template,
            trackTitle = "",
            artist = "",
            calendar = calendar
        )

        assertTrue(result.contains("un gran tema"))
        assertTrue(result.contains("la mejor selección"))
    }

    @Test
    fun getNextBroadcastTimeLabel_returnsFormattedHour() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 13)
        }
        val label = useCase.getNextBroadcastTimeLabel(calendar)
        assertEquals("14:00", label)
    }

    @Test
    fun getRemainingSecondsToNextHour_calculatesCorrectInterval() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.MINUTE, 50)
            set(Calendar.SECOND, 0)
        }
        val remaining = useCase.getRemainingSecondsToNextHour(calendar)
        assertEquals(600L, remaining)
    }
}
