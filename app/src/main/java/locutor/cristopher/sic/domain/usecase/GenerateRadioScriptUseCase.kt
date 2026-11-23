package locutor.cristopher.sic.domain.usecase

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class GenerateRadioScriptUseCase {

    fun execute(
        template: String,
        trackTitle: String,
        artist: String,
        calendar: Calendar = Calendar.getInstance()
    ): String {
        val hour24 = calendar.get(Calendar.HOUR_OF_DAY)
        val hour12 = if (calendar.get(Calendar.HOUR) == 0) 12 else calendar.get(Calendar.HOUR)
        val amPm = if (calendar.get(Calendar.AM_PM) == Calendar.AM) "de la mañana" else if (hour24 < 19) "de la tarde" else "de la noche"

        val formattedHourVoice = if (hour12 == 1) {
            "la 1 $amPm en punto"
        } else {
            "las $hour12 $amPm en punto"
        }

        val cleanTitle = if (trackTitle.isNotBlank()) trackTitle else "un gran tema"
        val cleanArtist = if (artist.isNotBlank()) artist else "la mejor selección"

        return template
            .replace("{canción}", cleanTitle)
            .replace("{cancion}", cleanTitle)
            .replace("{artista}", cleanArtist)
            .replace("{hora en punto}", formattedHourVoice)
            .replace("{hora}", formattedHourVoice)
    }

    fun getNextBroadcastTimeLabel(calendar: Calendar = Calendar.getInstance()): String {
        val nextHour = (calendar.get(Calendar.HOUR_OF_DAY) + 1) % 24
        return String.format(Locale.getDefault(), "%02d:00", nextHour)
    }

    fun getRemainingSecondsToNextHour(calendar: Calendar = Calendar.getInstance()): Long {
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)
        val elapsedSeconds = (minute * 60) + second
        val totalSecondsInHour = 3600
        return (totalSecondsInHour - elapsedSeconds).toLong().coerceAtLeast(0L)
    }
}
