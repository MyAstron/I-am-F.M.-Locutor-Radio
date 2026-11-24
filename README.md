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
- **Cabecera despejada sin icono de usuario:** Barra superior simplificada sin el icono superfluo de perfil, optimizando el espacio visual en pantalla.
- **Reproductor minimalista sin barra ni duracion:** La tarjeta de cancion actual presenta exclusivamente titulo, artista, album, controles de transporte y boton vincular, prescindiendo de la barra de progreso y el cronometro de duracion.
- **Ondas reactivas y responsivas:** Animacion acustica adaptable que genera expansiones amplias y energicas ante mayor volumen o sonido ("bulla") y oscilaciones suaves y armonicas en reposo o volumen bajo.
- **Reactivacion bidireccional de frases de clima:** Al apagar el clima se pausan las frases que lo mencionan, y al encenderlo nuevamente se reactivan de forma automatica en el catalogo.
- **Scroll horizontal de locutores y modulaciones:** Carrusel horizontal fluido para navegar por los 7 estilos de radio FM y sus parametros de tono y velocidad, sin barras de desplazamiento visibles.
- **Secciones colapsables para navegacion agil:** Todas las funciones auxiliares (simulador, servicio en 2do plano, metricas tecnicas, configuraciones avanzadas) cuentan con encabezados plegables, manteniendo fijas y siempre visibles unicamente la informacion de la cancion y la emision central en cabina.

## Guia de Usuario y Funcionamiento General
1. **Cabina limpia y ergonomica:** Consulta la cancion en curso sin barras de duracion distractoras y comanda la reproduccion con anterior, pausa/play y siguiente.
2. **Navegacion colapsable:** Pliega o despliega el Simulador de Cabina, el Servicio en 2do Plano o las Metricas Tecnicas tocando sus encabezados.
3. **Ondas dinamicas en cabina:** Observa como las ondas concentricas del boton central se expanden ampliamente ante mayor intensidad sonora y se tornan sutiles y armonicas cuando la musica o la voz es pausada.
4. **Carrusel de locutores y modulacion:** En Ajustes, desliza horizontalmente entre los presets de radio FM para examinar sus tonos y velocidades sin ver barras de scroll en pantalla.
5. **Gestion reactiva de clima:** Al activar el interruptor de Clima Local, las frases que contienen `{clima}` se reactivan automaticamente para sonar en antena.
6. **Configuraciones organizadas:** En la pestana de Ajustes, despliega unicamente el modulo que desees editar (horarios, afinacion fina, voces nativas, ducking) manteniendo el resto colapsado para un acceso instantaneo.

## Implementacion Tecnica
- **SoundWaveRing:** Modulacion responsiva basada en `dynamicIntensity` y factor de ruido acustico (`isHighNoise`), escalando el radio hasta 2.1x con aceleracion FastOutSlow en alta energia y atenuando a 1.12x con curvas armonicas en baja intensidad.
- **WazeBasicControlBar:** Estructura depurada con eliminacion de `LinearProgressIndicator` y etiquetas temporales de posicion/duracion para un diseno limpio y libre de desincronizaciones de streaming.
- **PhraseCrudRepository:** `onWeatherSettingChanged(Boolean)` aplica actualizacion bidireccional sobre el StateFlow de frases, pausando con clima inactivo y restaurando a `isActive = true` al reactivarse el servicio meteorologico.
- **RadioSettingsScreen y RadioMainScreen:** Incorporacion de contenedores `AnimatedVisibility` con `expandVertically()` y `shrinkVertically()` gobernados por estados booleanos independientes, junto a `Modifier.horizontalScroll()` sin decoradores de scrollbar.

## Manual de Desarrollo
Pendiente de redactar al finalizar todas las fases del proyecto.

### Cambio Principal de Esta Version (Evaluacion Progresiva)
- **Que era:** Una interfaz con un icono de perfil de usuario sobrante en la esquina superior derecha, una tarjeta musical recargada con barra de progreso y cronometros de duracion, ondas estaticas que no diferenciaban volumen alto de volumen bajo, frases climaticas que quedaban apagadas permanentemente al reactivar el clima, locutores en lista larga con scrollbar visible y pantallas saturadas donde era necesario desplazarse excesivamente para encontrar cada ajuste.
- **Que es:** Una arquitectura visual limpia y modular, con cabecera despejada sin icono de usuario, reproductor musical libre de barras de duracion, ondas acusticas responsivas que amplian su radio ante la bulla y se vuelven armonicas en calma, reactivacion automatica de frases de clima al volver a encenderlo, carrusel horizontal invisible para los estilos de locutor y secciones colapsables en todas las funciones secundarias garantizando que solo la musica y la cabina permanezcan siempre fijas.
- **Que sera:** En la siguiente iteracion, integracion de ecualizador visual FFT conectado directamente al bus de audio nativo y perfiles de viaje automatizados para autopista, ciudad y noche.

## Creditos
- **Autor:** MyAstron
- **Agentes de Inteligencia Artificial:**
  - Stitch
  - Gemini
  - Antigravity (CLI)
  - AI Studio
