package locutor.cristopher.sic.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import locutor.cristopher.sic.domain.model.AudioWaveState
import locutor.cristopher.sic.ui.theme.CockpitPrimaryContainer

@Composable
fun SoundWaveRing(
    waveState: AudioWaveState,
    isEnabled: Boolean,
    isSpeaking: Boolean = false,
    isPlayingMusic: Boolean = false,
    baseSize: Dp = 128.dp,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "WaveAnimation")

    val isWaveActive = isSpeaking || isPlayingMusic

    // Ancho de expansión de la onda exterior (más enérgica si habla el locutor, rítmica si es música)
    val outerTargetScale = when {
        isSpeaking -> 1.55f
        isPlayingMusic -> 1.42f
        isEnabled -> 1.15f
        else -> 1.0f
    }

    val outerDuration = when {
        isSpeaking -> 750
        isPlayingMusic -> 1200
        else -> 2400
    }

    val outerScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = outerTargetScale,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = outerDuration, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "OuterRingScale"
    )

    // Ancho de la onda media
    val midTargetScale = when {
        isSpeaking -> 1.32f
        isPlayingMusic -> 1.25f
        isEnabled -> 1.08f
        else -> 1.0f
    }

    val midDuration = when {
        isSpeaking -> 550
        isPlayingMusic -> 900
        else -> 1800
    }

    val midScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = midTargetScale,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = midDuration, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "MidRingScale"
    )

    val outerAlpha = when {
        isSpeaking -> 0.40f
        isPlayingMusic -> 0.30f
        isEnabled -> 0.12f
        else -> 0.0f
    }

    val midAlpha = when {
        isSpeaking -> 0.55f
        isPlayingMusic -> 0.42f
        isEnabled -> 0.20f
        else -> 0.0f
    }

    val innerAlpha = when {
        isSpeaking -> 0.70f
        isPlayingMusic -> 0.55f
        isEnabled -> 0.30f
        else -> 0.0f
    }

    Box(
        modifier = modifier.size(baseSize * 1.85f),
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

        // Mid Ring 2 (Reactivo a la modulación de voz o compás musical)
        val dynamicModulation = if (isSpeaking) (waveState.intensity * 0.18f) else 0f
        Box(
            modifier = Modifier
                .size(baseSize * 1.35f)
                .scale(midScale + dynamicModulation)
                .background(
                    color = CockpitPrimaryContainer.copy(alpha = midAlpha),
                    shape = CircleShape
                )
        )

        // Inner Ring 1 (Halo luminoso cercano permanente)
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
