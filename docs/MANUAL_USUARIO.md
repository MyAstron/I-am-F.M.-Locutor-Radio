# Manual de Usuario: I AM-FM Locutor Radio

## 1. Puesta en Marcha y Vinculación Musical

1. **Permiso de Notificaciones:** Abre la aplicación y pulsa en el banner informativo de la pestaña **Servicio** para conceder el acceso a notificaciones de Android. Esto permite a I AM-FM leer en tiempo real las canciones de Spotify, YouTube Music, etc.
2. **Selector de Apps de Música:**
   - Pulsa el botón **Vincular** en la tarjeta de reproducción multimedia estilo Waze o en la barra superior.
   - **Solo Apps Instaladas:** La ventana lista exclusivamente las aplicaciones que se encuentran realmente instaladas en tu dispositivo (Spotify, YouTube Music, etc.), más las opciones de *Detección Automática* y *Reproductor Local*.
   - **Detección Automática:** Al seleccionar *Detección Automática*, el botón "Abrir reproductor en teléfono" se deshabilita de manera inteligente, ya que el sistema capturará automáticamente cualquier app de música que comience a sonar.
3. **Carátula y Metadatos en Directo:** Visualiza título, artista, álbum y la carátula oficial en alta definición.

## 2. Nueva Jerarquía Visual en la Pantalla Principal (Cabina)

La pantalla principal se encuentra organizada de forma intuitiva de arriba hacia abajo:
1. **Canción Actual:** Tarjeta superior flotante estilo Waze con carátula, título, artista, botón de Vincular y controles Anterior, Pausa/Play y Siguiente.
2. **Locución y Emisión:** Botón central táctil maestro con anillos de ondas reactivas, estado "EN ANTENA", estilo de voz y selector de encendido/apagado.
3. **Simulador de Cabina:** Actuador instantáneo "Probar locución y fade ahora" con vúmetro interactivo para ensayar la atenuación de volumen y la voz sin esperar la hora.
4. **Servicio en 2° Plano:** Tarjeta interactiva con selector segmentado de tres modos:
   - **ON:** Servicio siempre activo en primer plano con máxima persistencia.
   - **OFF:** Servicio en pausa o detenido.
   - **AUTO:** Modo inteligente de bajo consumo que se activa al detectar música en el teléfono.
5. **Lo Técnico de la Música y Próximo Anuncio:** Medidores tipo Bento Grid con el porcentaje de atenuación/ducking en vivo y el reloj con cuenta regresiva para la siguiente intervención.

## 3. Precisión Horaria al Minuto Exacto

- **Revisión en Vivo de la Hora:** Cada vez que el locutor interviene, consulta el minuto exacto del reloj del sistema.
  - Si son las 14:00 dirá *"Son las 2 de la tarde en punto"*.
  - Si son las 14:01 dirá *"Son las 2 y 1 minuto de la tarde"*.
  - A las 14:15 dirá *"Son las 2 y cuarto de la tarde"*.
  - A las 14:30 dirá *"Son las 2 y media de la tarde"*.
- **Cero Redundancias:** Se eliminó de raíz el fallo que repetía dos veces *"en punto en punto"*.

## 4. Personalización de Horarios y Frecuencia de Anuncios

En la pestaña **Ajustes**, bajo la sección **Horarios y Frecuencia**, puedes elegir cómo deseas que intervenga el locutor:
1. **Por Defecto (FM Clásico):** Anuncia al terminar una canción cerca de la hora (:00 a :01) o de forma puntual en el reloj sin interrumpir canciones.
2. **Cada N Canciones:** Configura la locución para que se dispare cada vez que termines de escuchar 1, 2, 3, 5 u 8 canciones.
3. **Interrupción Inmediata en la Hora Exacta:** Sin importar si una canción está a la mitad, atenúa suavemente el volumen (ducking) e interrumpe para dar la hora exacta en punto.
4. **Horas Específicas Programadas:** Toca los chips de horas del día (ej. 08:00, 12:00, 14:00, 18:00, 20:00) para que el locutor hable únicamente en los horarios que decidas.

## 5. Mosaico de Locutores con Transición Animada

En la pestaña **Ajustes**:
- **Mosaico Compacto:** Se muestran los 7 estilos de radio FM (*Radio DJ Dinámico, Locutor FM Clásico, Locutora FM Estéreo, Voz Conductor Calma, Noticias y Boletín, Locutor Urbano Chill, Locutor Juvenil Pop*) como íconos en mosaico.
- **Expansión Horizontal:** Al tocar cualquier ícono del mosaico, la tarjeta transiciona suavemente a un formato horizontal de ancho completo con la información técnica del locutor, insignia de "ACTIVO" y un botón para **"Escuchar prueba en vivo"**.
- **Voces del Sistema y Modulación:** Si tu teléfono tiene varias voces TTS instaladas, puedes elegirlas, además de regular el tono (grave/agudo) y la velocidad (pausada/rápida).

## 6. Frases de Locución y Sincronización de Clima

En la pestaña **Frases**:
- **Desactivación Automática con Clima Apagado:** Cuando desactivas el interruptor de *Mención de Clima Local*, todas las frases que contengan `{clima}` se desactivan automáticamente y se muestran con la insignia **"CLIMA APAGADO"**.
- **Creación y Edición:** Puedes crear o editar plantillas con variables dinámicas `{cancion}`, `{artista}`, `{hora}` y `{clima}`.
- **Anti-repetición:** Dos frases consecutivas nunca serán iguales.

## 7. Pestaña de Servicio y Auditoría de Permisos

En la pestaña **Servicio**:
- Si el permiso de lectura de notificaciones no está concedido, aparece una **tarjeta destacada de advertencia** con un botón de un toque para abrir los Ajustes de Android y activarlo.
- Puedes auditar el estado de los permisos esenciales del sistema, verificar el tiempo de actividad (*Uptime*) y el consumo de memoria RAM.
