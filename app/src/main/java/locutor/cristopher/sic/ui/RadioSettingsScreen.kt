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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import locutor.cristopher.sic.domain.model.AnnouncerConfig
import locutor.cristopher.sic.domain.usecase.GenerateRadioScriptUseCase
import locutor.cristopher.sic.ui.theme.CockpitOnPrimaryContainer
import locutor.cristopher.sic.ui.theme.CockpitOnSurface
import locutor.cristopher.sic.ui.theme.CockpitOnSurfaceVariant
import locutor.cristopher.sic.ui.theme.CockpitPrimary
import locutor.cristopher.sic.ui.theme.CockpitPrimaryContainer
import locutor.cristopher.sic.ui.theme.CockpitSecondary
import locutor.cristopher.sic.ui.theme.CockpitSurface
import locutor.cristopher.sic.ui.theme.CockpitSurfaceBright
import locutor.cristopher.sic.ui.theme.CockpitSurfaceContainer
import locutor.cristopher.sic.ui.theme.CockpitSurfaceContainerHigh
import locutor.cristopher.sic.ui.theme.CockpitSurfaceContainerHighest
import locutor.cristopher.sic.ui.theme.CockpitSurfaceContainerLow
import locutor.cristopher.sic.ui.theme.CockpitSurfaceContainerLowest

@Composable
fun RadioSettingsScreen(
    viewModel: RadioViewModel,
    modifier: Modifier = Modifier
) {
    val config by viewModel.announcerConfig.collectAsState()
    val scrollState = rememberScrollState()
    val scriptUseCase = remember { GenerateRadioScriptUseCase() }

    var templateText by remember(config.scriptTemplate) { mutableStateOf(config.scriptTemplate) }
    var pitchVal by remember(config.pitch) { mutableFloatStateOf(config.pitch) }
    var speedVal by remember(config.speechRate) { mutableFloatStateOf(config.speechRate) }
    var fadeDownVal by remember(config.fadeDownDurationMs) { mutableFloatStateOf(config.fadeDownDurationMs / 1000f) }
    var fadeUpVal by remember(config.fadeUpDurationMs) { mutableFloatStateOf(config.fadeUpDurationMs / 1000f) }
    var duckingDepthVal by remember(config.duckingDepthPercent) { mutableFloatStateOf(config.duckingDepthPercent.toFloat()) }

    fun saveConfigChanges() {
        viewModel.updateConfig(
            config.copy(
                scriptTemplate = templateText,
                pitch = pitchVal,
                speechRate = speedVal,
                fadeDownDurationMs = (fadeDownVal * 1000).toLong(),
                fadeUpDurationMs = (fadeUpVal * 1000).toLong(),
                duckingDepthPercent = duckingDepthVal.toInt()
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CockpitSurface)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Section 1: Locutor y Síntesis de Voz (TTS)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CockpitSurfaceContainer)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
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
                            imageVector = Icons.Default.RecordVoiceOver,
                            contentDescription = null,
                            tint = CockpitPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Locutor y Síntesis (TTS)",
                            style = MaterialTheme.typography.headlineSmall,
                            color = CockpitOnSurface
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(CockpitPrimaryContainer.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "MOTOR IA",
                            style = MaterialTheme.typography.labelSmall,
                            color = CockpitPrimary,
                            fontSize = 10.sp
                        )
                    }
                }

                Text(
                    text = "Define la identidad sonora de tu copiloto en antena y la cadencia de modulación.",
                    style = MaterialTheme.typography.bodySmall,
                    color = CockpitOnSurfaceVariant
                )

                // Voice Style Options
                Text(
                    text = "ESTILO DE LOCUCIÓN",
                    style = MaterialTheme.typography.labelSmall,
                    color = CockpitPrimary,
                    fontWeight = FontWeight.Bold
                )

                val voices = listOf(
                    Triple("Radio DJ Dinámico", "Español Latino · Alta energía FM", Icons.Default.ElectricBolt),
                    Triple("Locutor FM Clásico", "Español Neutro · Tono barítono", Icons.Default.Radio),
                    Triple("Voz Conductor Calma", "Sosegado · Para viajes nocturnos", Icons.Default.SelfImprovement)
                )

                voices.forEach { (name, desc, icon) ->
                    val isSelected = config.voiceStyle == name
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) CockpitPrimaryContainer else CockpitSurfaceContainerHigh)
                            .clickable {
                                viewModel.updateConfig(config.copy(voiceStyle = name))
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
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) CockpitSurfaceContainerLowest.copy(alpha = 0.2f) else CockpitSurfaceContainerLowest),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = if (isSelected) CockpitOnPrimaryContainer else CockpitPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = if (isSelected) CockpitOnPrimaryContainer else CockpitOnSurface,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = desc,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (isSelected) CockpitOnPrimaryContainer.copy(alpha = 0.8f) else CockpitOnSurfaceVariant
                                    )
                                }
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = CockpitOnPrimaryContainer,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }

                // Sliders: Pitch & Speed
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CockpitSurfaceContainerLow)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Pitch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "TONO / MODULACIÓN (PITCH)",
                            style = MaterialTheme.typography.labelSmall,
                            color = CockpitOnSurfaceVariant
                        )
                        Text(
                            text = String.format("%.2fx", pitchVal),
                            style = MaterialTheme.typography.labelSmall,
                            color = CockpitPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Slider(
                        value = pitchVal,
                        onValueChange = {
                            pitchVal = it
                            saveConfigChanges()
                        },
                        valueRange = 0.75f..1.35f,
                        colors = SliderDefaults.colors(
                            thumbColor = CockpitPrimaryContainer,
                            activeTrackColor = CockpitPrimaryContainer
                        )
                    )

                    // Speed
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "VELOCIDAD DE LOCUCIÓN",
                            style = MaterialTheme.typography.labelSmall,
                            color = CockpitOnSurfaceVariant
                        )
                        Text(
                            text = String.format("%.2fx", speedVal),
                            style = MaterialTheme.typography.labelSmall,
                            color = CockpitPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Slider(
                        value = speedVal,
                        onValueChange = {
                            speedVal = it
                            saveConfigChanges()
                        },
                        valueRange = 0.80f..1.40f,
                        colors = SliderDefaults.colors(
                            thumbColor = CockpitPrimaryContainer,
                            activeTrackColor = CockpitPrimaryContainer
                        )
                    )

                    // Button: Test Speech Sample
                    Button(
                        onClick = {
                            val sample = scriptUseCase.execute(templateText, "Midnight City", "M83")
                            viewModel.testVoiceSpeech(sample)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CockpitSurfaceContainerHigh)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = CockpitPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ESCUCHAR MUESTRA EN VIVO",
                            style = MaterialTheme.typography.labelSmall,
                            color = CockpitPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Script Template Customizer
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "PLANTILLA DEL SCRIPT",
                        style = MaterialTheme.typography.labelSmall,
                        color = CockpitPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedTextField(
                        value = templateText,
                        onValueChange = {
                            templateText = it
                            saveConfigChanges()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CockpitPrimaryContainer,
                            unfocusedBorderColor = CockpitSurfaceContainerHigh,
                            focusedTextColor = CockpitOnSurface,
                            unfocusedTextColor = CockpitOnSurface
                        )
                    )

                    // Token Chips
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("{canción}", "{artista}", "{hora en punto}").forEach { token ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(CockpitSurfaceContainerHigh)
                                    .clickable {
                                        templateText += " $token"
                                        saveConfigChanges()
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "+ $token",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CockpitSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    // Live Expansion Preview
                    val preview = scriptUseCase.execute(templateText, "Blinding Lights", "The Weeknd")
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(CockpitSurfaceContainerHigh.copy(alpha = 0.6f))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(
                                text = "SIMULACIÓN EN ANTENA",
                                style = MaterialTheme.typography.labelSmall,
                                color = CockpitPrimary,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "\"$preview\"",
                                style = MaterialTheme.typography.bodySmall,
                                color = CockpitOnSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Section 2: Fade y Ducking de Audio
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CockpitSurfaceContainer)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
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
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = CockpitPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Fade y Ducking de Audio",
                            style = MaterialTheme.typography.headlineSmall,
                            color = CockpitOnSurface
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(CockpitSecondary.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "TRANSICIÓN",
                            style = MaterialTheme.typography.labelSmall,
                            color = CockpitSecondary,
                            fontSize = 10.sp
                        )
                    }
                }

                Text(
                    text = "Atenúa la música suavemente para insertar el corte de emisora como en una cabina real.",
                    style = MaterialTheme.typography.bodySmall,
                    color = CockpitOnSurfaceVariant
                )

                // Fade Down Duration
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "DURACIÓN FADE DOWN (BAJADA)",
                            style = MaterialTheme.typography.labelSmall,
                            color = CockpitOnSurfaceVariant
                        )
                        Text(
                            text = String.format("%.1f s", fadeDownVal),
                            style = MaterialTheme.typography.labelSmall,
                            color = CockpitPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Slider(
                        value = fadeDownVal,
                        onValueChange = {
                            fadeDownVal = it
                            saveConfigChanges()
                        },
                        valueRange = 0.5f..3.5f,
                        colors = SliderDefaults.colors(
                            thumbColor = CockpitPrimary,
                            activeTrackColor = CockpitPrimary
                        )
                    )
                }

                // Fade Up Duration
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "DURACIÓN FADE UP (RETORNO)",
                            style = MaterialTheme.typography.labelSmall,
                            color = CockpitOnSurfaceVariant
                        )
                        Text(
                            text = String.format("%.1f s", fadeUpVal),
                            style = MaterialTheme.typography.labelSmall,
                            color = CockpitPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Slider(
                        value = fadeUpVal,
                        onValueChange = {
                            fadeUpVal = it
                            saveConfigChanges()
                        },
                        valueRange = 0.5f..4.0f,
                        colors = SliderDefaults.colors(
                            thumbColor = CockpitPrimary,
                            activeTrackColor = CockpitPrimary
                        )
                    )
                }

                // Ducking Depth
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "VOLUMEN FONDO DURANTE LOCUCIÓN",
                            style = MaterialTheme.typography.labelSmall,
                            color = CockpitOnSurfaceVariant
                        )
                        Text(
                            text = "${duckingDepthVal.toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = CockpitPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Slider(
                        value = duckingDepthVal,
                        onValueChange = {
                            duckingDepthVal = it
                            saveConfigChanges()
                        },
                        valueRange = 5f..40f,
                        colors = SliderDefaults.colors(
                            thumbColor = CockpitPrimary,
                            activeTrackColor = CockpitPrimary
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
