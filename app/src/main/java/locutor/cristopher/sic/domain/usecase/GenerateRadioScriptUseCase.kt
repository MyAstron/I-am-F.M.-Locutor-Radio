package locutor.cristopher.sic.domain.usecase

import java.util.Calendar
import java.util.Locale

class GenerateRadioScriptUseCase {

    fun execute(
        template: String,
        trackTitle: String,
        artist: String,
        weatherReport: String = "",
        calendar: Calendar = Calendar.getInstance()
    ): String {
        val hour24 = calendar.get(Calendar.HOUR_OF_DAY)
        val hour12 = if (calendar.get(Calendar.HOUR) == 0) 12 else calendar.get(Calendar.HOUR)
        val minute = calendar.get(Calendar.MINUTE)
        val amPm = if (calendar.get(Calendar.AM_PM) == Calendar.AM) "de la mañana" else if (hour24 < 19) "de la tarde" else "de la noche"

        // Locución horaria natural y exacta: revisa el minuto exacto
        val formattedHourVoice = when {
            minute == 0 -> {
                if (hour12 == 1) "la 1 $amPm en punto" else "las $hour12 $amPm en punto"
            }
            minute == 1 -> {
                if (hour12 == 1) "la 1 y 1 minuto $amPm" else "las $hour12 y 1 minuto $amPm"
            }
            minute == 15 -> {
                if (hour12 == 1) "la 1 y cuarto $amPm" else "las $hour12 y cuarto $amPm"
            }
            minute == 30 -> {
                if (hour12 == 1) "la 1 y media $amPm" else "las $hour12 y media $amPm"
            }
            minute == 45 -> {
                val nextHour = if (hour12 == 12) 1 else hour12 + 1
                val article = if (nextHour == 1) "la" else "las"
                "$article $nextHour menos cuarto $amPm"
            }
            else -> {
                if (hour12 == 1) "la 1 y $minute $amPm" else "las $hour12 y $minute $amPm"
            }
        }

        val cleanTitle = if (trackTitle.isNotBlank()) trackTitle else "un gran tema"
        val cleanArtist = if (artist.isNotBlank()) artist else "la mejor selección"

        var cleanTemplate = template

        // Si el minuto no es 00 y la plantilla contiene "{hora} en punto", remover "en punto" para no contradecir
        if (minute != 0) {
            cleanTemplate = cleanTemplate
                .replace("{hora} en punto", "{hora}")
                .replace("{hora en punto} en punto", "{hora}")
                .replace("{hora en punto}", "{hora}")
        }

        var result = cleanTemplate
            .replace("{canción}", cleanTitle)
            .replace("{cancion}", cleanTitle)
            .replace("{artista}", cleanArtist)
            .replace("{hora en punto}", formattedHourVoice)
            .replace("{hora}", formattedHourVoice)
            .replace("{clima}", weatherReport)

        // Limpieza estricta contra repetición de "en punto en punto"
        result = result
            .replace("en punto en punto", "en punto")
            .replace("en punto, en punto", "en punto")
            .replace("en punto . en punto", "en punto")
            .replace("  ", " ")
            .trim()

        return result
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
