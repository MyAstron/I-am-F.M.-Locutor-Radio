package locutor.cristopher.sic.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Timelapse
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
import locutor.cristopher.sic.domain.model.AnnouncementScheduleMode
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
import locutor.cristopher.sic.ui.theme.CockpitTertiary

data class PresetOption(
    val name: String,
    val shortName: String,
    val desc: String,
    val pitch: Float,
    val speed: Float,
    val icon: ImageVector
)

@OptIn(ExperimentalAnimationApi::class, ExperimentalLayoutApi::class)
@Composable
fun RadioSettingsScreen(
    viewModel: RadioViewModel,
    modifier: Modifier = Modifier
) {
    val config by viewModel.announcerConfig.collectAsState()
    val availableVoices by viewModel.availableVoices.collectAsState()
    val scrollState = rememberScrollState()
    val scriptUseCase = remember { GenerateRadioScriptUseCase() }

    var templateText by remember(config.scriptTemplate) { mutableStateOf(config.scriptTemplate) }
    var selectedVoice by remember(config.selectedVoiceName) { mutableStateOf(config.selectedVoiceName) }
    var pitchVal by remember(config.pitch) { mutableFloatStateOf(config.pitch) }
    var speedVal by remember(config.speechRate) { mutableFloatStateOf(config.speechRate) }
    var fadeDownVal by remember(config.fadeDownDurationMs) { mutableFloatStateOf(config.fadeDownDurationMs / 1000f) }
    var fadeUpVal by remember(config.fadeUpDurationMs) { mutableFloatStateOf(config.fadeUpDurationMs / 1000f) }
    var duckingDepthVal by remember(config.duckingDepthPercent) { mutableFloatStateOf(config.duckingDepthPercent.toFloat()) }

    val presets = remember {
        listOf(
            PresetOption("Radio DJ Dinámico", "DJ Dinámico", "Español Latino · Alta energía FM y ritmo", 1.10f, 1.15f, Icons.Default.ElectricBolt),
            PresetOption("Locutor FM Clásico", "FM Clásico", "Español Neutro · Tono barítono profundo e institucional", 0.80f, 0.95f, Icons.Default.Radio),
            PresetOption("Locutora FM Estéreo", "Locutora FM", "Voz brillante y cálida · Estilo comercial moderno", 1.30f, 1.05f, Icons.Default.Mic),
            PresetOption("Voz Conductor Calma", "Conductor Calma", "Sosegado y suave · Ideal para autopista nocturna", 0.90f, 0.85f, Icons.Default.SelfImprovement),
            PresetOption("Noticias y Boletín", "Noticiero", "Informativo sobrio · Pronunciación precisa", 1.00f, 1.00f, Icons.Default.Campaign),
            PresetOption("Locutor Urbano Nocturno", "Urbano Nocturno", "Voz ultra grave y pausada · Estilo podcast / chill", 0.70f, 0.90f, Icons.Default.GraphicEq),
            PresetOption("Locutor Juvenil Pop", "Juvenil Pop", "Fresco y dinámico · Radio juvenil contemporánea", 1.25f, 1.20f, Icons.Default.RecordVoiceOver)
        )
    }

    val selectedPreset = presets.find { it.name == config.voiceStyle } ?: presets.first()

    fun saveConfigChanges() {
        viewModel.updateConfig(
            config.copy(
                scriptTemplate = templateText,
                selectedVoiceName = selectedVoice,
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
        // =========================================================================
        // SECCIÓN 1: LOCUTORES FM (Mosaico de Íconos -> Transición Horizontal Completa)
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
                            text = "Estilo de Locutor FM",
                            style = MaterialTheme.typography.headlineSmall,
                            color = CockpitOnSurface,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(CockpitPrimaryContainer.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "7 PRESETS",
                            style = MaterialTheme.typography.labelSmall,
                            color = CockpitPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = "Selecciona un estilo en el mosaico para expandir su formato completo y afinar su modulación:",
                    style = MaterialTheme.typography.bodySmall,
                    color = CockpitOnSurfaceVariant
                )

                // 1. Mosaico de Íconos de Locutores
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CockpitSurfaceContainerLowest)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    presets.forEach { preset ->
                        val isSelected = config.voiceStyle == preset.name
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    pitchVal = preset.pitch
                                    speedVal = preset.speed
                                    viewModel.updateConfig(
                                        config.copy(
                                            voiceStyle = preset.name,
                                            pitch = preset.pitch,
                                            speechRate = preset.speed
                                        )
                                    )
                                }
                                .padding(horizontal = 4.dp, vertical = 6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) CockpitPrimaryContainer else CockpitSurfaceContainerHigh)
                                    .border(
                                        width = if (isSelected) 2.dp else 0.dp,
                                        color = if (isSelected) CockpitPrimary else CockpitSurfaceContainerHigh,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = preset.icon,
                                    contentDescription = preset.name,
                                    tint = if (isSelected) CockpitOnPrimaryContainer else CockpitOnSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = preset.shortName.take(6),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) CockpitPrimary else CockpitOnSurfaceVariant,
                                fontSize = 9.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                // 2. Transición Animada a Formato Horizontal Ocupando el Ancho Completo
                AnimatedContent(
                    targetState = selectedPreset,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    },
                    label = "PresetExpandedTransition"
                ) { activePreset ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(CockpitPrimaryContainer.copy(alpha = 0.12f))
                            .border(1.dp, CockpitPrimaryContainer.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Fila horizontal con avatar grande e información completa
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
                                            .size(46.dp)
                                            .clip(CircleShape)
                                            .background(CockpitPrimaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = activePreset.icon,
                                            contentDescription = null,
                                            tint = CockpitOnPrimaryContainer,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Column {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = activePreset.name,
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = CockpitOnSurface,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(CockpitPrimaryContainer)
                                                    .padding(horizontal = 6.dp, vertical = 1.dp)
                                            ) {
                                                Text(
                                                    text = "ACTIVO",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = CockpitOnPrimaryContainer,
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                        Text(
                                            text = activePreset.desc,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = CockpitOnSurfaceVariant,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }

                            // Botón de prueba rápida con este locutor
                            Button(
                                onClick = {
                                    val sample = scriptUseCase.execute(templateText, "Midnight City", "M83")
                                    viewModel.testVoiceSpeech(sample)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CockpitSurfaceContainerHigh)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = CockpitPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "ESCUCHAR PRUEBA EN VIVO",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CockpitPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // 3. Selección de Voz Nativa del Dispositivo (TTS)
                if (availableVoices.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(CockpitSurfaceContainerLow)
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "VOZ NATIVA DEL SISTEMA (TTS DISPOSITIVO)",
                            style = MaterialTheme.typography.labelSmall,
                            color = CockpitPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        val isDefault = selectedVoice.isBlank()
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isDefault) CockpitPrimaryContainer.copy(alpha = 0.2f) else CockpitSurfaceContainerHigh)
                                .clickable {
                                    selectedVoice = ""
                                    saveConfigChanges()
                                }
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Predeterminada del sistema (Recomendado)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isDefault) CockpitPrimaryContainer else CockpitOnSurface,
                                fontWeight = if (isDefault) FontWeight.Bold else FontWeight.Normal
                            )
                        }

                        availableVoices.take(4).forEach { voiceItem ->
                            val isVoiceSelected = selectedVoice == voiceItem.name
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isVoiceSelected) CockpitPrimaryContainer.copy(alpha = 0.2f) else CockpitSurfaceContainerHigh)
                                    .clickable {
                                        selectedVoice = voiceItem.name
                                        saveConfigChanges()
                                    }
                                    .padding(horizontal = 10.dp, vertical = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = voiceItem.displayName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (isVoiceSelected) CockpitPrimaryContainer else CockpitOnSurface,
                                        fontWeight = if (isVoiceSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                    if (isVoiceSelected) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = CockpitPrimaryContainer,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 4. Sliders de Personalización Fina: Pitch & Speed
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CockpitSurfaceContainerLow)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val pitchLabel = when {
                        pitchVal <= 0.80f -> "Grave"
                        pitchVal >= 1.25f -> "Agudo"
                        else -> "Neutro"
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TONO / MODULACIÓN ($pitchLabel)",
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
                        valueRange = 0.50f..1.80f,
                        colors = SliderDefaults.colors(
                            thumbColor = CockpitPrimaryContainer,
                            activeTrackColor = CockpitPrimaryContainer
                        )
                    )

                    val speedLabel = when {
                        speedVal <= 0.85f -> "Pausada"
                        speedVal >= 1.25f -> "Rápida"
                        else -> "Normal"
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "VELOCIDAD DE LOCUCIÓN ($speedLabel)",
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
                        valueRange = 0.60f..1.60f,
                        colors = SliderDefaults.colors(
                            thumbColor = CockpitPrimaryContainer,
                            activeTrackColor = CockpitPrimaryContainer
                        )
                    )
                }

                // 5. Plantilla del Script
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

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("{canción}", "{artista}", "{hora}", "{clima}").forEach { token ->
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
                }
            }
        }

        // =========================================================================
        // SECCIÓN 2: PERSONALIZACIÓN DE HORARIOS Y FRECUENCIA DE ANUNCIOS
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
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = CockpitPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Horarios y Frecuencia",
                            style = MaterialTheme.typography.headlineSmall,
                            color = CockpitOnSurface,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(CockpitTertiary.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "PROGRAMACIÓN",
                            style = MaterialTheme.typography.labelSmall,
                            color = CockpitTertiary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = "Elige cuándo y con qué frecuencia deseas que el locutor intervenga en tu música:",
                    style = MaterialTheme.typography.bodySmall,
                    color = CockpitOnSurfaceVariant
                )

                // Modo 1: Por Defecto FM (Hora en punto al terminar canción o en reloj)
                val isDefaultMode = config.scheduleMode == AnnouncementScheduleMode.DEFAULT_TOP_OF_HOUR
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isDefaultMode) CockpitPrimaryContainer.copy(alpha = 0.2f) else CockpitSurfaceContainerLow)
                        .clickable {
                            viewModel.setScheduleMode(AnnouncementScheduleMode.DEFAULT_TOP_OF_HOUR)
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
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Radio,
                                contentDescription = null,
                                tint = if (isDefaultMode) CockpitPrimaryContainer else CockpitOnSurfaceVariant,
                                modifier = Modifier.size(22.dp)
                            )
                            Column {
                                Text(
                                    text = "Por Defecto: Hora en Punto FM",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isDefaultMode) CockpitPrimaryContainer else CockpitOnSurface,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Anuncia al terminar la canción cerca de la hora (:00) o puntualmente en el reloj sin cortar temas.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = CockpitOnSurfaceVariant
                                )
                            }
                        }
                        if (isDefaultMode) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = CockpitPrimaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Modo 2: Cada N Canciones Reproducidas
                val isSongCountMode = config.scheduleMode == AnnouncementScheduleMode.SONG_COUNT
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSongCountMode) CockpitPrimaryContainer.copy(alpha = 0.2f) else CockpitSurfaceContainerLow)
                        .clickable {
                            viewModel.setScheduleMode(AnnouncementScheduleMode.SONG_COUNT)
                        }
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatListNumbered,
                                contentDescription = null,
                                tint = if (isSongCountMode) CockpitPrimaryContainer else CockpitOnSurfaceVariant,
                                modifier = Modifier.size(22.dp)
                            )
                            Column {
                                Text(
                                    text = "Cada N Canciones",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isSongCountMode) CockpitPrimaryContainer else CockpitOnSurface,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Emite locución cada vez que termine de sonar una cantidad fija de temas.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = CockpitOnSurfaceVariant
                                )
                            }
                        }
                        if (isSongCountMode) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = CockpitPrimaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    if (isSongCountMode) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(1, 2, 3, 5, 8).forEach { count ->
                                val isCountSelected = config.songsInterval == count
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isCountSelected) CockpitPrimaryContainer else CockpitSurfaceContainerHigh)
                                        .clickable {
                                            viewModel.setSongsInterval(count)
                                        }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$count ${if (count == 1) "tema" else "temas"}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isCountSelected) CockpitOnPrimaryContainer else CockpitOnSurface,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Modo 3: Hora Exacta con Interrupción Inmediata
                val isInterruptMode = config.scheduleMode == AnnouncementScheduleMode.EXACT_TIME_INTERRUPT
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isInterruptMode) CockpitPrimaryContainer.copy(alpha = 0.2f) else CockpitSurfaceContainerLow)
                        .clickable {
                            viewModel.setScheduleMode(AnnouncementScheduleMode.EXACT_TIME_INTERRUPT)
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
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Alarm,
                                contentDescription = null,
                                tint = if (isInterruptMode) CockpitPrimaryContainer else CockpitOnSurfaceVariant,
                                modifier = Modifier.size(22.dp)
                            )
                            Column {
                                Text(
                                    text = "Interrupción Inmediata en la Hora Exacta",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isInterruptMode) CockpitPrimaryContainer else CockpitOnSurface,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Sin importar si una canción está a la mitad, atenúa el volumen en vivo (ducking) e interrumpe para dar la hora puntual.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = CockpitOnSurfaceVariant
                                )
                            }
                        }
                        if (isInterruptMode) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = CockpitPrimaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Modo 4: Horarios Específicos Programados (Horas Fijas)
                val isCustomHoursMode = config.scheduleMode == AnnouncementScheduleMode.CUSTOM_HOURS
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isCustomHoursMode) CockpitPrimaryContainer.copy(alpha = 0.2f) else CockpitSurfaceContainerLow)
                        .clickable {
                            viewModel.setScheduleMode(AnnouncementScheduleMode.CUSTOM_HOURS)
                        }
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timelapse,
                                contentDescription = null,
                                tint = if (isCustomHoursMode) CockpitPrimaryContainer else CockpitOnSurfaceVariant,
                                modifier = Modifier.size(22.dp)
                            )
                            Column {
                                Text(
                                    text = "Horas Específicas Programadas",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isCustomHoursMode) CockpitPrimaryContainer else CockpitOnSurface,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Activa los anuncios solo a las horas del día que elijas (ej. 08:00, 12:00, 18:00).",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = CockpitOnSurfaceVariant
                                )
                            }
                        }
                        if (isCustomHoursMode) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = CockpitPrimaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    if (isCustomHoursMode) {
                        Text(
                            text = "Toca las horas en las que deseas locución:",
                            style = MaterialTheme.typography.labelSmall,
                            color = CockpitPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            (6..22).forEach { hour ->
                                val isHourActive = config.customScheduledHours.contains(hour)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isHourActive) CockpitPrimaryContainer else CockpitSurfaceContainerHigh)
                                        .clickable {
                                            val updated = if (isHourActive) {
                                                config.customScheduledHours - hour
                                            } else {
                                                config.customScheduledHours + hour
                                            }
                                            viewModel.setCustomScheduledHours(updated)
                                        }
                                        .padding(horizontal = 8.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = String.format("%02d:00", hour),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isHourActive) CockpitOnPrimaryContainer else CockpitOnSurface,
                                        fontWeight = if (isHourActive) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // =========================================================================
        // SECCIÓN 3: FADE Y DUCKING DE AUDIO
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
