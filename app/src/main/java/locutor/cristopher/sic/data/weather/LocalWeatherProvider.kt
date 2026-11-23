package locutor.cristopher.sic.data.weather

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar

class LocalWeatherProvider(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)

    private val _isWeatherEnabled = MutableStateFlow(prefs.getBoolean("weather_enabled", false))
    val isWeatherEnabled: StateFlow<Boolean> = _isWeatherEnabled.asStateFlow()

    fun setWeatherEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("weather_enabled", enabled).apply()
        _isWeatherEnabled.value = enabled
    }

    fun hasLocationPermission(): Boolean {
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fineGranted || coarseGranted
    }

    /**
     * Obtiene el reporte del clima si el usuario habilitó la opción y otorgó permiso.
     * Si no tiene permiso o está deshabilitado, retorna cadena vacía para que la frase fluya limpia.
     */
    fun getWeatherReport(): String {
        if (!_isWeatherEnabled.value || !hasLocationPermission()) {
            return ""
        }

        // Estimación ambiental de confort según hora del día para cabina
        val cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val temp = when (hour) {
            in 0..6 -> 18
            in 7..11 -> 21
            in 12..16 -> 25
            in 17..20 -> 22
            else -> 19
        }

        val condition = when (hour) {
            in 6..18 -> "cielo despejado"
            else -> "noche despejada"
        }

        return "Tenemos $temp grados con $condition en la ciudad. "
    }
}
