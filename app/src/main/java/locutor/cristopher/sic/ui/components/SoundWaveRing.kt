package locutor.cristopher.sic.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
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

    // Nivel de intensidad acústica (0f a 1f)
    val dynamicIntensity = if (isSpeaking) {
        waveState.intensity.coerceIn(0.1f, 1.0f)
    } else if (isPlayingMusic) {
        // En reproducción de música, ritmo pulsante continuo
        0.65f
    } else if (isEnabled) {
        0.20f
    } else {
        0.0f
    }

    // "Cuando haya más bulla genere ondas más amplias y caso contrario sean más armónicas"
    val isHighNoise = dynamicIntensity > 0.55f

    // 1. Escala exterior: en más bulla se expande hasta 2.0x, en modo armónico es suave (~1.22x)
    val outerTargetScale = when {
        !isEnabled -> 1.0f
        isSpeaking && isHighNoise -> 1.85f + (dynamicIntensity * 0.30f)
        isSpeaking -> 1.35f + (dynamicIntensity * 0.15f)
        isPlayingMusic && isHighNoise -> 1.60f
        isPlayingMusic -> 1.28f
        else -> 1.12f
    }

    val outerDuration = when {
        !isWaveActive -> 2600
        isHighNoise -> 550 // Ritmo rápido y amplio en volumen alto / bulla
        else -> 1600       // Ritmo pausado y armónico
    }

    val outerScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = outerTargetScale,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = outerDuration,
                easing = if (isHighNoise) FastOutSlowInEasing else LinearOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "OuterRingScale"
    )

    // 2. Anillo medio: modulación reactiva directa
    val midTargetScale = when {
        !isEnabled -> 1.0f
        isSpeaking && isHighNoise -> 1.55f + (dynamicIntensity * 0.20f)
        isSpeaking -> 1.22f + (dynamicIntensity * 0.10f)
        isPlayingMusic && isHighNoise -> 1.40f
        isPlayingMusic -> 1.18f
        else -> 1.08f
    }

    val midDuration = when {
        !isWaveActive -> 2000
        isHighNoise -> 420
        else -> 1200
    }

    val midScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = midTargetScale,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = midDuration,
                easing = if (isHighNoise) FastOutSlowInEasing else LinearOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "MidRingScale"
    )

    // Opacidades armónicas
    val outerAlpha = when {
        !isEnabled -> 0.0f
        isHighNoise -> 0.45f
        isWaveActive -> 0.25f
        else -> 0.10f
    }

    val midAlpha = when {
        !isEnabled -> 0.0f
        isHighNoise -> 0.60f
        isWaveActive -> 0.38f
        else -> 0.16f
    }

    val innerAlpha = when {
        !isEnabled -> 0.0f
        isHighNoise -> 0.75f
        isWaveActive -> 0.50f
        else -> 0.22f
    }

    Box(
        modifier = modifier.size(baseSize * 2.1f),
        contentAlignment = Alignment.Center
    ) {
        // Onda 3: Exterior (Onda expansiva amplia o armónica)
        if (isEnabled && isWaveActive) {
            Box(
                modifier = Modifier
                    .size(baseSize * 1.75f)
                    .scale(outerScale)
                    .background(
                        color = CockpitPrimaryContainer.copy(alpha = outerAlpha),
                        shape = CircleShape
                    )
            )
        }

        // Onda 2: Media (Cuerpo resonante reactivo)
        if (isEnabled) {
            val responsiveBoost = if (isHighNoise) (dynamicIntensity * 0.15f) else 0f
            Box(
                modifier = Modifier
                    .size(baseSize * 1.42f)
                    .scale(midScale + responsiveBoost)
                    .background(
                        color = CockpitPrimaryContainer.copy(alpha = midAlpha),
                        shape = CircleShape
                    )
            )
        }

        // Onda 1: Interior (Halo luminoso cercano de contorno)
        Box(
            modifier = Modifier
                .size(baseSize * 1.16f)
                .background(
                    color = CockpitPrimaryContainer.copy(alpha = innerAlpha),
                    shape = CircleShape
                )
        )
    }
}
