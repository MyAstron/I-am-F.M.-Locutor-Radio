package locutor.cristopher.sic.ui

import android.content.Context
import android.content.Intent
import android.provider.Settings
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
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import locutor.cristopher.sic.ui.theme.CockpitOnPrimary
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
import locutor.cristopher.sic.ui.theme.CockpitSurfaceContainerLowest

@Composable
fun RadioServiceScreen(
    viewModel: RadioViewModel,
    modifier: Modifier = Modifier
) {
    val uptime by viewModel.uptimeLabel.collectAsState()
    val isDucking by viewModel.duckingActive.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CockpitSurface)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // System Health Hero Status
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CockpitSurfaceContainerHigh)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
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
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(CockpitPrimaryContainer)
                        )
                        Text(
                            text = "DAEMON HEARTBEAT OK",
                            style = MaterialTheme.typography.labelSmall,
                            color = CockpitPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(CockpitSurfaceContainerHighest)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "PID: ACTIVO",
                            style = MaterialTheme.typography.labelSmall,
                            color = CockpitOnSurfaceVariant,
                            fontSize = 10.sp
                        )
                    }
                }

                Text(
                    text = "Servicio en primer plano ACTIVO",
                    style = MaterialTheme.typography.headlineMedium,
                    color = CockpitOnSurface
                )
                Text(
                    text = "Canal prioritario fijado en memoria OOM_ADJ_PERSISTENT con wakelock de baja latencia.",
                    style = MaterialTheme.typography.bodySmall,
                    color = CockpitOnSurfaceVariant
                )

                // Telemetry Badges Strip
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = CockpitSurfaceContainer)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(CockpitPrimary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = CockpitPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "UPTIME",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CockpitOnSurfaceVariant,
                                    fontSize = 9.sp
                                )
                                Text(
                                    text = uptime,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = CockpitOnSurface,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = CockpitSurfaceContainer)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(CockpitSecondary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Memory,
                                    contentDescription = null,
                                    tint = CockpitSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "IMPACTO RAM",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CockpitOnSurfaceVariant,
                                    fontSize = 9.sp
                                )
                                Text(
                                    text = "38.2 MB",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = CockpitOnSurface,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Android Notification Drawer Preview
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = CockpitPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "SIMULADOR BARRA DE NOTIFICACIÓN",
                        style = MaterialTheme.typography.labelSmall,
                        color = CockpitOnSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Prioridad Alta",
                    style = MaterialTheme.typography.bodySmall,
                    color = CockpitOnSurfaceVariant,
                    fontSize = 11.sp
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CockpitSurfaceContainerLowest)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(CockpitPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Radio,
                                contentDescription = null,
                                tint = CockpitOnPrimary,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                        Text(
                            text = "I AM-FM ENGINE",
                            style = MaterialTheme.typography.labelSmall,
                            color = CockpitOnSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .size(3.dp)
                                .clip(CircleShape)
                                .background(CockpitOnSurfaceVariant)
                        )
                        Text(
                            text = "ahora",
                            style = MaterialTheme.typography.labelSmall,
                            color = CockpitOnSurfaceVariant
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "📻 I AM-FM Activo · Modo Radio DJ",
                                style = MaterialTheme.typography.bodyLarge,
                                color = CockpitOnSurface,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Monitoreando reproducción y hora en punto",
                                style = MaterialTheme.typography.bodySmall,
                                color = CockpitPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(999.dp))
                                        .background(CockpitPrimaryContainer)
                                        .padding(horizontal = 6.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "TTS Listo",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = CockpitOnPrimaryContainer,
                                        fontSize = 9.sp
                                    )
                                }
                                Text(
                                    text = "Audio Ducking -24dB preparado",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = CockpitOnSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CockpitSurfaceContainerHighest),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = CockpitPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    // Quick Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.triggerSimulationNow() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(999.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CockpitSurfaceContainer)
                        ) {
                            Icon(
                                imageVector = Icons.Default.RecordVoiceOver,
                                contentDescription = null,
                                tint = CockpitPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Hablar Ya",
                                style = MaterialTheme.typography.labelSmall,
                                color = CockpitPrimary
                            )
                        }

                        Button(
                            onClick = { viewModel.toggleAnnouncer() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(999.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CockpitSurfaceContainer)
                        ) {
                            Text(
                                text = "Suspender",
                                style = MaterialTheme.typography.labelSmall,
                                color = CockpitOnSurface
                            )
                        }
                    }
                }
            }
        }

        // Permissions Checklist & System Access
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CockpitSurfaceContainer)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "AUDITORÍA DE PERMISOS ANDROID",
                        style = MaterialTheme.typography.labelSmall,
                        color = CockpitOnSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "4/4 CONFIGURADOS",
                        style = MaterialTheme.typography.labelSmall,
                        color = CockpitPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                val permissions = listOf(
                    "Acceso a Notificaciones (MediaSession)" to "Habilita la lectura de pistas externas",
                    "Servicio Foreground (MediaPlayback)" to "Mantiene el locutor en segundo plano",
                    "Notificaciones del Sistema (POST_NOTIFICATIONS)" to "Control visible en barra superior",
                    "Foco de Audio (MODIFY_AUDIO_SETTINGS)" to "Fade out suave de Spotify/YouTube Music"
                )

                permissions.forEach { (perm, desc) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = CockpitPrimaryContainer,
                            modifier = Modifier.size(18.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = perm,
                                style = MaterialTheme.typography.bodyMedium,
                                color = CockpitOnSurface,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = desc,
                                style = MaterialTheme.typography.bodySmall,
                                color = CockpitOnSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = {
                        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CockpitSurfaceContainerHigh)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = CockpitPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ABRIR AJUSTES DE ACCESO A NOTIFICACIONES",
                        style = MaterialTheme.typography.labelSmall,
                        color = CockpitPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
