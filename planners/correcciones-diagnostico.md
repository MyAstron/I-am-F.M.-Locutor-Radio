### 1. Leer título, artista, álbum y carátula (foto) de YouTube Music y demás apps

Si el selector solo guarda el `packageName` pero no engancha el `MediaController` correspondiente ni lee `metadata` y `playbackState`, la tarjeta se queda vacía:

* **Vincular el `MediaController` correcto:** En `MediaSessionManager`, obtén `getActiveSessions(ComponentName(context, MediaMonitorNotificationListener::class.java))`. Si hay una app vinculada (`linkedPackageName`), busca la sesión cuyo `packageName` coincida; si no, toma la primera sesión activa.
* **Leer metadatos y carátula:** En `MediaController.Callback.onMetadataChanged(metadata: MediaMetadata?)` (y al enlazar inicialmente), extrae:
* Título: `metadata?.getString(MediaMetadata.METADATA_KEY_TITLE)` o `METADATA_KEY_DISPLAY_TITLE`
* Artista: `metadata?.getString(MediaMetadata.METADATA_KEY_ARTIST)` o `METADATA_KEY_ALBUM_ARTIST`
* Álbum: `metadata?.getString(MediaMetadata.METADATA_KEY_ALBUM)`
* Carátula (`Bitmap`): `metadata?.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART) ?: metadata?.getBitmap(MediaMetadata.METADATA_KEY_ART)` (y si viene null en algunos reproductores, leer `METADATA_KEY_ALBUM_ART_URI` / `METADATA_KEY_ART_URI`).


* **Re-registrar callbacks:** Cada vez que cambie la lista de sesiones activas (`onActiveSessionsChanged`), limpia callbacks anteriores y registra `MediaController.Callback` en la sesión vinculada/activa para recibir cambios de canción y estado en vivo.

---

### 2. Corregir la desincronización de Play / Pausa con YouTube Music

El bug de estado invertido ocurre cuando la UI asume el estado opuesto al hacer clic en lugar de reflejar `PlaybackState.state` del `MediaController`, o cuando `dispatchMediaKeyEvent` alterna sin sincronizar el estado real:

* **Fuente única de verdad:** El ícono de Play/Pausa en `WazeBasicControlBar` debe derivarse **siempre** de `track.isPlaying` (proveniente de `PlaybackStateCompat.STATE_PLAYING` / `PlaybackState.STATE_PLAYING`), nunca de un booleano local invertido al pulsar.
* **Usar `transportControls` antes que teclas ciegas:**
* Si `playbackState?.state == PlaybackState.STATE_PLAYING`, llama a `mediaController.transportControls.pause()`.
* Si está pausado o detenido, llama a `mediaController.transportControls.play()`.
* Usa `dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE))` solo como respaldo si `mediaController == null` o si `transportControls` no está disponible.


* **Escuchar `onPlaybackStateChanged`:** Al cambiar de estado en YouTube Music, `onPlaybackStateChanged(state)` actualizará `isPlaying = (state?.state == PlaybackState.STATE_PLAYING)` y corregirá el botón de tu app automáticamente.

---

### 3. Más ubicaciones visibles para el botón "Vincular"

Para que el usuario no tenga que buscarlo en un solo rincón, expón la misma acción `onOpenServiceLinker()` en tres puntos clave:

1. **Barra superior / Cabina:** Un chip o botón compacto **"Vincular"** junto al badge de la app o indicador de estado.
2. **Tarjeta de reproducción estilo Waze:** El botón **"Vincular"** visible en la cabecera de la tarjeta (junto al nombre de la app detectada).
3. **Pantalla / pestaña de Servicio:** Una tarjeta destacada de **"Servicio de música vinculado"** con el botón **"Vincular / Cambiar app"** para reconfigurarlo cuando quieras.