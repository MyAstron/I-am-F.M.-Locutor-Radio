# Manual Tecnico: I AM-FM Locutor Radio

## 1. Arquitectura del Sistema

La aplicacion implementa una arquitectura MVVM combinada con Clean Architecture ligera orientada a servicios en segundo plano en Android, ahora con enlace directo de reproductores de musica, rotacion de frases y modulo meteorologico opcional.

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

## 2. Modulos y Correcciones Implementadas

### 2.1 Enlace y Control de Reproduccion (MusicServiceLinker + MediaSessionRepository)
- **Vinculacion seleccionable:** El usuario puede fijar su reproductor preferido (Spotify, YouTube Music, Apple Music, Deezer, etc.) o activar Deteccion Automatica.
- **Estrategia dual de comandos:**
  1. Si `activeController` esta enlazado y disponible, despacha comandos via `transportControls` (`skipToPrevious()`, `play()`/`pause()`, `skipToNext()`).
  2. Si la sesion aun no se ha enganchado, envia pulsaciones multimedia directas via `AudioManager.dispatchMediaKeyEvent` con `KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE`, `KEYCODE_MEDIA_PREVIOUS` y `KEYCODE_MEDIA_NEXT`.
- **Apertura de app:** Ofrece un intent directo para lanzar la aplicacion de musica si esta cerrada.

### 2.2 Catalogo CRUD y Rotacion de Frases (PhraseCrudRepository)
- **Operaciones completas:** Permite Crear, Listar, Actualizar y Eliminar plantillas de locucion.
- **Variables dinámicas:** Admite `{cancion}`, `{artista}`, `{hora}` y `{clima}`.
- **Algoritmo Anti-repeticion:** Al seleccionar la siguiente frase para el locutor, excluye la ultima frase empleada (`lastUsedPhraseId`), garantizando que dos emisiones consecutivas nunca sean identicas.

### 2.3 Clima Local Opcional bajo Permiso (LocalWeatherProvider)
- **Privacidad estricta:** Solo se activa si el usuario enciende el switch de clima y otorga el permiso de ubicacion (`ACCESS_FINE_LOCATION` o `ACCESS_COARSE_LOCATION`).
- **Degradacion limpia:** Si el permiso no esta concedido, la etiqueta `{clima}` se elimina de forma limpia sin generar errores ni silencios bruscos.

### 2.4 Integracion de Icono y Assets Visuales
- Se incorporo `stitch/icon.png` como recurso oficial `@drawable/app_icon` en el manifiesto y como emblema en la barra superior de la cabina.

### 2.5 Ducking Nativo de Audio sin Alterar Volumen General del Sistema (AudioFadeController)
- **Atenuacion de musica exclusiva:** Se elimino cualquier mutacion de `AudioManager.setStreamVolume` sobre `STREAM_MUSIC` o `STREAM_RING`.
- **Foco de audio transitorio:** Utiliza `AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)` con atributos `USAGE_ASSISTANCE_NAVIGATION_GUIDANCE` y `CONTENT_TYPE_SPEECH`.
- **Comportamiento sin pausa:** Con `setWillPauseWhenDucked(false)`, las apps reproductoras externas (Spotify, YouTube Music) atenúan su salida en segundo plano de manera continua (estilo Waze/WhatsApp).
- **Restauracion instantanea:** Al culminar la locucion o la prueba, se invoca `abandonAudioFocusRequest`, devolviendo la musica a su volumen nominal sin alterar la barra de volumen general del telefono.

### 2.6 Ondas Reactivas Duales y Conmutacion Dinamica de Icono (SoundWaveRing + CentralAnnouncerPowerButton)
- **Animacion de sonido unificada:** Los anillos de ondas concentricas reaccionan activamente tanto al hablar el locutor (`isSpeaking == true`) como durante la reproduccion musical en curso (`isPlayingMusic == true`).
- **Alternancia de icono segun emision:**
  - Si el locutor habla: Icono de microfono (`Icons.Default.Mic`) con etiqueta "LOCUTOR EN VIVO".
  - Si suena musica: Icono de ecualizador (`Icons.Default.GraphicEq`) con etiqueta "MÚSICA EN RUTA".
  - En espera o inactivo: Icono de radio (`Icons.Default.Radio`) o microfono apagado (`Icons.Default.MicOff`) con etiqueta "LOCUTOR LISTO" o "LOCUTOR PAUSADO".

### 2.7 Sincronizacion de Metadatos y Caratulas (YouTube Music, Spotify, Apple Music)
- **Enlace de MediaController:** Filtra dinamicamente las sesiones activas en `MediaSessionManager` mediante el `ComponentName` de `MediaMonitorNotificationListener`. Si el usuario selecciono un reproductor especifico, enlaza prioritariamente su sesion; en modo automatico, prioriza la sesion en estado de reproduccion.
- **Extraccion exhaustiva de campos:**
  - Titulo: `METADATA_KEY_TITLE` con fallback a `METADATA_KEY_DISPLAY_TITLE`.
  - Artista: `METADATA_KEY_ARTIST` con fallback a `METADATA_KEY_ALBUM_ARTIST`.
  - Album: `METADATA_KEY_ALBUM`.
  - Caratula: Lectura de `Bitmap` directo via `METADATA_KEY_ALBUM_ART` o `METADATA_KEY_ART`. En reproductores que entregan URI (`METADATA_KEY_ALBUM_ART_URI` o `METADATA_KEY_ART_URI`), decodifica el stream a traves de `ContentResolver`.
- **Re-registro reactivo de callbacks:** Al cambiar la lista de sesiones activas o la app seleccionada, se liberan callbacks anteriores y se registra `MediaController.Callback` sobre el controlador activo en el hilo principal (`mainHandler`).

### 2.8 Correccion de Desincronizacion de Play/Pausa e Idempotencia
- **Fuente unica de la verdad:** Se elimino cualquier mutacion especulativa o booleano local invertido al pulsar el boton de reproduccion. El icono en la UI se deriva estrictamente del `PlaybackState.state` notificado por `onPlaybackStateChanged`.
- **Comandos transportControls precisos:**
  - Si `playbackState?.state == PlaybackState.STATE_PLAYING`, despacha `transportControls.pause()`.
  - Si esta en pausa o detenido, despacha `transportControls.play()`.
  - Solo en caso de ausencia total de `MediaController` se utiliza `dispatchMediaKeyEvent(KEYCODE_MEDIA_PLAY_PAUSE)` como fallback.

### 2.9 Ubicacion Triple del Acceso a Vincular
- La accion de apertura del selector de reproductores (`ServiceSelectorDialog`) se expone en tres puntos clave:
  1. **Barra Superior / Cabina:** Chip compacto interactivo "Vincular" junto al badge de estado en el `TopAppBar` y en el strip de telemetria.
  2. **Tarjeta de Reproduccion Waze:** Chip y cabecera interactiva "VINCULAR" junto al nombre de la aplicacion sonando.
  3. **Pantalla de Servicio:** Tarjeta dedicada "Servicio de musica vinculado" con boton de accion completa "Vincular / Cambiar app".
