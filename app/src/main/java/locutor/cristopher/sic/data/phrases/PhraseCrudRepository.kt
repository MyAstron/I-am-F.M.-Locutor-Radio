package locutor.cristopher.sic.data.phrases

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class PhraseCrudRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("radio_phrases_prefs", Context.MODE_PRIVATE)

    private val _phrases = MutableStateFlow<List<RadioPhrase>>(emptyList())
    val phrases: StateFlow<List<RadioPhrase>> = _phrases.asStateFlow()

    private var lastUsedPhraseId: String? = null

    init {
        loadPhrases()
    }

    private fun loadPhrases() {
        val savedJson = prefs.getString("phrases_json", null)
        if (savedJson != null) {
            try {
                val array = JSONArray(savedJson)
                val list = mutableListOf<RadioPhrase>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    // Sanear posibles plantillas antiguas con doble "en punto"
                    val rawTemplate = obj.getString("template")
                    val cleanTemplate = rawTemplate
                        .replace("{hora} en punto", "{hora}")
                        .replace("{hora en punto} en punto", "{hora}")
                    list.add(
                        RadioPhrase(
                            id = obj.getString("id"),
                            template = cleanTemplate,
                            isActive = obj.optBoolean("isActive", true),
                            createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                        )
                    )
                }
                if (list.isNotEmpty()) {
                    _phrases.value = list
                    return
                }
            } catch (_: Exception) {
                // Si falla lectura, carga predeterminadas
            }
        }
        // Frases iniciales variadas con estilo cabina de radio FM sin redundancias
        _phrases.value = getDefaultPhrases()
        savePhrases()
    }

    private fun getDefaultPhrases(): List<RadioPhrase> {
        return listOf(
            RadioPhrase(
                template = "Acabas de escuchar {cancion} de {artista}. En I AM-FM son exactamente {hora}. {clima}¡Seguimos en ruta!",
                isActive = true
            ),
            RadioPhrase(
                template = "Eso fue {cancion} con {artista}. La hora en cabina: {hora}. {clima}¡Buena música en tu camino!",
                isActive = true
            ),
            RadioPhrase(
                template = "Sonando en I AM-FM: terminaba {cancion} de {artista}. Reloj marca {hora}. {clima}Acompáñanos en la frecuencia.",
                isActive = true
            ),
            RadioPhrase(
                template = "Gran tema: {cancion} por {artista}. Son {hora}. {clima}Maneja con precaución y quédate con nosotros.",
                isActive = true
            )
        )
    }

    private fun savePhrases() {
        val array = JSONArray()
        _phrases.value.forEach { phrase ->
            val obj = JSONObject().apply {
                put("id", phrase.id)
                put("template", phrase.template)
                put("isActive", phrase.isActive)
                put("createdAt", phrase.createdAt)
            }
            array.put(obj)
        }
        prefs.edit().putString("phrases_json", array.toString()).apply()
    }

    // CRUD: Create
    fun addPhrase(template: String): RadioPhrase {
        val clean = template.trim()
            .replace("{hora} en punto", "{hora}")
            .replace("{hora en punto} en punto", "{hora}")
        val newPhrase = RadioPhrase(template = clean, isActive = true)
        _phrases.value = _phrases.value + newPhrase
        savePhrases()
        return newPhrase
    }

    // CRUD: Update
    fun updatePhrase(id: String, newTemplate: String, isActive: Boolean) {
        val clean = newTemplate.trim()
            .replace("{hora} en punto", "{hora}")
            .replace("{hora en punto} en punto", "{hora}")
        _phrases.value = _phrases.value.map {
            if (it.id == id) it.copy(template = clean, isActive = isActive) else it
        }
        savePhrases()
    }

    // CRUD: Toggle Active
    fun togglePhraseActive(id: String) {
        _phrases.value = _phrases.value.map {
            if (it.id == id) it.copy(isActive = !it.isActive) else it
        }
        savePhrases()
    }

    // CRUD: Delete
    fun deletePhrase(id: String) {
        _phrases.value = _phrases.value.filter { it.id != id }
        if (_phrases.value.isEmpty()) {
            _phrases.value = getDefaultPhrases()
        }
        savePhrases()
    }

    /**
     * Sincroniza las frases cuando se activa o desactiva el clima.
     * Si el clima se desactiva, se desactivan automáticamente las frases que incluyan {clima}.
     */
    fun onWeatherSettingChanged(isWeatherEnabled: Boolean) {
        if (!isWeatherEnabled) {
            _phrases.value = _phrases.value.map { phrase ->
                if (phrase.template.contains("{clima}", ignoreCase = true) || phrase.template.contains("clima", ignoreCase = true)) {
                    phrase.copy(isActive = false)
                } else {
                    phrase
                }
            }
            savePhrases()
        }
    }

    /**
     * Selección de frase con algoritmo anti-repetición consecutiva
     * y filtrado condicional por estado del clima.
     */
    fun getNextNonRepeatingPhrase(weatherEnabled: Boolean = true): RadioPhrase {
        var activePhrases = _phrases.value.filter { it.isActive }

        // Si el clima está desactivado, filtrar frases que contengan etiquetas de clima
        if (!weatherEnabled) {
            activePhrases = activePhrases.filter {
                !it.template.contains("{clima}", ignoreCase = true)
            }
        }

        val pool = if (activePhrases.isNotEmpty()) {
            activePhrases
        } else {
            // Si todas tenían clima o estaban inactivas, tomar del total filtrando clima si está deshabilitado
            val fallbackPool = if (!weatherEnabled) {
                _phrases.value.filter { !it.template.contains("{clima}", ignoreCase = true) }
            } else {
                _phrases.value
            }
            if (fallbackPool.isNotEmpty()) fallbackPool else getDefaultPhrases()
        }

        if (pool.size <= 1) {
            val single = pool.firstOrNull() ?: getDefaultPhrases().first()
            lastUsedPhraseId = single.id
            return single
        }

        val candidates = pool.filter { it.id != lastUsedPhraseId }
        val selected = (if (candidates.isNotEmpty()) candidates else pool).random()
        lastUsedPhraseId = selected.id
        return selected
    }
}
