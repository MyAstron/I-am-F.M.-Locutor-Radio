# Manual Técnico: I AM-FM Locutor Radio

## 1. Arquitectura del Sistema

La aplicación implementa una arquitectura MVVM combinada con Clean Architecture ligera orientada a servicios en segundo plano en Android, ahora con enlace directo de reproductores de música, rotación de frases y módulo meteorológico opcional.

```text
app/src/main/java/locutor/cristopher/sic/
├── LocutorApplication.kt
├── MainActivity.kt
├── data/
│   ├── audio/
│   │   └── AudioFadeController.kt
│   ├── media/
│   │   ├── MediaSessionRepository.kt
│   │   ├── MusicServiceLinker.kt
│   │   └── PlatformResolver.kt
│   ├── phrases/
│   │   ├── RadioPhrase.kt
│   │   └── PhraseCrudRepository.kt
│   ├── preferences/
│   │   └── RadioPreferences.kt
│   ├── voice/
│   │   ├── VoiceEngine.kt
│   │   └── AndroidTtsVoiceEngine.kt
│   └── weather/
│       └── LocalWeatherProvider.kt
├── domain/
│   ├── model/
│   │   ├── TrackInfo.kt
│   │   ├── AudioWaveState.kt
│   │   ├── AnnouncerConfig.kt
│   │   └── AnnouncementLog.kt
│   └── usecase/
│       ├── EvaluateHourlyAnnouncementUseCase.kt
│       └── GenerateRadioScriptUseCase.kt
├── service/
│   ├── MediaMonitorNotificationListener.kt
│   ├── RadioAnnouncerForegroundService.kt
│   └── RadioEngineCoordinator.kt
└── ui/
    ├── theme/
    │   ├── Color.kt
    │   ├── Theme.kt
    │   └── Type.kt
    ├── components/
    │   ├── WazeBasicControlBar.kt
    │   ├── ServiceSelectorDialog.kt
    │   ├── CentralAnnouncerPowerButton.kt
    │   └── SoundWaveRing.kt
    ├── RadioViewModel.kt
    ├── RadioMainScreen.kt
    ├── RadioPhrasesCrudScreen.kt
    ├── RadioSettingsScreen.kt
    ├── RadioHistoryScreen.kt
    └── RadioServiceScreen.kt
```

## 2. Módulos y Correcciones Implementadas

### 2.1 Enlace y Control de Reproducción (MusicServiceLinker + MediaSessionRepository)
- **Filtro exclusivo de apps instaladas:** `MusicServiceLinker.getDetectedServices()` evalúa la existencia real en el sistema de cada reproductor (`isAppInstalled()`). Las aplicaciones no instaladas en el terminal son excluidas de la lista.
- **Deshabilitación inteligente:** En el diálogo de vinculación (`ServiceSelectorDialog`), si el usuario selecciona "Detección Automática" o "Reproductor Local", el botón "Abrir reproductor en teléfono" se deshabilita automáticamente impidiendo intents fallidos.

### 2.2 Locución Horaria de Precisión y Erradicación de Duplicidades (GenerateRadioScriptUseCase)
- **Inspección del minuto exacto:** Cada locución generada evalúa `Calendar.getInstance()` en el instante exacto del disparo:
  - Minuto 0: "la 1 de la tarde en punto" / "las 2 de la tarde en punto".
  - Minuto 1: "la 1 y 1 minuto de la tarde" / "las 2 y 1 minuto de la tarde" (sin decir "en punto").
  - Minuto 15: "la 1 y cuarto de la tarde" / "las 2 y cuarto de la tarde".
  - Minuto 30: "la 1 y media de la tarde" / "las 2 y media de la tarde".
  - Otros minutos: "la 1 y N minutos" / "las H y N minutos".
- **Erradicación del bug "en punto en punto":** Se corrigió la redundancia eliminando de raíz el append estático de "en punto" cuando el minuto es distinto de 00 y saneando las plantillas de SharedPreferences y valores por defecto mediante un filtro de sustitución estricta.

### 2.3 Motor Multicriterio de Programación de Anuncios (EvaluateHourlyAnnouncementUseCase + RadioPreferences)
- **Modalidades soportadas:**
  1. `DEFAULT_TOP_OF_HOUR`: Modo FM por defecto que anuncia cerca de la hora (:00 a :01) al terminar un tema o de forma continua por reloj sin cortar canciones.
  2. `SONG_COUNT`: Dispara el anuncio tras completarse un conteo fijo de canciones (configurable a 1, 2, 3, 5 u 8 temas).
  3. `EXACT_TIME_INTERRUPT`: Interrumpe de inmediato la música a la hora exacta aplicando ducking en vivo.
  4. `CUSTOM_HOURS`: Permite al usuario seleccionar horas específicas fijas del día (ej. 08:00, 12:00, 18:00) mediante chips interactivos.

### 2.4 Reorganización Visual en Pantalla Principal (RadioMainScreen)
- La pantalla principal (`Cabina`) se reestructuró en 5 bloques jerárquicos:
  1. **Primero: Canción actual** (`WazeBasicControlBar` con metadatos, carátula, transport controls y botón de vincular).
  2. **Segundo: Locución** (`CentralAnnouncerPowerButton` con control maestro de antena, badge de modo y estilo de voz).
  3. **Tercero: Simulador de cabina** (Actuador de disparo inmediato con vúmetro interactivo).
  4. **Cuarto: Servicio de 2do plano** (Tarjeta de telemetría con conmutador interactivo segmentado `[ ON ] [ OFF ] [ AUTO ]`).
  5. **Quinto: Lo técnico de la música y próximo anuncio** (Bento Grid con medidores de ducking, cuenta regresiva y horario).

### 2.5 Sincronización Reactiva de Clima y Frases (PhraseCrudRepository)
- **Desactivación automática:** Al desactivar el interruptor de "Mención de Clima Local", `PhraseCrudRepository.onWeatherSettingChanged(false)` recorre el catálogo y desactiva (`isActive = false`) de manera automática todas las frases que contengan la variable `{clima}`.
- **Filtro en rotación:** Al seleccionar frases en `getNextNonRepeatingPhrase(weatherEnabled)`, las frases que incluyan clima son ignoradas si el clima está inactivo.

### 2.6 Mosaico Interactivo de Locutores y Transición Horizontal (RadioSettingsScreen)
- **Mosaico compacto:** Los 7 presets de radio FM se presentan inicialmente como una fila/mosaico de avatares con íconos vectoriales distintivos.
- **Transición expandida:** Al pulsar cualquier preset, `AnimatedContent` realiza una animación fluida hacia una tarjeta horizontal de ancho completo con avatar de alto contraste, descripción del timbre, badge "ACTIVO" y botón de prueba rápida en vivo.

### 2.7 Auditoría de Lectura de Notificaciones en Pestaña de Servicio (RadioServiceScreen)
- **Alerta prioritaria:** Si `viewModel.isNotificationAccessGranted()` retorna `false`, se despliega una tarjeta destacada en la parte superior del tab de Servicio con llamada a la acción directa hacia `Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS`.
- **Lista de auditoría:** Muestra en tiempo real el estado verificado de los 4 permisos críticos de Android (MediaSession, Servicio Foreground, POST_NOTIFICATIONS y Foco de Audio).
