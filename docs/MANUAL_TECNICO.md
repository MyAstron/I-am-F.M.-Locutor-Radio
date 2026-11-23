# Manual Tecnico: I AM-FM Locutor Radio

## 1. Arquitectura del Sistema

La aplicacion implementa una arquitectura MVVM combinada con Clean Architecture ligera orientada a servicios en segundo plano en Android.

```text
app/src/main/java/locutor/cristopher/sic/
├── LocutorApplication.kt
├── MainActivity.kt
├── data/
│   ├── audio/
│   │   └── AudioFadeController.kt
│   ├── media/
│   │   ├── MediaSessionRepository.kt
│   │   └── PlatformResolver.kt
│   ├── preferences/
│   │   └── RadioPreferences.kt
│   └── voice/
│       ├── VoiceEngine.kt
│       └── AndroidTtsVoiceEngine.kt
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
    │   ├── CentralAnnouncerPowerButton.kt
    │   └── SoundWaveRing.kt
    ├── RadioViewModel.kt
    ├── RadioMainScreen.kt
    ├── RadioSettingsScreen.kt
    ├── RadioHistoryScreen.kt
    └── RadioServiceScreen.kt
```

## 2. Modulos Principales

### 2.1 MediaSessionRepository y PlatformResolver
- **Responsabilidad:** Monitorear el controlador multimedia activo en Android sin depender de APIs de terceros (Spotify API, YouTube API).
- **Controles basicos tipo Waze:** Expone exclusivamente `skipToPrevious()`, `playPause()` y `skipToNext()`. No expone listas de reproduccion ni colas para evitar sobrecarga y distracciones.
- **Deteccion de cierre de pista:** Compara `onMetadataChanged` para registrar la cancion que termino (`previousTrack`) y la nueva en curso (`currentTrack`).

### 2.2 AudioFadeController
- **Atenuacion (Fade Out):** Reduce gradualmente el volumen del canal `STREAM_MUSIC` en escalones temporizados y solicita foco de audio transitorio (`AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK`).
- **Restauracion (Fade In):** Al finalizar la locucion, eleva suavemente el volumen al nivel previo y abandona el foco de audio.

### 2.3 AndroidTtsVoiceEngine
- **Sintesis por voz:** Implementa `VoiceEngine` mediante `android.speech.tts.TextToSpeech`.
- **Modulacion de ondas:** Mediante `UtteranceProgressListener` y un flujo continuo en corrutina, actualiza `AudioWaveState.intensity` para que los anillos visuales de Compose vibren al ritmo de la voz.

### 2.4 RadioEngineCoordinator y Servicios Android
- `MediaMonitorNotificationListener`: Servicio `NotificationListenerService` que accede a `MediaSessionManager.addOnActiveSessionsChangedListener` para recibir sesiones multimedia del sistema.
- `RadioAnnouncerForegroundService`: Servicio persistente con tipo `mediaPlayback` y canal de notificaciones prioritario, permitiendo operar con pantalla apagada o en conduccion.
- `RadioEngineCoordinator`: Singleton coordinador que enlaza el repositorio multimedia, el evaluador de hora exacta, el atenuador de audio y el motor de voz.

## 3. Requerimientos de Compilacion
- Android SDK Build Tools (API 36)
- Java 21 / OpenJDK
- Min SDK: 26 (Android 8.0 Oreo)
- Target SDK: 36
