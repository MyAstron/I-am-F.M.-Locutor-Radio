package locutor.cristopher.sic.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import locutor.cristopher.sic.domain.model.AudioWaveState
import locutor.cristopher.sic.ui.theme.CockpitOnPrimaryContainer
import locutor.cristopher.sic.ui.theme.CockpitOnSurfaceVariant
import locutor.cristopher.sic.ui.theme.CockpitPrimaryContainer
import locutor.cristopher.sic.ui.theme.CockpitSurfaceContainerHigh
import locutor.cristopher.sic.ui.theme.CockpitSurfaceContainerHighest

@Composable
fun CentralAnnouncerPowerButton(
    isEnabled: Boolean,
    waveState: AudioWaveState,
    voiceStyleLabel: String,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1.0f,
        animationSpec = tween(durationMillis = 150),
        label = "ButtonPressScale"
    )

    val coreBgColor by animateColorAsState(
        targetValue = if (isEnabled) CockpitPrimaryContainer else CockpitSurfaceContainerHigh,
        animationSpec = tween(durationMillis = 300),
        label = "CoreBgColor"
    )

    val iconColor by animateColorAsState(
        targetValue = if (isEnabled) CockpitOnPrimaryContainer else CockpitOnSurfaceVariant,
        animationSpec = tween(durationMillis = 300),
        label = "IconColor"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            // Ondas sonoras reactivas
            SoundWaveRing(
                waveState = waveState,
                isEnabled = isEnabled,
                baseSize = 132.dp
            )

            // Botón central maestro
            Box(
                modifier = Modifier
                    .size(132.dp)
                    .scale(scale)
                    .shadow(
                        elevation = if (isEnabled) 16.dp else 4.dp,
                        shape = CircleShape,
                        spotColor = CockpitPrimaryContainer
                    )
                    .clip(CircleShape)
                    .background(CockpitSurfaceContainerHighest)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onToggle
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Cara interna del botón
                Box(
                    modifier = Modifier
                        .size(114.dp)
                        .clip(CircleShape)
                        .background(coreBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (isEnabled) Icons.Default.Mic else Icons.Default.MicOff,
                            contentDescription = if (isEnabled) "Locutor Encendido" else "Locutor Apagado",
                            tint = iconColor,
                            modifier = Modifier.size(38.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "LOCUTOR",
                            style = MaterialTheme.typography.labelSmall,
                            color = iconColor,
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isEnabled) "ON" else "MUTED",
                            style = MaterialTheme.typography.labelMedium,
                            color = iconColor,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Subtexto de telemetría del locutor
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (isEnabled) CockpitPrimaryContainer else CockpitOnSurfaceVariant)
            )
            Text(
                text = "Voz: $voiceStyleLabel",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }

        Text(
            text = if (isEnabled) "Priorización con fade y anuncio horario activada" else "Locución horaria en pausa",
            style = MaterialTheme.typography.bodySmall,
            color = CockpitOnSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
