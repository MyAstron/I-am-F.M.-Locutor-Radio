# I AM-FM: Locutor Radio

[![Kotlin](https://img.shields.io/badge/Kotlin-Android-7F52FF?style=flat&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-UI-4285F4?style=flat&logo=jetpackcompose&logoColor=white)](https://developer.android.com/compose)
[![Android](https://img.shields.io/badge/Platform-Android%2026%2B-3DDC84?style=flat&logo=android&logoColor=white)](https://developer.android.com/)
[![CLI Workflow](https://img.shields.io/badge/Workflow-CLI-0F172A?style=flat&logo=gnubash&logoColor=white)](#)

## Descripcion del Proyecto
I AM-FM: Locutor Radio es una aplicacion nativa para Android (Kotlin y Jetpack Compose) con interfaz estilo Waze que controla de forma basica la reproduccion activa de otras aplicaciones de musica en el dispositivo (sin exigir cuentas Premium ni integrar APIs musicales cerradas) e incorpora un locutor por voz que interviene al terminar una cancion cuando coincide con una hora exacta, aplicando un fade previo para priorizar la locucion.

## Especificaciones de Uso y Mantenimiento
- **Plataforma y compilacion:** Android (minSdk 26), desarrollado y compilado mediante flujo CLI con Android SDK y Gradle.
- **Referencia visual inicial:** Los estados y composiciones visuales base se toman de los archivos HTML ubicados en la carpeta stitch/ del proyecto.
- **Requerimientos de cuenta:** No requiere suscripciones Premium ni cuentas de terceros; opera directamente sobre la sesion multimedia activa del sistema Android.
- **Controles soportados:** Unicamente controles basicos estilo Waze (Anterior, Reproducir/Pausar y Siguiente), sin lista de reproduccion ni gestion de colas.
- **Permisos y servicios:** Requiere acceso a notificaciones (BIND_NOTIFICATION_LISTENER_SERVICE), servicio en primer plano (FOREGROUND_SERVICE_MEDIA_PLAYBACK), notificaciones (POST_NOTIFICATIONS) y gestion de foco y volumen de audio (MODIFY_AUDIO_SETTINGS).
- **Mantenimiento:** Conservar desacoplados el lector de MediaSession, AudioFadeController, el motor de voz y los componentes visuales de Compose para facilitar adaptaciones ante evoluciones del sistema Android o mejoras de voz sintetica.

## Guia de Usuario y Funcionamiento General
1. **Inspeccion y arranque:** Abre la aplicacion, concede el permiso de acceso a notificaciones y manten activo el servicio de monitoreo en primer plano.
2. **Barra estilo Waze:** Visualiza la cancion en curso y la aplicacion de origen (Spotify, YouTube Music, etc.) con los controles basicos de Anterior, Reproducir/Pausar y Siguiente (sin lista de reproduccion).
3. **Boton central y ondas:** Usa el boton central grande para encender o apagar al locutor (ON / OFF) y observa las ondas reactivas al sonido a su alrededor vibrando segun la actividad de audio y locucion.
4. **Fade y anuncio de hora exacta:** Cuando concluye una cancion y la hora esta en punto (sin haberse anunciado previamente en esa misma hora), la aplicacion aplica una atenuacion (fade out) para priorizar la voz del locutor, anuncia la cancion terminada y la hora exacta, y restaura el volumen (fade in) al concluir.
5. **Simulador de Cabina:** Permite probar inmediatamente la secuencia completa de atenuacion, sintesis y restauracion de audio sin esperar la hora en punto.

## Implementacion Tecnica
- **Traduccion visual desde stitch/:** Los archivos HTML en stitch/ definen la estructura visual y estados de referencia traducidos fielmente a Jetpack Compose (WazeBasicControlBar, CentralAnnouncerPowerButton, SoundWaveRing, Bento HUD y simulador).
- **Sesiones multimedia:** MediaMonitorNotificationListener y MediaSessionRepository observan MediaSessionManager para obtener metadatos, identificar la aplicacion de origen y ejecutar comandos de transporte basico.
- **Priorizacion de voz con fade:** AudioFadeController aplica atenuacion progresiva y foco transitorio antes y despues de la locucion mediante AudioManager y AudioFocusRequest.
- **Evaluacion al cierre de pista:** Al cambiar de cancion, EvaluateHourlyAnnouncementUseCase valida la hora actual contra la hora exacta y el registro de la ultima hora anunciada para evitar duplicidad.
- **Sintesis de voz y ondas sonoras:** AndroidTtsVoiceEngine gestiona el motor TextToSpeech y modula dinamicamente el estado de intensidad en AudioWaveState para retroalimentar la interfaz de usuario.

## Manual de Desarrollo
Pendiente de redactar al finalizar todas las fases del proyecto.

### Cambio Principal de Esta Version (Evaluacion Progresiva)
- **Que era:** Una propuesta centrada en colas o listas de reproduccion entre plataformas o pantallas sin priorizacion de voz con fade ni referencia visual HTML en stitch/.
- **Que es:** Una aplicacion nativa Android en Kotlin y Jetpack Compose con referencia visual en los HTML de stitch/, control basico estilo Waze (Anterior, Reproducir/Pausar, Siguiente, sin lista de reproduccion), boton central grande con ondas reactivas al sonido, fade previo para priorizar la voz al anunciar la hora exacta al terminar una cancion, arquitectura desacoplada por capas y documentacion sin emojis con badges de GitHub.
- **Que sera:** En la siguiente fase, integracion de almacenamiento local Room para persistencia historica extendida y conexion opcional con modelos de lenguaje para enriquecer el guion del locutor con contexto meteorologico o vial.

## Creditos
- **Autor:** MyAstron
- **Agentes de Inteligencia Artificial:**
  - Stitch
  - Gemini
  - Antigravity (CLI)
  - AI Studio
