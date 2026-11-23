# I AM-FM: Locutor Radio

[![Kotlin](https://img.shields.io/badge/Kotlin-Android-7F52FF?style=flat&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-UI-4285F4?style=flat&logo=jetpackcompose&logoColor=white)](https://developer.android.com/compose)
[![Android](https://img.shields.io/badge/Platform-Android%2026%2B-3DDC84?style=flat&logo=android&logoColor=white)](https://developer.android.com/)
[![CLI Workflow](https://img.shields.io/badge/Workflow-CLI-0F172A?style=flat&logo=gnubash&logoColor=white)](#)

## Descripcion del Proyecto
I AM-FM: Locutor Radio es una aplicacion nativa para Android (Kotlin y Jetpack Compose) con interfaz estilo Waze que controla de forma basica la reproduccion activa de otras apps de musica en el telefono (sin exigir cuentas Premium ni integrar APIs musicales cerradas) e incorpora un locutor por voz que interviene al terminar una cancion cuando coincide con una hora exacta, aplicando un fade previo para priorizar la locucion.

## Especificaciones de Uso y Mantenimiento
- **Plataforma y compilacion:** Android (minSdk 26), desarrollado y compilado mediante flujo CLI y Android SDK.
- **Referencia visual e icono:** Basado en los archivos HTML de stitch/ y en el icono oficial stitch/icon.png integrado en los drawables de la aplicacion.
- **Vinculacion de servicio musical:** Permite seleccionar y vincular la app de musica (Spotify, YouTube Music, Apple Music, Deezer, etc.) o detectar sesiones multimedia activas para operar los controles basicos (Anterior, Reproducir/Pausar y Siguiente, sin lista de reproduccion) con doble respaldo de eventos multimedia directos.
- **Voz, frases y clima:** Soporta variaciones de voz, catalogo CRUD de frases sin repetir la ultima usada y mencion de clima local unicamente con permiso explicito del usuario.
- **Mantenimiento:** Mantener desacoplados el enlace de sesion multimedia, el controlador de fade, el repositorio CRUD de frases, el motor de voz y el proveedor opcional de clima.

## Guia de Usuario y Funcionamiento General
1. **Permisos y servicio de musica:** Concede acceso a notificaciones y selecciona o vincula tu servicio de musica mediante el dialogo de enlace para habilitar los controles de Anterior, Reproducir/Pausar y Siguiente.
2. **Boton central y ondas:** Usa el boton central grande para encender o apagar al locutor y observa las ondas reactivas al sonido a su alrededor vibrando durante la reproduccion y la locucion.
3. **Menu de frases (CRUD) y variaciones de voz:** Crea, edita, consulta o elimina frases personalizadas con etiquetas dinamicas ({cancion}, {artista}, {hora}, {clima}) y garantiza una rotacion sin repeticion consecutiva.
4. **Clima local opcional y fade:** Si otorgas permiso para clima local, el locutor puede incluirlo en el reporte; al cerrar una cancion en hora exacta, aplica fade previo para priorizar su voz y restaura el audio al terminar.

## Implementacion Tecnica
- **Enlace y control de sesiones:** MusicServiceLinker y MediaSessionRepository gestionan la app musical seleccionada y observan MediaSessionManager para despachar comandos basicos, con respaldo de eventos directos KeyEvent si no hay controlador enlazado.
- **Priorizacion por fade:** AudioFadeController ejecuta fade out / fade in y foco de audio antes y despues de la locucion mediante AudioManager.
- **CRUD y rotacion de frases:** PhraseCrudRepository almacena las frases del usuario y selecciona una distinta a la anterior (lastUsedPhraseId), integrando variables dinamicas.
- **Variaciones de voz y ondas:** AndroidTtsVoiceEngine gestiona el sintetizador de voz y emite niveles continuos de modulacion para animar los anillos de SoundWaveRing.
- **Clima local condicionado a permiso:** LocalWeatherProvider evalua los permisos de localizacion antes de consultar las condiciones meteorologicas, omitiendo limpiamente la variable si el permiso no esta concedido.

## Manual de Desarrollo
Pendiente de redactar al finalizar todas las fases del proyecto.

### Cambio Principal de Esta Version (Evaluacion Progresiva)
- **Que era:** Una version donde el control multimedia pedia permiso de notificaciones pero no vinculaba explicitamente el servicio de musica ni habilitaba el control efectivo, sin icono stitch/icon.png integrado, sin menu CRUD de frases con rotacion ni clima local opcional.
- **Que es:** Una cabina en Kotlin/Compose con enlace y seleccion de servicio de musica para controles basicos plenamente funcionales (con fallback de teclas multimedia directas), icono stitch/icon.png integrado en la cabina y el manifiesto, menu dedicado con CRUD completo de frases sin repeticion consecutiva, opcion de clima local bajo permiso explicito y fade previo de locutor.
- **Que sera:** En la siguiente iteracion, soporte de sincronizacion con APIs meteorologicas remotas en tiempo real y conexion con modelos de lenguaje ligeros en el dispositivo para generar comentarios contextuales de trafico o anecdotas musicales.

## Creditos
- **Autor:** MyAstron
- **Agentes de Inteligencia Artificial:**
  - Stitch
  - Gemini
  - Antigravity (CLI)
  - AI Studio
