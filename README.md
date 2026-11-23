# I AM-FM: Locutor Radio

[![Kotlin](https://img.shields.io/badge/Kotlin-Android-7F52FF?style=flat&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-UI-4285F4?style=flat&logo=jetpackcompose&logoColor=white)](https://developer.android.com/compose)
[![Android](https://img.shields.io/badge/Platform-Android%2026%2B-3DDC84?style=flat&logo=android&logoColor=white)](https://developer.android.com/)
[![CLI Workflow](https://img.shields.io/badge/Workflow-CLI-0F172A?style=flat&logo=gnubash&logoColor=white)](#)

## Descripcion del Proyecto
I AM-FM: Locutor Radio es una aplicacion nativa para Android (Kotlin y Jetpack Compose) con interfaz estilo Waze que controla de forma basica la reproduccion activa de otras apps de musica en el telefono (sin exigir cuentas Premium ni integrar APIs musicales cerradas) e incorpora un locutor por voz que interviene al terminar una cancion cuando coincide con una hora exacta, aplicando ducking nativo transitorio exclusivo para la musica sin alterar el volumen general del dispositivo.

## Especificaciones de Uso y Mantenimiento
- **Plataforma y compilacion:** Android (minSdk 26), desarrollado y compilado mediante flujo CLI con Android SDK y Gradle (JavaVersion 17 con jvmToolchain 21).
- **Referencia visual e icono:** Basado en los archivos HTML de stitch/ y en el icono oficial stitch/icon.png integrado en los drawables de la aplicacion.
- **Boton Vincular estilo Waze y seleccion musical:** Boton visible en la tarjeta de reproduccion que despliega una lista de aplicaciones de musica instaladas y detectadas (Spotify, YouTube Music, Apple Music, Deezer, etc.) con etiqueta de estado e inicio rapido, manteniendo activos los controles de Anterior, Reproducir/Pausar y Siguiente mediante MediaController o eventos directos.
- **Ducking exclusivo sobre musica:** Utiliza foco de audio transitorio (AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK con USAGE_ASSISTANCE_NAVIGATION_GUIDANCE y CONTENT_TYPE_SPEECH) para atenuar la musica durante la locucion sin tocar el volumen del sistema ni modificar STREAM_RING o STREAM_MUSIC global.
- **Ondas reactivas con alternancia de icono:** Anillos de sonido concentricos activos tanto durante la locucion como en reproduccion musical, alternando dinamicamente entre el icono de microfono (Locutor en vivo), ecualizador grafico (Musica en ruta) y radio/apagado.
- **Voz, frases y clima:** Soporta variaciones de voz, catalogo CRUD de frases sin repetir la ultima usada y mencion de clima local unicamente con permiso explicito del usuario.
- **Mantenimiento:** Mantener desacoplados el enlace de sesion multimedia, el controlador de ducking, el repositorio CRUD de frases, el motor de voz y el proveedor opcional de clima.

## Guia de Usuario y Funcionamiento General
1. **Permisos y boton Vincular:** Concede acceso a notificaciones y pulsa el boton Vincular en la tarjeta de la cabina para seleccionar o abrir tu reproductor preferido (detectado automaticamente).
2. **Controles Waze:** Controla Anterior, Reproducir/Pausar y Siguiente de inmediato en cualquier sesion multimedia activa.
3. **Boton central y ondas reactivas:** Observa los anillos de audio continuos tanto cuando suena una cancion (icono de musica) como cuando el locutor habla (icono de microfono), cambiando de estado de forma reactiva.
4. **Ducking limpio tipo WhatsApp/Waze:** Al dar la hora o emitir una locucion, la musica baja de volumen suavemente sin alterar la barra de volumen general del telefono, restaurandose al culminar.
5. **Menu de frases (CRUD) y clima:** Personaliza tus locuciones con variables dinamicas sin repeticiones consecutivas y anade clima local si otorgas el permiso correspondiente.

## Implementacion Tecnica
- **Enlace y control de sesiones:** MusicServiceLinker y MediaSessionRepository gestionan la deteccion de aplicaciones instaladas (packageManager.getLaunchIntentForPackage) y operan sobre MediaController y dispatchMediaKeyEvent.
- **Ducking nativo sin impacto en volumen global:** AudioFadeController solicita foco de audio transitorio con atenuacion voluntaria (setWillPauseWhenDucked(false)) y restaura el nivel al finalizar con abandonAudioFocusRequest, evitando mutaciones directas sobre setStreamVolume.
- **Modulacion visual y conmutacion de iconos:** SoundWaveRing y CentralAnnouncerPowerButton coordinan las animaciones concentricas para senales de voz y de musica, alternando los iconos de microfono, ecualizador musical y radio.
- **CRUD y rotacion de frases:** PhraseCrudRepository almacena las frases del usuario y selecciona una distinta a la anterior (lastUsedPhraseId), integrando variables dinamicas.
- **Clima local condicionado a permiso:** LocalWeatherProvider evalua los permisos de localizacion antes de consultar las condiciones meteorologicas, omitiendo limpiamente la variable si el permiso no esta concedido.

## Manual de Desarrollo
Pendiente de redactar al finalizar todas las fases del proyecto.

### Cambio Principal de Esta Version (Evaluacion Progresiva)
- **Que era:** Una version con control de audio que intentaba ajustar el volumen global del sistema con setStreamVolume, sin deteccion de apps instaladas ni boton Vincular estilo Waze, y con ondas visuales que solo reaccionaban al locutor y no a la musica en reproduccion.
- **Que es:** Una cabina completa estilo Waze con boton Vincular y selector de reproductores detectados/instalados, controles basicos siempre funcionales, ducking nativo transitorio exclusivo para musica (sin alterar el volumen general del dispositivo), ondas reactivas continuas para voz y musica con alternancia dinamica de icono (microfono vs ecualizador), CRUD completo de frases sin repeticion y clima local opcional.
- **Que sera:** En la siguiente iteracion, soporte de sincronizacion con APIs meteorologicas remotas en tiempo real y conexion con modelos de lenguaje ligeros en el dispositivo para generar comentarios contextuales de trafico o anecdotas musicales.

## Creditos
- **Autor:** MyAstron
- **Agentes de Inteligencia Artificial:**
  - Stitch
  - Gemini
  - Antigravity (CLI)
  - AI Studio
