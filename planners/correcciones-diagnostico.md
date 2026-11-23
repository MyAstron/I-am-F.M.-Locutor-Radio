  1. Botón "Vincular" estilo Waze y Selección de Servicios Musicales
  
  • Componentes: app/src/main/java/locutor/cristopher/sic/data/media/MusicServiceLinker.kt,
  app/src/main/java/locutor/cristopher/sic/ui/components/ServiceSelectorDialog.kt y          
  app/src/main/java/locutor/cristopher/sic/ui/components/WazeBasicControlBar.kt.
  • Comportamiento:
      • En la tarjeta del reproductor se muestra el botón interactivo VINCULAR.
      • Al pulsarlo, se abre un diálogo que consulta en tiempo real mediante packageManager.getLaunchIntentForPackage cuáles de las plataformas musicales  
      (Spotify, YouTube Music, Apple Music, Deezer, etc.) están realmente instaladas en el dispositivo, marcándolas con una insignia de estado verde       
      "Instalada" y permitiendo su selección o lanzamiento directo.
      • Los controles básicos (Anterior, Reproducir/Pausar y Siguiente) operan de inmediato sobre la sesión multimedia activa (MediaController) con        
      respaldo de pulsaciones directas (dispatchMediaKeyEvent) sin exigir vinculación previa.
  

  2. Ducking Nativo Exclusivo sobre la Música (Sin Alterar Volumen General)
  
  • Componentes: app/src/main/java/locutor/cristopher/sic/data/audio/AudioFadeController.kt y
  app/src/main/java/locutor/cristopher/sic/data/voice/AndroidTtsVoiceEngine.kt.
  • Comportamiento:          
      • Se eliminaron todas las modificaciones directas de setStreamVolume sobre STREAM_MUSIC o STREAM_RING.
      • La atenuación se gestiona mediante foco de audio transitorio: AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK con USAGE_ASSISTANCE_NAVIGATION_GUIDANCE,         
      CONTENT_TYPE_SPEECH y setWillPauseWhenDucked(false).
      • Este mecanismo replica el comportamiento de Waze o notas de voz de WhatsApp: la aplicación de música externa baja suavemente su nivel de salida sin
      mover la barra física de volumen del teléfono. Al finalizar la intervención, se invoca abandonAudioFocusRequest restaurando la música a su volumen   
      habitual.
  
  
  3. Ondas Reactivas Duales y Conmutación Dinámica de Ícono Central
  
  • Componentes: app/src/main/java/locutor/cristopher/sic/ui/components/SoundWaveRing.kt,
  app/src/main/java/locutor/cristopher/sic/ui/components/CentralAnnouncerPowerButton.kt y app/src/main/java/locutor/cristopher/sic/ui/RadioMainScreen.kt.  
  • Comportamiento:
      • Los tres anillos concéntricos vibran con modulaciones continuas tanto cuando interviene el locutor como cuando suena una canción.
      • Alternancia de ícono e información central:
          • Locutor en vivo (isSpeaking == true): Muestra el ícono de micrófono (Mic) con la etiqueta LOCUTOR EN VIVO.
          • Música en reproducción (isPlayingMusic == true y !isSpeaking): Muestra el ícono de ecualizador gráfico (GraphicEq) con la etiqueta MÚSICA EN   
          RUTA.
          • En espera / Silenciado: Muestra el ícono de radio (Radio) o micrófono apagado (MicOff) con la etiqueta LOCUTOR LISTO o LOCUTOR PAUSADO.        
  
  

  4. Verificación de Compilación y Configuración Gradle/JDK
  
  • Se garantizó la configuración de compileOptions con JavaVersion.VERSION_17 y kotlin { jvmToolchain(21) } en app/build.gradle.kts.
  • Resultado de compilación:
    BUILD SUCCESSFUL in 32s
    APK generado: app/build/outputs/apk/debug/app-debug.apk (25 MB)
  

  5. Documentación Actualizada
  
  • README.md: Actualizado con las especificaciones del nuevo ducking, selector estilo Waze y ondas duales, cumpliendo la regla de cero emojis, insignias  
  Shields.io, evaluación progresiva y créditos.
  • docs/MANUAL_TECNICO.md: Secciones 2.5 y 2.6 añadidas detallando la arquitectura de ducking transitorio y conmutación reactiva.
  • docs/MANUAL_USUARIO.md: Actualizado con el uso del botón Vincular, explicación de la atenuación de volumen y guía de los estados visuales del botón    
  maestro.