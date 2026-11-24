package locutor.cristopher.sic.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeDown
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import locutor.cristopher.sic.domain.model.AnnouncementScheduleMode
import locutor.cristopher.sic.domain.model.BackgroundServiceMode
import locutor.cristopher.sic.ui.components.CentralAnnouncerPowerButton
import locutor.cristopher.sic.ui.components.WazeBasicControlBar
import locutor.cristopher.sic.ui.theme.CockpitOnPrimaryContainer
import locutor.cristopher.sic.ui.theme.CockpitOnSurface
import locutor.cristopher.sic.ui.theme.CockpitOnSurfaceVariant
import locutor.cristopher.sic.ui.theme.CockpitPrimary
import locutor.cristopher.sic.ui.theme.CockpitPrimaryContainer
import locutor.cristopher.sic.ui.theme.CockpitSurface
import locutor.cristopher.sic.ui.theme.CockpitSurfaceContainer
import locutor.cristopher.sic.ui.theme.CockpitSurfaceContainerHigh
import locutor.cristopher.sic.ui.theme.CockpitSurfaceContainerHighest
import locutor.cristopher.sic.ui.theme.CockpitSurfaceContainerLow
import locutor.cristopher.sic.ui.theme.CockpitSurfaceContainerLowest
import locutor.cristopher.sic.ui.theme.CockpitTertiary

@Composable
fun RadioMainScreen(
    viewModel: RadioViewModel,
    modifier: Modifier = Modifier
) {
    val currentTrack by viewModel.currentTrack.collectAsState()
    val config by viewModel.announcerConfig.collectAsState()
    val waveState by viewModel.waveState.collectAsState()
    val isExecuting by viewModel.isExecutingAnnouncement.collectAsState()
    val isDucking by viewModel.duckingActive.collectAsState()
    val nextHourLabel by viewModel.nextBroadcastLabel.collectAsState()
    val countdownSec by viewModel.countdownSeconds.collectAsState()

    // Estados de colapso para navegación ágil (excepto info de la canción y reproducción en cabina)
    var isSimulatorExpanded by remember { mutableStateOf(false) }
    var isServiceExpanded by remember { mutableStateOf(false) }
    var isTelemetryExpanded by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CockpitSurface)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // -------------------------------------------------------------
        // 1. PRIMERO: CANCIÓN ACTUAL (SIEMPRE VISIBLE - NO COLAPSABLE)
        // -------------------------------------------------------------
        WazeBasicControlBar(
            trackInfo = currentTrack,
            onPrevious = { viewModel.onPreviousClicked() },
            onPlayPause = { viewModel.onPlayPauseClicked() },
            onNext = { viewModel.onNextClicked() },
            onOpenServiceSelector = { viewModel.openServiceSelector() }
        )

        // -------------------------------------------------------------
        // 2. SEGUNDO: REPRODUCCIÓN EN CABINA / LOCUCIÓN (SIEMPRE VISIBLE)
        // -------------------------------------------------------------
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CockpitSurfaceContainerLow)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 18.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Modo Programado Badge
                val scheduleBadgeText = when (config.scheduleMode) {
                    AnnouncementScheduleMode.DEFAULT_TOP_OF_HOUR -> "AUTO: HORA EN PUNTO FM"
                    AnnouncementScheduleMode.SONG_COUNT -> "CADA ${config.songsInterval} CANCIONES"
                    AnnouncementScheduleMode.EXACT_TIME_INTERRUPT -> "INTERRUPCIÓN EXACTA :00"
                    AnnouncementScheduleMode.CUSTOM_HOURS -> "HORARIOS PROGRAMADOS (${config.customScheduledHours.size}H)"
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(CockpitSurfaceContainerHigh)
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = CockpitPrimaryContainer,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = scheduleBadgeText,
                            style = MaterialTheme.typography.labelSmall,
                            color = CockpitOnSurface,
                            letterSpacing = 0.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                val isSpeaking = isExecuting || waveState.isEmitting
                val isPlayingMusic = currentTrack.isPlaying

                CentralAnnouncerPowerButton(
                    isEnabled = config.isEnabled,
                    isSpeaking = isSpeaking,
                    isPlayingMusic = isPlayingMusic,
                    waveState = waveState,
                    voiceStyleLabel = config.voiceStyle,
                    onToggle = { viewModel.toggleAnnouncer() }
                )
            }
        }

        // -------------------------------------------------------------
        // 3. TERCERO: SIMULADOR DE CABINA (COLAPSABLE / OCULTABLE)
        // -------------------------------------------------------------
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CockpitSurfaceContainerHigh)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Header Colapsable
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { isSimulatorExpanded = !isSimulatorExpanded }
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = CockpitPrimaryContainer,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "SIMULADOR DE CABINA",
                            style = MaterialTheme.typography.labelSmall,
                            color = CockpitOnSurface,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (isSimulatorExpanded) "Ocultar" else "Abrir prueba",
                            style = MaterialTheme.typography.bodySmall,
                            color = CockpitOnSurfaceVariant,
                            fontSize = 11.sp
                        )
                        Icon(
                            imageVector = if (isSimulatorExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = CockpitPrimaryContainer,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Contenido Desplegable
                AnimatedVisibility(
                    visible = isSimulatorExpanded,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 14.dp, end = 14.dp, bottom = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Ensaya la atenuación de volumen y la locución en cualquier momento sin esperar la hora exacta:",
                            style = MaterialTheme.typography.bodySmall,
                            color = CockpitOnSurfaceVariant,
                            fontSize = 11.sp
                        )

                        // Actuador de simulación directa
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(CockpitSurfaceContainerHighest)
                                .clickable(enabled = !isExecuting) {
                                    viewModel.triggerSimulationNow()
                                }
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(CockpitPrimaryContainer.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayCircle,
                                            contentDescription = null,
                                            tint = CockpitPrimaryContainer,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = if (isExecuting) "Transmitiendo locución..." else "Probar locución y fade ahora",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = if (isExecuting) CockpitPrimaryContainer else CockpitOnSurface,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Atenúa música · Lanza locución · Restaura audio",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = CockpitOnSurfaceVariant
                                        )
                                    }
                                }

                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = CockpitPrimaryContainer,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // 4. CUARTO: SERVICIO DE 2DO PLANO (COLAPSABLE / OCULTABLE)
        // -------------------------------------------------------------
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CockpitSurfaceContainer)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header Colapsable
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { isServiceExpanded = !isServiceExpanded }
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val indicatorColor = when (config.backgroundServiceMode) {
                            BackgroundServiceMode.ON -> CockpitPrimaryContainer
                            BackgroundServiceMode.AUTO -> CockpitTertiary
                            BackgroundServiceMode.OFF -> CockpitOnSurfaceVariant.copy(alpha = 0.4f)
                        }
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(indicatorColor)
                        )
                        Text(
                            text = "SERVICIO EN 2° PLANO",
                            style = MaterialTheme.typography.labelSmall,
                            color = CockpitPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(indicatorColor.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = config.backgroundServiceMode.name,
                                style = MaterialTheme.typography.labelSmall,
                                color = indicatorColor,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (isServiceExpanded) "Ocultar" else "Configurar",
                            style = MaterialTheme.typography.bodySmall,
                            color = CockpitOnSurfaceVariant,
                            fontSize = 11.sp
                        )
                        Icon(
                            imageVector = if (isServiceExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = CockpitPrimaryContainer,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Contenido Desplegable
                AnimatedVisibility(
                    visible = isServiceExpanded,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 14.dp, end = 14.dp, bottom = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Controla la persistencia del monitor en segundo plano para detectar Spotify o YouTube Music sin interrupciones:",
                            style = MaterialTheme.typography.bodySmall,
                            color = CockpitOnSurfaceVariant,
                            fontSize = 11.sp
                        )

                        // Segmented Button Interactivo: ON / OFF / AUTO
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(CockpitSurfaceContainerLowest)
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf(
                                Triple(BackgroundServiceMode.ON, "ON", "Siempre activo"),
                                Triple(BackgroundServiceMode.OFF, "OFF", "Detenido"),
                                Triple(BackgroundServiceMode.AUTO, "AUTO", "Inteligente")
                            ).forEach { (mode, label, desc) ->
                                val isSelected = config.backgroundServiceMode == mode
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) CockpitPrimaryContainer else CockpitSurfaceContainerLowest)
                                        .clickable {
                                            viewModel.setBackgroundServiceMode(mode)
                                        }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = if (isSelected) CockpitOnPrimaryContainer else CockpitOnSurface,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = desc,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isSelected) CockpitOnPrimaryContainer.copy(alpha = 0.8f) else CockpitOnSurfaceVariant,
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // 5. QUINTO: LO TÉCNICO Y PRÓX ANUNCIO (COLAPSABLE / OCULTABLE)
        // -------------------------------------------------------------
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CockpitSurfaceContainer)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header Colapsable
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { isTelemetryExpanded = !isTelemetryExpanded }
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = null,
                            tint = CockpitPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "MÉTRICAS Y PRÓXIMO ANUNCIO",
                            style = MaterialTheme.typography.labelSmall,
                            color = CockpitOnSurface,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (isTelemetryExpanded) "Ocultar" else "Ver reloj & ducking",
                            style = MaterialTheme.typography.bodySmall,
                            color = CockpitOnSurfaceVariant,
                            fontSize = 11.sp
                        )
                        Icon(
                            imageVector = if (isTelemetryExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = CockpitPrimaryContainer,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Contenido Desplegable (Bento Grid)
                AnimatedVisibility(
                    visible = isTelemetryExpanded,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 14.dp, end = 14.dp, bottom = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Canal A: Nivel Técnico de Música & Ducking
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = CockpitSurfaceContainerLow)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "MÚSICA",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = CockpitOnSurfaceVariant
                                    )
                                    Text(
                                        text = if (isDucking) "Fade -85%" else "Normal 100%",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isDucking) CockpitPrimaryContainer else CockpitPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(36.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(CockpitSurfaceContainerLowest)
                                        .padding(horizontal = 4.dp, vertical = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    val barHeights = if (isDucking) listOf(0.2f, 0.15f, 0.25f, 0.18f) else listOf(0.7f, 0.85f, 0.65f, 0.9f)
                                    barHeights.forEach { fraction ->
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxWidth()
                                                .height((32 * fraction).dp)
                                                .clip(RoundedCornerShape(3.dp))
                                                .background(CockpitPrimaryContainer)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.VolumeDown,
                                            contentDescription = null,
                                            tint = CockpitTertiary,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Text(
                                            text = if (isDucking) "Ducking ON" else "Standby",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = CockpitTertiary,
                                            fontSize = 9.sp
                                        )
                                    }
                                    Text(
                                        text = if (isDucking) "15% Nivel" else "100%",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = CockpitOnSurfaceVariant,
                                        fontSize = 9.sp
                                    )
                                }
                            }
                        }

                        // Canal B: Próximo Anuncio & Cuenta Regresiva
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = CockpitSurfaceContainerLow)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "PRÓXIMO",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = CockpitOnSurfaceVariant
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(CockpitTertiary)
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = nextHourLabel,
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = CockpitOnSurface,
                                    fontWeight = FontWeight.ExtraBold
                                )

                                val minutesRem = countdownSec / 60
                                val secondsRem = countdownSec % 60
                                Text(
                                    text = "En ${minutesRem}m ${secondsRem}s",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CockpitPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Boletín horario",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = CockpitOnSurfaceVariant,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
