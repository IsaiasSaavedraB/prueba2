package com.example.agencia.ui.seguimientos

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.agencia.model.EstadosOportunidad
import com.example.agencia.model.TiposGestion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeguimientoScreen(
    oportunidadId: Int,
    onGuardadoExitoso: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SeguimientoViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(oportunidadId) {
        viewModel.cargarOportunidad(oportunidadId)
    }

    LaunchedEffect(uiState.guardadoExitoso) {
        if (uiState.guardadoExitoso) {
            Toast.makeText(context, "Gestión de seguimiento registrada con éxito", Toast.LENGTH_SHORT).show()
            onGuardadoExitoso()
        }
    }

    if (uiState.cargando && uiState.oportunidadDetalle == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val item = uiState.oportunidadDetalle ?: return
    val op = item.oportunidad

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Registrar Gestión de Seguimiento",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        // Opportunity Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = op.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Business,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${item.organizacion.nombre} • Estado: ${op.estado}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        // Form Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Detalle de la Gestión", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                // Management Type Dropdown
                var expandedTipo by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedTipo,
                    onExpandedChange = { expandedTipo = !expandedTipo }
                ) {
                    OutlinedTextField(
                        value = uiState.tipoGestion,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo de Gestión / Canal *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTipo) },
                        modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedTipo,
                        onDismissRequest = { expandedTipo = false }
                    ) {
                        TiposGestion.TODOS.forEach { tipo ->
                            DropdownMenuItem(
                                text = { Text(tipo) },
                                onClick = {
                                    viewModel.cambiarTipoGestion(tipo)
                                    expandedTipo = false
                                }
                            )
                        }
                    }
                }

                // Description
                OutlinedTextField(
                    value = uiState.descripcion,
                    onValueChange = { viewModel.cambiarDescripcion(it) },
                    label = { Text("Descripción / Minuta de la Gestión *") },
                    leadingIcon = { Icon(Icons.AutoMirrored.Filled.Comment, contentDescription = null) },
                    isError = uiState.errorDescripcion,
                    supportingText = {
                        if (uiState.errorDescripcion && uiState.mensajeErrorDescripcion != null) {
                            Text(uiState.mensajeErrorDescripcion!!, color = MaterialTheme.colorScheme.error)
                        } else {
                            Text("Ingrese los acuerdos, observaciones o próx. pasos")
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    maxLines = 4
                )

                HorizontalDivider()

                // Option to update status
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = uiState.actualizarEstado,
                        onCheckedChange = { viewModel.cambiarActualizarEstado(it) }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Actualizar estado de la oportunidad simultáneamente",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                AnimatedVisibility(visible = uiState.actualizarEstado) {
                    var expandedNuevoEstado by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedNuevoEstado,
                        onExpandedChange = { expandedNuevoEstado = !expandedNuevoEstado }
                    ) {
                        OutlinedTextField(
                            value = uiState.nuevoEstado,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Nuevo Estado Comercial") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedNuevoEstado) },
                            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedNuevoEstado,
                            onDismissRequest = { expandedNuevoEstado = false }
                        ) {
                            EstadosOportunidad.TODOS.forEach { est ->
                                DropdownMenuItem(
                                    text = { Text(est) },
                                    onClick = {
                                        viewModel.cambiarNuevoEstado(est)
                                        expandedNuevoEstado = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = { viewModel.registrarSeguimiento() },
            enabled = !uiState.cargando,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Registrar Gestión")
        }

        // Previous Timeline History
        if (uiState.historialSeguimientos.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.History, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Historial de Gestiones Previas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }

                    uiState.historialSeguimientos.forEach { seg ->
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(seg.tipoGestion, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                Text(seg.fechaHora, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                                Text(seg.descripcion, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}