package locutor.cristopher.sic.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import locutor.cristopher.sic.domain.model.TrackInfo
import locutor.cristopher.sic.ui.theme.CockpitOnPrimaryContainer
import locutor.cristopher.sic.ui.theme.CockpitOnSurface
import locutor.cristopher.sic.ui.theme.CockpitOnSurfaceVariant
import locutor.cristopher.sic.ui.theme.CockpitPrimary
import locutor.cristopher.sic.ui.theme.CockpitPrimaryContainer
import locutor.cristopher.sic.ui.theme.CockpitSurfaceContainer
import locutor.cristopher.sic.ui.theme.CockpitSurfaceContainerHigh
import locutor.cristopher.sic.ui.theme.CockpitSurfaceContainerHighest
import locutor.cristopher.sic.ui.theme.CockpitSurfaceContainerLowest
import locutor.cristopher.sic.ui.theme.CockpitTertiaryContainer

@Composable
fun WazeBasicControlBar(
    trackInfo: TrackInfo,
    onPrevious: () -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CockpitSurfaceContainer)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header Bar: App Source & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(CockpitSurfaceContainerHighest),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = CockpitPrimaryContainer,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(
                        text = trackInfo.appName.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = CockpitOnSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(CockpitOnSurfaceVariant)
                    )
                    Text(
                        text = "Externo",
                        style = MaterialTheme.typography.bodySmall,
                        color = CockpitOnSurfaceVariant
                    )
                }

                // Status Chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(CockpitTertiaryContainer.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(CockpitTertiaryContainer)
                        )
                        Text(
                            text = if (trackInfo.isPlaying) "Reproduciendo" else "En pausa",
                            style = MaterialTheme.typography.labelSmall,
                            color = CockpitTertiaryContainer,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Track metadata & Artwork placeholder
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CockpitSurfaceContainerHighest),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = null,
                        tint = CockpitPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = trackInfo.title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = CockpitOnSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = trackInfo.artist,
                        style = MaterialTheme.typography.bodyMedium,
                        color = CockpitOnSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${trackInfo.formattedPosition} / ${trackInfo.formattedDuration}",
                            style = MaterialTheme.typography.labelSmall,
                            color = CockpitPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = { trackInfo.progressFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(999.dp)),
                color = CockpitPrimaryContainer,
                trackColor = CockpitSurfaceContainerLowest
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Waze-Style Playback Knobs: Previous, Master Play/Pause, Next (NO playlist)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onPrevious,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CockpitSurfaceContainerHigh,
                        contentColor = CockpitOnSurface
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Anterior",
                        modifier = Modifier.size(26.dp)
                    )
                }

                Button(
                    onClick = onPlayPause,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .shadow(8.dp, RoundedCornerShape(14.dp), spotColor = CockpitPrimaryContainer),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CockpitPrimaryContainer,
                        contentColor = CockpitOnPrimaryContainer
                    )
                ) {
                    Icon(
                        imageVector = if (trackInfo.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (trackInfo.isPlaying) "Pausar" else "Reproducir",
                        modifier = Modifier.size(30.dp)
                    )
                }

                Button(
                    onClick = onNext,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CockpitSurfaceContainerHigh,
                        contentColor = CockpitOnSurface
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Siguiente",
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    }
}
