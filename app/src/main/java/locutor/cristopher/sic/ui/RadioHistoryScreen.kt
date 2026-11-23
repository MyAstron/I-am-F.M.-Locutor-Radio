package locutor.cristopher.sic.ui

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Tune
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import locutor.cristopher.sic.ui.theme.CockpitOnPrimaryContainer
import locutor.cristopher.sic.ui.theme.CockpitOnSurface
import locutor.cristopher.sic.ui.theme.CockpitOnSurfaceVariant
import locutor.cristopher.sic.ui.theme.CockpitPrimary
import locutor.cristopher.sic.ui.theme.CockpitPrimaryContainer
import locutor.cristopher.sic.ui.theme.CockpitSecondary
import locutor.cristopher.sic.ui.theme.CockpitSurface
import locutor.cristopher.sic.ui.theme.CockpitSurfaceContainer
import locutor.cristopher.sic.ui.theme.CockpitSurfaceContainerHigh
import locutor.cristopher.sic.ui.theme.CockpitSurfaceContainerHighest
import locutor.cristopher.sic.ui.theme.CockpitSurfaceContainerLow

@Composable
fun RadioHistoryScreen(
    viewModel: RadioViewModel,
    modifier: Modifier = Modifier
) {
    val historyLogs by viewModel.historyLogs.collectAsState()
    var selectedFilter by remember { mutableStateOf("all") }

    val filteredLogs = when (selectedFilter) {
        "auto" -> historyLogs.filter { it.isAutomatic }
        "manual" -> historyLogs.filter { !it.isAutomatic }
        else -> historyLogs
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CockpitSurface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Dynamic Audio Telemetry HUD Header
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
                    imageVector = Icons.Default.Insights,
                    contentDescription = null,
                    tint = CockpitPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "TELEMETRÍA DE EMISIÓN",
                    style = MaterialTheme.typography.labelSmall,
                    color = CockpitOnSurface,
                    fontWeight = FontWeight.Bold
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(CockpitSurfaceContainerHigh)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "SESIÓN ACTIVA",
                    style = MaterialTheme.typography.labelSmall,
                    color = CockpitPrimaryContainer,
                    fontSize = 10.sp
                )
            }
        }

        // 3 Metrics Cockpit Tiles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Tile 1: Total
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CockpitSurfaceContainerHigh)
            ) {
                Column(
                    modifier = Modifier.padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "TOTAL",
                            style = MaterialTheme.typography.labelSmall,
                            color = CockpitOnSurfaceVariant,
                            fontSize = 10.sp
                        )
                        Icon(
                            imageVector = Icons.Default.RecordVoiceOver,
                            contentDescription = null,
                            tint = CockpitPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(
                        text = "${historyLogs.size}",
                        style = MaterialTheme.typography.headlineLarge,
                        color = CockpitOnSurface,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Hoy en ruta",
                        style = MaterialTheme.typography.labelSmall,
                        color = CockpitPrimary,
                        fontSize = 10.sp
                    )
                }
            }

            // Tile 2: Última
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CockpitSurfaceContainerHigh)
            ) {
                Column(
                    modifier = Modifier.padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "ÚLTIMA",
                            style = MaterialTheme.typography.labelSmall,
                            color = CockpitOnSurfaceVariant,
                            fontSize = 10.sp
                        )
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = CockpitPrimaryContainer,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    val lastTime = historyLogs.firstOrNull()?.timeLabel ?: "--:--"
                    Text(
                        text = lastTime,
                        style = MaterialTheme.typography.headlineSmall,
                        color = CockpitOnSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "En punto",
                        style = MaterialTheme.typography.labelSmall,
                        color = CockpitOnSurfaceVariant,
                        fontSize = 10.sp
                    )
                }
            }

            // Tile 3: Fade OK
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CockpitSurfaceContainerHigh)
            ) {
                Column(
                    modifier = Modifier.padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "FADE OK",
                            style = MaterialTheme.typography.labelSmall,
                            color = CockpitOnSurfaceVariant,
                            fontSize = 10.sp
                        )
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = CockpitSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(
                        text = "100%",
                        style = MaterialTheme.typography.headlineLarge,
                        color = CockpitPrimaryContainer,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Precisión ducking",
                        style = MaterialTheme.typography.labelSmall,
                        color = CockpitOnSurfaceVariant,
                        fontSize = 10.sp
                    )
                }
            }
        }

        // Filter Selector Capsules
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filters = listOf(
                Pair("all", "Todas (${historyLogs.size})"),
                Pair("auto", "Automáticas"),
                Pair("manual", "Pruebas")
            )
            filters.forEach { (key, label) ->
                val isSelected = selectedFilter == key
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(if (isSelected) CockpitPrimaryContainer else CockpitSurfaceContainerHigh)
                        .clickable { selectedFilter = key }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) CockpitOnPrimaryContainer else CockpitOnSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Timeline Log List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredLogs, key = { it.id }) { log ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CockpitSurfaceContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
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
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(CockpitPrimaryContainer)
                                )
                                Text(
                                    text = log.timeLabel,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = CockpitOnSurface,
                                    fontWeight = FontWeight.Bold
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(999.dp))
                                        .background(CockpitSurfaceContainerHighest)
                                        .padding(horizontal = 6.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = if (log.isAutomatic) "EN PUNTO" else "SIMULACIÓN",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = CockpitPrimary,
                                        fontSize = 9.sp
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(CockpitPrimaryContainer.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = log.status,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CockpitPrimaryContainer,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Track info chip
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(CockpitSurfaceContainerLow)
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CockpitPrimaryContainer.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MusicNote,
                                    contentDescription = null,
                                    tint = CockpitPrimaryContainer,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${log.appName} · Fade Out",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CockpitPrimary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = log.trackTitle,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = CockpitOnSurface,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = log.artist,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = CockpitOnSurfaceVariant
                                )
                            }
                        }

                        // Voice speech text bubble
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(CockpitSurfaceContainerHighest.copy(alpha = 0.6f))
                                .padding(10.dp)
                        ) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.RecordVoiceOver,
                                        contentDescription = null,
                                        tint = CockpitPrimary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "LOCUCIÓN EMITIDA",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = CockpitPrimary,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "\"${log.speechText}\"",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = CockpitOnSurface,
                                    fontStyle = FontStyle.Italic
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
