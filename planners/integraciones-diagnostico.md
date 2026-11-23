### 1. Botón "Vincular" estilo Waze y selección de servicio musical

* En lugar de que el usuario lo haga a mano o quede sin vincular, agrega un botón **"Vincular"** visible en la barra/tarjeta de reproducción (como Waze) que abra un diálogo/sheet con las apps de música detectadas o instaladas (Spotify, YouTube Music, Apple Music, MP3, etc.) y guarde la app vinculada.
* Mientras no haya una app vinculada o si el usuario usa la sesión activa actual, mantén habilitados **Anterior**, **Reproducir/Pausar** y **Siguiente** enlazados al `MediaController` activo actual para que funcionen desde el inicio.

### 2. Fade solo sobre la música (no sobre el volumen general del teléfono)

* **No toques `STREAM_RING` / volumen general del teléfono** ni `setStreamVolume` global si baja el volumen del sistema; para lograr el efecto tipo WhatsApp (baja la música mientras suena la voz sin tocar el volumen general del teléfono):
* Solicita foco de audio transitorio con `AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK` usando `AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE` y `AudioAttributes.CONTENT_TYPE_SPEECH` (o en Android 12+/API 31+ `AudioFocusRequest.Builder(...).setWillPauseWhenDucked(false)`), dejando que la app de música haga *ducking* de su propio flujo de audio, o aplica una reducción gradual únicamente sobre el reproductor/foco de música sin alterar el volumen general del teléfono.
* Al terminar la locución, abandona el foco (`abandonAudioFocusRequest`) para que la música regrese a su nivel normal.



### 3. Efecto visual de ondas con cambio de ícono (Locutor vs. Canción)

* Mantén el mismo efecto de ondas/anillos reactivos alrededor del botón central tanto cuando habla el locutor como cuando suena una canción, pero alterna el ícono central según la fuente activa:
* **Cuando habla el locutor (`isSpeaking == true`):** muestra el **ícono de micrófono** con las ondas activas.
* **Cuando es música/canción (`isSpeaking == false` y reproduciendo):** muestra un **ícono diferente de música/nota musical** (o el ícono de `stitch/ICON .PNG`) manteniendo el mismo efecto visual de ondas.



### 4. Nota de compilación (JDK / Gradle) para el plan CLI

* Si vuelve a fallar la compilación por ruta de JDK inexistente tras un daemon huérfano, detener daemons (`./gradlew --stop` y limpiar `KotlinCompileDaemon` si hace falta) y mantener `compileOptions` en `JavaVersion.VERSION_17` con `kotlin { jvmToolchain(21) }` en `app/build.gradle.kts` evita que Kotlin busque una ruta desincronizada de JDK 21.