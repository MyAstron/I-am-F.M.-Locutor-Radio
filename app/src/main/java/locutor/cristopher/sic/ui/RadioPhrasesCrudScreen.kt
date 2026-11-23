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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import locutor.cristopher.sic.data.phrases.RadioPhrase
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

@Composable
fun RadioPhrasesCrudScreen(
    viewModel: RadioViewModel,
    modifier: Modifier = Modifier
) {
    val phrases by viewModel.phrases.collectAsState()
    val isWeatherEnabled by viewModel.isWeatherEnabled.collectAsState()

    var showEditDialog by remember { mutableStateOf(false) }
    var editingPhrase by remember { mutableStateOf<RadioPhrase?>(null) }
    var dialogText by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CockpitSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header: Anti-repetición y Telemetría
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
                            .background(CockpitPrimaryContainer.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatQuote,
                            contentDescription = null,
                            tint = CockpitPrimaryContainer,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "CATÁLOGO DE FRASES (CRUD)",
                            style = MaterialTheme.typography.labelSmall,
                            color = CockpitPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Rotación anti-repetición activa",
                            style = MaterialTheme.typography.bodySmall,
                            color = CockpitOnSurfaceVariant
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(CockpitSurfaceContainerHigh)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Repeat,
                            contentDescription = null,
                            tint = CockpitPrimaryContainer,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${phrases.count { it.isActive }}/${phrases.size} ACTIVAS",
                            style = MaterialTheme.typography.labelSmall,
                            color = CockpitOnSurface,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            // Clima Local Opcional Toggle Bar
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CockpitSurfaceContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(CockpitSecondary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cloud,
                                contentDescription = null,
                                tint = CockpitSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Mención de Clima Local",
                                style = MaterialTheme.typography.bodyMedium,
                                color = CockpitOnSurface,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isWeatherEnabled) "Inyecta {clima} en el boletín horario" else "Desactivado (sin acceso a ubicación)",
                                style = MaterialTheme.typography.bodySmall,
                                color = CockpitOnSurfaceVariant
                            )
                        }
                    }

                    Switch(
                        checked = isWeatherEnabled,
                        onCheckedChange = { checked ->
                            viewModel.setWeatherEnabled(checked)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CockpitPrimaryContainer,
                            checkedTrackColor = CockpitPrimaryContainer.copy(alpha = 0.4f),
                            uncheckedThumbColor = CockpitOnSurfaceVariant,
                            uncheckedTrackColor = CockpitSurfaceContainerLowest
                        )
                    )
                }
            }

            // Lista de Frases CRUD
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(phrases, key = { it.id }) { phrase ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (phrase.isActive) CockpitSurfaceContainer else CockpitSurfaceContainerLow
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
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
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(if (phrase.isActive) CockpitPrimaryContainer else CockpitOnSurfaceVariant)
                                    )
                                    Text(
                                        text = if (phrase.isActive) "ACTIVA" else "EN PAUSA",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (phrase.isActive) CockpitPrimaryContainer else CockpitOnSurfaceVariant,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    // Escuchar muestra de esta frase
                                    IconButton(
                                        onClick = {
                                            viewModel.testVoiceSpeech(
                                                phrase.template
                                                    .replace("{cancion}", "Blinding Lights")
                                                    .replace("{artista}", "The Weeknd")
                                                    .replace("{hora}", "las 2 de la tarde en punto")
                                                    .replace("{clima}", "Tenemos 22 grados con cielo despejado. ")
                                            )
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = "Escuchar",
                                            tint = CockpitPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    // Editar
                                    IconButton(
                                        onClick = {
                                            editingPhrase = phrase
                                            dialogText = phrase.template
                                            showEditDialog = true
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Editar",
                                            tint = CockpitOnSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    // Eliminar
                                    IconButton(
                                        onClick = { viewModel.deletePhrase(phrase.id) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Eliminar",
                                            tint = CockpitTertiary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    // Switch Activa
                                    Switch(
                                        checked = phrase.isActive,
                                        onCheckedChange = { viewModel.togglePhraseActive(phrase.id) },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = CockpitPrimaryContainer,
                                            checkedTrackColor = CockpitPrimaryContainer.copy(alpha = 0.4f),
                                            uncheckedThumbColor = CockpitOnSurfaceVariant,
                                            uncheckedTrackColor = CockpitSurfaceContainerLowest
                                        )
                                    )
                                }
                            }

                            // Frase texto
                            Text(
                                text = phrase.template,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (phrase.isActive) CockpitOnSurface else CockpitOnSurfaceVariant
                            )
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(64.dp))
                }
            }
        }

        // Floating Action Button (+) para agregar nueva frase
        FloatingActionButton(
            onClick = {
                editingPhrase = null
                dialogText = ""
                showEditDialog = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
            containerColor = CockpitPrimaryContainer,
            contentColor = CockpitOnPrimaryContainer
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Nueva Frase",
                modifier = Modifier.size(24.dp)
            )
        }

        // Diálogo Crear / Editar Frase
        if (showEditDialog) {
            AlertDialog(
                onDismissRequest = { showEditDialog = false },
                containerColor = CockpitSurfaceContainer,
                title = {
                    Text(
                        text = if (editingPhrase != null) "Editar Frase" else "Nueva Frase de Locución",
                        style = MaterialTheme.typography.headlineSmall,
                        color = CockpitOnSurface,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Escribe la plantilla utilizando las etiquetas dinámicas:",
                            style = MaterialTheme.typography.bodySmall,
                            color = CockpitOnSurfaceVariant
                        )

                        OutlinedTextField(
                            value = dialogText,
                            onValueChange = { dialogText = it },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 5,
                            placeholder = {
                                Text(
                                    text = "Ej: Sonando {cancion} de {artista}. Son {hora}. {clima}¡Seguimos en cabina!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = CockpitOnSurfaceVariant.copy(alpha = 0.5f)
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CockpitPrimaryContainer,
                                unfocusedBorderColor = CockpitSurfaceContainerHigh,
                                focusedTextColor = CockpitOnSurface,
                                unfocusedTextColor = CockpitOnSurface
                            )
                        )

                        // Chips rápidos para insertar etiquetas
                        Text(
                            text = "INSERTAR VARIABLES",
                            style = MaterialTheme.typography.labelSmall,
                            color = CockpitPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("{cancion}", "{artista}", "{hora}", "{clima}").forEach { tag ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(999.dp))
                                        .background(CockpitSurfaceContainerHigh)
                                        .clickable { dialogText += " $tag" }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "+ $tag",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = CockpitSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (dialogText.isNotBlank()) {
                                if (editingPhrase != null) {
                                    viewModel.updatePhrase(
                                        id = editingPhrase!!.id,
                                        template = dialogText,
                                        isActive = editingPhrase!!.isActive
                                    )
                                } else {
                                    viewModel.addPhrase(dialogText)
                                }
                                showEditDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CockpitPrimaryContainer)
                    ) {
                        Text(
                            text = "Guardar",
                            style = MaterialTheme.typography.labelSmall,
                            color = CockpitOnPrimaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditDialog = false }) {
                        Text(
                            text = "Cancelar",
                            style = MaterialTheme.typography.labelSmall,
                            color = CockpitOnSurfaceVariant
                        )
                    }
                }
            )
        }
    }
}
