# I AM-FM: Locutor Radio

[![Kotlin](https://img.shields.io/badge/Kotlin-Android-7F52FF?style=flat&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-UI-4285F4?style=flat&logo=jetpackcompose&logoColor=white)](https://developer.android.com/compose)
[![Android](https://img.shields.io/badge/Platform-Android%2026%2B-3DDC84?style=flat&logo=android&logoColor=white)](https://developer.android.com/)
[![CLI Workflow](https://img.shields.io/badge/Workflow-CLI-0F172A?style=flat&logo=gnubash&logoColor=white)](#)

## Descripcion del Proyecto
I AM-FM: Locutor Radio es una aplicacion nativa para Android (Kotlin y Jetpack Compose) con interfaz estilo Waze que controla de forma basica la reproduccion activa de otras apps de musica en el telefono (sin exigir cuentas Premium ni integrar APIs musicales cerradas) e incorpora un copiloto locutor por voz personalizable en horarios y estilos de cabina, aplicando ducking nativo transitorio exclusivo para la musica sin alterar el volumen general del dispositivo.

## Especificaciones de Uso y Mantenimiento
- **Plataforma y compilacion:** Android (minSdk 26), desarrollado y compilado mediante flujo CLI con Android SDK y Gradle (JavaVersion 17 con jvmToolchain 21).
- **Referencia visual e icono:** Basado en los archivos HTML de stitch/ y en el icono oficial stitch/icon.png integrado en los drawables de la aplicacion.
- **Filtro exclusivo de apps instaladas:** El selector de vinculacion lista unicamente las aplicaciones de musica instaladas en el dispositivo, filtrando paquetes no presentes y deshabilitando el boton de apertura si se elige "Deteccion Automatica".
- **Locucion horaria exacta y sin repeticion:** Calculo en vivo del minuto exacto (minuto :00 en punto, :01 1 minuto, :15 y cuarto, :30 y media) con saneamiento integral contra duplicidades ("en punto en punto").
- **Horarios de anuncio personalizables:** Cuatro modalidades configurables: por defecto FM (hora en punto al terminar tema o en reloj), cada N canciones reproducidas, interrupcion inmediata en vivo a la hora en punto, y seleccion de horas programadas fijas del dia.
- **Jerarquia visual en Main:** Reorganizacion de la pantalla principal en cinco niveles: 1) Cancion actual, 2) Locucion y control maestro, 3) Simulador de cabina con disparo en vivo, 4) Servicio en 2do plano con selector interactivo ON / OFF / AUTO, y 5) Telemetria tecnica y proximo anuncio.
- **Sincronizacion de clima y frases:** Al desactivar la mencion de clima en la aplicacion, se pausan automaticamente las frases que contienen etiquetas de clima.
- **Mosaico interactivo de locutores:** Mosaico de avatares con transicion animada fluida hacia ficha tecnica horizontal completa con prueba de voz en directo.
- **Auditoria de permisos en Servicio:** Apartado destacado y persistente en la pestana de Servicio para conceder el acceso a notificaciones si no estuviera activo.

## Guia de Usuario y Funcionamiento General
1. **Vinculacion limpia:** Abre el selector de apps; solo veras las instaladas en tu terminal. Si seleccionas Deteccion Automatica, el sistema gestionara la captura en tiempo real sin requerir abrir una app fija.
2. **Reorganizacion visual de Cabina:**
   - Visualiza en primer plano la cancion actual y sus controles estilo Waze.
   - Gestiona el estado de locucion en antena con el boton central maestro.
   - Prueba la atenuacion y locucion de inmediato con el Simulador de Cabina.
   - Ajusta el servicio en segundo plano mediante el conmutador interactivo ON / OFF / AUTO.
   - Consulta el vumetro de audio y la cuenta regresiva del proximo anuncio.
3. **Programacion de anuncios en Ajustes:** Elige entre anunciar cada N temas, interrumpir a la hora exacta, respetar el modo FM por defecto o programar horas fijas del dia.
4. **Mosaico y estilos de voz:** Toca los iconos en mosaico para desplegar en formato horizontal la ficha del locutor, escuchar una prueba o afinar tono y velocidad.
5. **Gestion de frases:** Activa o desactiva plantillas sin riesgo de repeticion. Si apagas el clima, las frases climaticas se pausan automaticamente.
6. **Pestana de Servicio:** Verifica el uptime, la memoria y activa el permiso de notificaciones desde el banner prioritario si aun no fue concedido.

## Implementacion Tecnica
- **MusicServiceLinker:** Depura la lista de reproductores conocidos contrastando `packageManager.getPackageInfo` e `Intent.CATEGORY_APP_MUSIC`, entregando unicamente apps instaladas y bloqueando aperturas invalidas en modo automatico.
- **GenerateRadioScriptUseCase:** Evalua el minuto y la hora exacta en tiempo real para modular la frase ("la 1 y 1 minuto", "las 2 de la tarde en punto"), eliminando de raiz duplicidades en la plantilla.
- **EvaluateHourlyAnnouncementUseCase:** Motor multicriterio que soporta los modos DEFAULT_TOP_OF_HOUR, SONG_COUNT, EXACT_TIME_INTERRUPT y CUSTOM_HOURS.
- **PhraseCrudRepository:** Incorpora sincronizacion de eventos de clima que desactiva de forma automatica las plantillas con etiquetas `{clima}` cuando el proveedor de clima se deshabilita.
- **RadioMainScreen y RadioSettingsScreen:** Implementan la jerarquia visual requerida con selector segmentado de servicio (ON/OFF/AUTO) y transiciones animadas con AnimatedContent para los perfiles de locucion.

## Manual de Desarrollo
Pendiente de redactar al finalizar todas las fases del proyecto.

### Cambio Principal de Esta Version (Evaluacion Progresiva)
- **Que era:** Una version con deteccion de apps donde aparecian servicios no instalados, boton de abrir app activo aun en deteccion automatica, anuncios limitados a la hora en punto, locucion que decia "en punto" a las :01 con el bug de doble "en punto en punto", frases climaticas que seguian sonando con clima apagado, locutores en lista vertical estatica y una pantalla principal sin la jerarquia ergonomica solicitada.
- **Que es:** Una arquitectura refinada con filtro estricto de apps instaladas, boton de apertura inteligentemente deshabilitado en modo automatico, calculo riguroso de la hora y minuto exactos sin repeticion, 4 modalidades de horario y frecuencia de anuncios, sincronizacion reactiva de frases al apagar clima, mosaico de locutores interactivo con expansion horizontal completa, conmutador segmentado ON/OFF/AUTO para el servicio en 2do plano, y pantalla principal reorganizada exactamente segun las prioridades de uso en cabina.
- **Que sera:** En la siguiente iteracion, soporte de micro-audios pregrabados de sintonia tipo jingle de radio FM y generador de dialogos contextuales mediante inteligencia artificial local.

## Creditos
- **Autor:** MyAstron
- **Agentes de Inteligencia Artificial:**
  - Stitch
  - Gemini
  - Antigravity (CLI)
  - AI Studio
