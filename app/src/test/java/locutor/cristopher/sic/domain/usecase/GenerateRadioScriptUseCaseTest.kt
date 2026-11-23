package locutor.cristopher.sic.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class GenerateRadioScriptUseCaseTest {

    private val useCase = GenerateRadioScriptUseCase()

    @Test
    fun execute_replacesDynamicTagsProperly() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 14)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        val template = "Acabas de escuchar {cancion} de {artista}. Son {hora}. {clima}"
        val result = useCase.execute(
            template = template,
            trackTitle = "Blinding Lights",
            artist = "The Weeknd",
            weatherReport = "Cielo despejado con 24 grados",
            calendar = calendar
        )

        assertTrue(result.contains("Blinding Lights"))
        assertTrue(result.contains("The Weeknd"))
        assertTrue(result.contains("Cielo despejado con 24 grados"))
        assertTrue(result.contains("las 2 de la tarde en punto"))
    }

    @Test
    fun execute_handlesEmptyTrackAndArtistGracefully() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 9)
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
