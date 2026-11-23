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
- **Acceso ubicuo a Vincular (3 Puntos Clave):** Boton o chip Vincular disponible en la barra superior (TopAppBar), en la cabecera de la tarjeta estilo Waze y en la tarjeta destacada de la pantalla de Servicio.
- **Metadatos y caratula en vivo:** Lee y presenta titulo, artista, album y caratula oficial (Bitmap directo o URI decodificado) de YouTube Music, Spotify, Apple Music y reproductores locales.
- **Sincronizacion fidedigna de Play y Pausa:** Estado derivado estrictamente de PlaybackState en tiempo real con invocacion a transportControls.pause() y transportControls.play(), eliminando desincronizaciones de estado invertido.
- **Ducking exclusivo sobre musica:** Utiliza foco de audio transitorio (AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK con USAGE_ASSISTANCE_NAVIGATION_GUIDANCE y CONTENT_TYPE_SPEECH) para atenuar la musica durante la locucion sin tocar el volumen del sistema ni modificar STREAM_RING o STREAM_MUSIC global.
- **Ondas reactivas con alternancia de icono:** Anillos de sonido concentricos activos tanto durante la locucion como en reproduccion musical, alternando dinamicamente entre el icono de microfono (Locutor en vivo), ecualizador grafico (Musica en ruta) y radio/apagado.
- **Voz, frases y clima:** Soporta variaciones de voz, catalogo CRUD de frases sin repetir la ultima usada y mencion de clima local unicamente con permiso explicito del usuario.
- **Mantenimiento:** Mantener desacoplados el enlace de sesion multimedia, el controlador de ducking, el repositorio CRUD de frases, el motor de voz y el proveedor opcional de clima.

## Guia de Usuario y Funcionamiento General
1. **Permisos y vinculacion ubicua:** Concede acceso a notificaciones y pulsa Vincular desde la barra superior, la tarjeta Waze o la pestana de Servicio para seleccionar o abrir tu reproductor preferido (Spotify, YouTube Music, etc.).
2. **Caratula y metadatos en directo:** Disfruta de la visualizacion del album y la caratula original en la tarjeta de reproduccion.
3. **Controles sincronizados:** Controla Anterior, Pausa/Reproduccion y Siguiente sabiendo que el boton siempre muestra el estado veridico de tu app de musica.
4. **Boton central y ondas reactivas:** Observa los anillos de audio continuos tanto cuando suena una cancion (icono de musica) como cuando el locutor habla (icono de microfono), cambiando de estado de forma reactiva.
5. **Ducking limpio tipo WhatsApp/Waze:** Al dar la hora o emitir una locucion, la musica baja de volumen suavemente sin alterar la barra de volumen general del telefono, restaurandose al culminar.
6. **Menu de frases (CRUD) y clima:** Personaliza tus locuciones con variables dinamicas sin repeticiones consecutivas y anade clima local si otorgas el permiso correspondiente.

## Implementacion Tecnica
- **Enlace y control de sesiones:** MusicServiceLinker y MediaSessionRepository gestionan la deteccion de aplicaciones instaladas, el enlace prioritario de MediaController y la lectura de metadatos (titulo, artista, album y caratula Bitmap/URI).
- **Control idempotente de reproduccion:** MediaSessionRepository evalua el PlaybackState.STATE_PLAYING del MediaController para ejecutar pause() o play() sin invertir estados localmente, sincronizandose con onPlaybackStateChanged.
- **Ducking nativo sin impacto en volumen global:** AudioFadeController solicita foco de audio transitorio con atenuacion voluntaria (setWillPauseWhenDucked(false)) y restaura el nivel al finalizar con abandonAudioFocusRequest, evitando mutaciones directas sobre setStreamVolume.
- **Modulacion visual y conmutacion de iconos:** SoundWaveRing y CentralAnnouncerPowerButton coordinan las animaciones concentricas para senales de voz y de musica, alternando los iconos de microfono, ecualizador musical y radio.
- **CRUD y rotacion de frases:** PhraseCrudRepository almacena las frases del usuario y selecciona una distinta a la anterior (lastUsedPhraseId), integrando variables dinamicas.
- **Clima local condicionado a permiso:** LocalWeatherProvider evalua los permisos de localizacion antes de consultar las condiciones meteorologicas, omitiendo limpiamente la variable si el permiso no esta concedido.

## Manual de Desarrollo
Pendiente de redactar al finalizar todas las fases del proyecto.

### Cambio Principal de Esta Version (Evaluacion Progresiva)
- **Que era:** Una version donde la tarjeta no mostraba caratula ni album de YouTube Music, el boton de Play/Pausa sufria desincronizaciones de estado invertido, y el boton de vincular solo existia en la tarjeta principal.
- **Que es:** Una cabina enriquecida con lectura completa de titulo, artista, album y caratula (Bitmap y URI) de YouTube Music y reproductores externos, Play/Pausa sincronizado fidedignamente mediante transportControls, boton Vincular en tres accesos estrategicos (Barra superior, Tarjeta Waze y Pantalla de Servicio), ducking transitorio nativo y ondas reactivas duales.
- **Que sera:** En la siguiente iteracion, soporte de sincronizacion con APIs meteorologicas remotas en tiempo real y conexion con modelos de lenguaje ligeros en el dispositivo para generar comentarios contextuales de trafico o anecdotas musicales.

## Creditos
- **Autor:** MyAstron
- **Agentes de Inteligencia Artificial:**
- Stitch
- Gemini
- Antigravity (CLI)
- AI Studio
