# Manual de Usuario: I AM-FM Locutor Radio

## 1. Puesta en Marcha y Vinculación Musical

1. **Permiso de Notificaciones:** Abre la aplicación y pulsa en el banner informativo de la pestaña **Servicio** para conceder el acceso a notificaciones de Android. Esto permite a I AM-FM leer en tiempo real las canciones de Spotify, YouTube Music, etc.
2. **Selector de Apps de Música:**
   - Pulsa el botón **Vincular** en la tarjeta de reproducción multimedia estilo Waze o en la barra superior.
   - **Solo Apps Instaladas:** La ventana lista exclusivamente las aplicaciones que se encuentran realmente instaladas en tu dispositivo (Spotify, YouTube Music, etc.), más las opciones de *Detección Automática* y *Reproductor Local*.
   - **Detección Automática:** Al seleccionar *Detección Automática*, el botón "Abrir reproductor en teléfono" se deshabilita de manera inteligente, ya que el sistema capturará automáticamente cualquier app de música que comience a sonar.
3. **Carátula y Metadatos en Directo:** Visualiza título, artista, álbum y la carátula oficial en alta definición.
4. **Diseño Minimalista de Canción:** La tarjeta de reproducción muestra la información esencial del tema (carátula, títulos y botones de control) sin barras de progreso ni duración de tiempo que puedan saturar la vista.

## 2. Nueva Jerarquía Visual y Navegación Ágil en Cabina

La pantalla principal se encuentra organizada de forma intuitiva de arriba hacia abajo, con **componentes auxiliares colapsables** para que puedas ocultar o mostrar lo que necesites sin perder de vista lo esencial:
1. **Canción Actual (Siempre visible):** Tarjeta superior flotante estilo Waze con carátula, título, artista, botón de Vincular y controles Anterior, Pausa/Play y Siguiente.
2. **Locución y Emisión en Cabina (Siempre visible):** Botón central táctil maestro con anillos de ondas reactivas, estado "EN ANTENA", estilo de voz y selector de encendido/apagado.
3. **Simulador de Cabina (Plegable):** Toca el encabezado para desplegar u ocultar el actuador instantáneo "Probar locución y fade ahora" con vúmetro interactivo.
4. **Servicio en 2° Plano (Plegable):** Toca el encabezado para desplegar el selector interactivo segmentado:
   - **ON:** Servicio siempre activo en primer plano con máxima persistencia.
   - **OFF:** Servicio en pausa o detenido.
   - **AUTO:** Modo inteligente de bajo consumo que se activa al detectar música en el teléfono.
5. **Lo Técnico de la Música y Próximo Anuncio (Plegable):** Toca el encabezado para desplegar los medidores en vivo de atenuación/ducking y el reloj con cuenta regresiva.

## 3. Ondas Sonoras Responsivas

- El botón central cuenta con un sistema de animación acústica adaptativo:
  - **Con más bulla / volumen alto:** Las ondas concéntricas se amplían enérgicamente, reflejando la potencia de la transmisión o de la música en curso.
  - **En calma o volumen bajo:** Las ondas reducen su diámetro y oscilan suavemente de forma armónica y relajada.

## 4. Precisión Horaria al Minuto Exacto

- **Revisión en Vivo de la Hora:** Cada vez que el locutor interviene, consulta el minuto exacto del reloj del sistema.
  - Si son las 14:00 dirá *"Son las 2 de la tarde en punto"*.
  - Si son las 14:01 dirá *"Son las 2 y 1 minuto de la tarde"*.
  - A las 14:15 dirá *"Son las 2 y cuarto de la tarde"*.
  - A las 14:30 dirá *"Son las 2 y media de la tarde"*.
- **Cero Redundancias:** Se eliminó de raíz el fallo que repetía dos veces *"en punto en punto"*.

## 5. Personalización de Horarios y Frecuencia de Anuncios

En la pestaña **Ajustes**, bajo la sección **Horarios y Frecuencia** (la cual puedes desplegar u ocultar), puedes elegir cómo deseas que intervenga el locutor:
1. **Por Defecto (FM Clásico):** Anuncia al terminar una canción cerca de la hora (:00 a :01) o de forma puntual en el reloj sin interrumpir canciones.
2. **Cada N Canciones:** Configura la locución para que se dispare cada vez que termines de escuchar 1, 2, 3, 5 u 8 canciones.
3. **Interrupción Inmediata en la Hora Exacta:** Sin importar si una canción está a la mitad, atenúa suavemente el volumen (ducking) e interrumpe para dar la hora exacta en punto.
4. **Horas Específicas Programadas:** Toca los chips de horas del día (ej. 08:00, 12:00, 14:00, 18:00, 20:00) para que el locutor hable únicamente en los horarios que decidas.

## 6. Carrusel Horizontal de Locutores y Modulaciones

En la pestaña **Ajustes**:
- **Desplazamiento Horizontal sin Barra Visual:** Desliza suavemente de izquierda a derecha entre los 7 estilos de radio FM (*Radio DJ Dinámico, Locutor FM Clásico, Locutora FM Estéreo, Voz Conductor Calma, Noticias y Boletín, Locutor Urbano Chill, Locutor Juvenil Pop*).
- **Inspección de Modulaciones:** Cada tarjeta te permite comparar de un vistazo el Tono (Pitch) y la Velocidad (Speed) de cada personalidad sin barras de scroll visibles.
- **Ficha Rápida:** Al tocar un estilo, se actualizan los controles y dispones de un botón para **"Probar"** la locución de inmediato.

## 7. Frases de Locución y Reactivación Automática de Clima

En la pestaña **Frases**:
- **Sincronización Bidireccional de Clima:**
  - Cuando desactivas el interruptor de *Mención de Clima Local*, todas las frases que contengan `{clima}` se desactivan automáticamente con la etiqueta **"CLIMA APAGADO"**.
  - **Al encender nuevamente el clima:** Las frases climáticas **se reactivan automáticamente** para sonar en tus próximos boletines sin que tengas que encenderlas una por una.
- **Creación y Edición:** Puedes crear o editar plantillas con variables dinámicas `{cancion}`, `{artista}`, `{hora}` y `{clima}`.
- **Anti-repetición:** Dos frases consecutivas nunca serán iguales.

## 8. Pestaña de Servicio y Auditoría de Permisos

En la pestaña **Servicio**:
- Si el permiso de lectura de notificaciones no está concedido, aparece una **tarjeta destacada de advertencia** con un botón de un toque para abrir los Ajustes de Android y activarlo.
- Puedes auditar el estado de los permisos esenciales del sistema, verificar el tiempo de actividad (*Uptime*) y el consumo de memoria RAM.
