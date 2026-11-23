package locutor.cristopher.sic.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import locutor.cristopher.sic.domain.model.AudioWaveState
import locutor.cristopher.sic.ui.theme.CockpitPrimaryContainer

@Composable
fun SoundWaveRing(
    waveState: AudioWaveState,
    isEnabled: Boolean,
    baseSize: Dp = 128.dp,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "WaveAnimation")

    val outerScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (waveState.isEmitting) 1.55f else if (isEnabled) 1.15f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (waveState.isEmitting) 800 else 2400,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "OuterRingScale"
    )

    val midScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (waveState.isEmitting) 1.30f else if (isEnabled) 1.08f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (waveState.isEmitting) 600 else 1800,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "MidRingScale"
    )

    val outerAlpha = if (waveState.isEmitting) 0.35f else if (isEnabled) 0.12f else 0.0f
    val midAlpha = if (waveState.isEmitting) 0.50f else if (isEnabled) 0.20f else 0.0f
    val innerAlpha = if (waveState.isEmitting) 0.65f else if (isEnabled) 0.30f else 0.0f

    Box(
        modifier = modifier.size(baseSize * 1.8f),
        contentAlignment = Alignment.Center
    ) {
        // Outer Ring 3
        Box(
            modifier = Modifier
                .size(baseSize * 1.6f)
                .scale(outerScale)
                .background(
                    color = CockpitPrimaryContainer.copy(alpha = outerAlpha),
                    shape = CircleShape
                )
        )

        // Mid Ring 2 (Reactivo a la intensidad del habla)
        val dynamicMidScale = midScale + (waveState.intensity * 0.15f)
        Box(
            modifier = Modifier
                .size(baseSize * 1.35f)
                .scale(dynamicMidScale)
                .background(
                    color = CockpitPrimaryContainer.copy(alpha = midAlpha),
                    shape = CircleShape
                )
        )

        // Inner Ring 1 (Halo luminoso cercano)
        Box(
            modifier = Modifier
                .size(baseSize * 1.15f)
                .background(
                    color = CockpitPrimaryContainer.copy(alpha = innerAlpha),
                    shape = CircleShape
                )
        )
    }
}
