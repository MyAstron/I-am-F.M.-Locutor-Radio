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
