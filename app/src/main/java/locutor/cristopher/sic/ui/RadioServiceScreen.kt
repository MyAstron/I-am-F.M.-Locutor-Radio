package locutor.cristopher.sic.ui

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import locutor.cristopher.sic.domain.model.BackgroundServiceMode
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
import locutor.cristopher.sic.ui.theme.CockpitSurfaceContainerLow
import locutor.cristopher.sic.ui.theme.CockpitSurfaceContainerLowest
import locutor.cristopher.sic.ui.theme.CockpitTertiary

@Composable
fun RadioServiceScreen(
    viewModel: RadioViewModel,
    modifier: Modifier = Modifier
) {
    val uptime by viewModel.uptimeLabel.collectAsState()
    val isDucking by viewModel.duckingActive.collectAsState()
    val currentTrack by viewModel.currentTrack.collectAsState()
    val config by viewModel.announcerConfig.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val isNotificationAccessGranted = remember(currentTrack) { viewModel.isNotificationAccessGranted() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CockpitSurface)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // =========================================================================
        // APARTADO CRÍTICO: PERMISO DE LECTURA DE NOTIFICACIONES MULTIMEDIA
        // =========================================================================
        if (!isNotificationAccessGranted) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CockpitPrimaryContainer.copy(alpha = 0.15f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(CockpitPrimaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = CockpitOnPrimaryContainer,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Lectura de Notificaciones Desactivada",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = CockpitPrimaryContainer,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "Para que el locutor lea las canciones de Spotify, YouTube Music y otros reproductores, concede el acceso a notificaciones en Android.",
                                style = MaterialTheme.typography.bodySmall,
                                color = CockpitOnSurfaceVariant
                            )
                        }
                    }

                    Button(
                        onClick = {
                            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CockpitPrimaryContainer)
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = CockpitOnPrimaryContainer,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "HABILITAR ACCESO A NOTIFICACIONES",
                            style = MaterialTheme.typography.labelSmall,
                            color = CockpitOnPrimaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // =========================================================================
        // CONTROL MAESTRO DE 2DO PLANO (ON / OFF / AUTO)
        // =========================================================================
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
                            text = "MODO DE SERVICIO EN SEGUNDO PLANO",
                            style = MaterialTheme.typography.labelSmall,
                            color = CockpitPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "Configuración Activa",
                        style = MaterialTheme.typography.bodySmall,
                        color = CockpitOnSurfaceVariant,
                        fontSize = 11.sp
                    )
                }

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

        // =========================================================================
        // TARJETA DE REPRODUCTOR / APP DE MÚSICA DETECTADA
        // =========================================================================
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CockpitSurfaceContainer)
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
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(CockpitPrimaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Audiotrack,
                                contentDescription = null,
                                tint = CockpitOnPrimaryContainer,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = "SERVICIO DE MÚSICA VINCULADO",
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
                            text = if (currentTrack.isPlaying) "EN REPRODUCCIÓN" else "EN ESPERA",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (currentTrack.isPlaying) CockpitPrimaryContainer else CockpitOnSurfaceVariant,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(CockpitSurfaceContainerHigh),
                        contentAlignment = Alignment.Center
                    ) {
                        if (currentTrack.albumArtBitmap != null) {
                            Image(
                                bitmap = currentTrack.albumArtBitmap!!.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = CockpitPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (currentTrack.hasTrack) currentTrack.title else "Sin pista detectada",
                            style = MaterialTheme.typography.bodyLarge,
                            color = CockpitOnSurface,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (currentTrack.hasTrack) "${currentTrack.artist} · ${currentTrack.appName}" else "Esperando reproductor multimedia externo",
                            style = MaterialTheme.typography.bodySmall,
                            color = CockpitOnSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // =========================================================================
        // TELEMETRÍA DE PROCESO Y UPTIME
        // =========================================================================
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
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(CockpitPrimaryContainer)
                        )
                        Text(
                            text = "ESTADO DEL NÚCLEO FOREGROUND",
                            style = MaterialTheme.typography.labelSmall,
                            color = CockpitPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(CockpitSurfaceContainerLowest)
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
                    text = "Canal prioritario fijado en memoria OOM_ADJ_PERSISTENT con audio focus detector de baja latencia.",
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

        // =========================================================================
        // AUDITORÍA DE PERMISOS ANDROID
        // =========================================================================
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
                        text = if (isNotificationAccessGranted) "TODOS CONFIGURADOS" else "ATENCIÓN REQUERIDA",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isNotificationAccessGranted) CockpitPrimaryContainer else CockpitTertiary,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Permiso 1: Notificaciones Multimedia
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                            context.startActivity(intent)
                        }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = if (isNotificationAccessGranted) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (isNotificationAccessGranted) CockpitPrimaryContainer else CockpitTertiary,
                        modifier = Modifier.size(18.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Acceso a Notificaciones (MediaSession)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = CockpitOnSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isNotificationAccessGranted) "Habilita la lectura de pistas externas · ACTIVO" else "Toca para conceder acceso en Ajustes · PENDIENTE",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isNotificationAccessGranted) CockpitOnSurfaceVariant else CockpitTertiary
                        )
                    }
                }

                // Permiso 2: Servicio Foreground
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
                            text = "Servicio Foreground (MediaPlayback)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = CockpitOnSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Mantiene el locutor en segundo plano sin ser eliminado por el OS",
                            style = MaterialTheme.typography.bodySmall,
                            color = CockpitOnSurfaceVariant
                        )
                    }
                }

                // Permiso 3: Foco de Audio
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
                            text = "Foco de Audio (AudioManager Fade)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = CockpitOnSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Atenuación suave (ducking) de Spotify y YouTube Music",
                            style = MaterialTheme.typography.bodySmall,
                            color = CockpitOnSurfaceVariant
                        )
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
                        text = "CONFIGURAR ACCESO A NOTIFICACIONES",
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
