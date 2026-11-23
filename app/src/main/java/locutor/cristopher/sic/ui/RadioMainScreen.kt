package locutor.cristopher.sic.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.Hearing
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import locutor.cristopher.sic.ui.components.CentralAnnouncerPowerButton
import locutor.cristopher.sic.ui.components.WazeBasicControlBar
import locutor.cristopher.sic.ui.theme.CockpitOnPrimaryContainer
import locutor.cristopher.sic.ui.theme.CockpitOnSurface
import locutor.cristopher.sic.ui.theme.CockpitOnSurfaceVariant
import locutor.cristopher.sic.ui.theme.CockpitPrimary
import locutor.cristopher.sic.ui.theme.CockpitPrimaryContainer
import locutor.cristopher.sic.ui.theme.CockpitSurface
import locutor.cristopher.sic.ui.theme.CockpitSurfaceBright
import locutor.cristopher.sic.ui.theme.CockpitSurfaceContainer
import locutor.cristopher.sic.ui.theme.CockpitSurfaceContainerHigh
import locutor.cristopher.sic.ui.theme.CockpitSurfaceContainerHighest
import locutor.cristopher.sic.ui.theme.CockpitSurfaceContainerLow
import locutor.cristopher.sic.ui.theme.CockpitSurfaceContainerLowest
import locutor.cristopher.sic.ui.theme.CockpitTertiary
import locutor.cristopher.sic.ui.theme.CockpitTertiaryContainer

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

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CockpitSurface)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Background Service Telemetry Status Strip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CockpitSurfaceContainerLow)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(CockpitPrimaryContainer)
                )
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "SERVICIO EN 2° PLANO",
                            style = MaterialTheme.typography.labelSmall,
                            color = CockpitPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(CockpitPrimaryContainer.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "ACTIVO",
                                style = MaterialTheme.typography.labelSmall,
                                color = CockpitPrimaryContainer,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        text = "Audio focus detector enlazado · Latencia 12ms",
                        style = MaterialTheme.typography.bodySmall,
                        color = CockpitOnSurfaceVariant
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(CockpitSurfaceContainerHigh)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Hearing,
                        contentDescription = null,
                        tint = CockpitPrimaryContainer,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "AUTO",
                        style = MaterialTheme.typography.labelSmall,
                        color = CockpitOnSurfaceVariant
                    )
                }
            }
        }

        // 2. Waze-Style Floating External Music Player Card
        WazeBasicControlBar(
            trackInfo = currentTrack,
            onPrevious = { viewModel.onPreviousClicked() },
            onPlayPause = { viewModel.onPlayPauseClicked() },
            onNext = { viewModel.onNextClicked() }
        )

        // 3. Center Stage - Tactile Master Locutor Switch Button with Sound Waves
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CockpitSurfaceContainerLow)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Mode Badge
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
                            text = "AUTO: ANUNCIO EN HORA EN PUNTO",
                            style = MaterialTheme.typography.labelSmall,
                            color = CockpitOnSurface,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                CentralAnnouncerPowerButton(
                    isEnabled = config.isEnabled,
                    waveState = waveState,
                    voiceStyleLabel = config.voiceStyle,
                    onToggle = { viewModel.toggleAnnouncer() }
                )
            }
        }

        // 4. Audio Fade & Voice Prioritization Live HUD (Bento Grid)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Channel A: Music Volume with Auto-Duck Meter
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CockpitSurfaceContainer)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
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

                    Spacer(modifier = Modifier.height(10.dp))

                    // Graphic Level Meter Bars
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CockpitSurfaceContainerLowest)
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val barHeights = if (isDucking) listOf(0.2f, 0.15f, 0.25f, 0.18f) else listOf(0.7f, 0.85f, 0.65f, 0.9f)
                        barHeights.forEach { fraction ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .height((40 * fraction).dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(CockpitPrimaryContainer)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

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
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = if (isDucking) "Ducking ON" else "Standby",
                                style = MaterialTheme.typography.labelSmall,
                                color = CockpitTertiary,
                                fontSize = 10.sp
                            )
                        }
                        Text(
                            text = if (isDucking) "15% Nivel" else "100% Nivel",
                            style = MaterialTheme.typography.labelSmall,
                            color = CockpitOnSurfaceVariant,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            // Channel B: Next Broadcast Countdown Clock
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CockpitSurfaceContainer)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PRÓX. ANUNCIO",
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

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = nextHourLabel,
                        style = MaterialTheme.typography.headlineLarge,
                        color = CockpitOnSurface,
                        fontWeight = FontWeight.ExtraBold
                    )

                    val minutesRem = countdownSec / 60
                    val secondsRem = countdownSec % 60
                    Text(
                        text = "En ${minutesRem}m ${secondsRem}s",
                        style = MaterialTheme.typography.labelSmall,
                        color = CockpitPrimary,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = null,
                            tint = CockpitPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Canción + Hora",
                            style = MaterialTheme.typography.bodySmall,
                            color = CockpitOnSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // 5. Instant Test Simulation Cockpit Bar
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CockpitSurfaceContainerHigh)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
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
                    Text(
                        text = "Sin esperar la hora",
                        style = MaterialTheme.typography.bodySmall,
                        color = CockpitOnSurfaceVariant,
                        fontSize = 11.sp
                    )
                }

                // Big Trigger Actuator Button
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

        Spacer(modifier = Modifier.height(16.dp))
    }
}
