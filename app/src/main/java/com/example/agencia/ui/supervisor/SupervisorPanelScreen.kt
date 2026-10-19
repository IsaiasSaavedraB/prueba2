package com.example.agencia.ui.supervisor

import android.widget.Toast
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.agencia.model.relations.OportunidadConDetalles
import com.example.agencia.ui.theme.StatusConfirmada
import com.example.agencia.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupervisorPanelScreen(
    onVerOportunidadDetalle: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SupervisorPanelViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.mensajeExito) {
        uiState.mensajeExito?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.limpiarMensajeExito()
        }
    }

    if (uiState.cargando) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (!uiState.esSupervisor) {
        Box(modifier = modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Acceso Restringido", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onErrorContainer)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Esta sección está reservada exclusivamente para el perfil Supervisor Comercial.", color = MaterialTheme.colorScheme.onErrorContainer)
                }
            }
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.SupervisorAccount, contentDescription = null, modifier = Modifier.size(36.dp), tint = MaterialTheme.colorScheme.onTertiaryContainer)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Panel de Control de Supervisor", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onTertiaryContainer)
                        Text("Gestión global y asignación de ejecutivos comerciales", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onTertiaryContainer)
                    }
                }
            }
        }

        // Section 1: Executive Workload Balance
        item {
            Text("Carga de Trabajo por Ejecutivo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(uiState.ejecutivosResumen) { resumen ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(resumen.ejecutivo.nombreCompleto, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text("${resumen.cantidadOportunidades} Oportunidades Asignadas", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                        }
                    }

                    Surface(color = StatusConfirmada.copy(alpha = 0.15f), shape = MaterialTheme.shapes.small) {
                        Text(
                            text = DateUtils.formatearMonto(resumen.montoTotalPipeline),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = StatusConfirmada
                        )
                    }
                }
            }
        }

        // Section 2: Opportunities & Reassignment List
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Gestión y Reasignación de Oportunidades", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(uiState.todasOportunidades, key = { it.oportunidad.id }) { item ->
            OportunidadSupervisorRow(
                item = item,
                onReasignar = { viewModel.abrirDialogoReasignar(item) },
                onVerDetalle = { onVerOportunidadDetalle(item.oportunidad.id) }
            )
        }
    }

    // Reassignment Dialog
    uiState.oportunidadParaReasignar?.let { target ->
        AlertDialog(
            onDismissRequest = { viewModel.cerrarDialogoReasignar() },
            title = { Text("Asignar Responsable Comercial", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Oportunidad: ${target.oportunidad.titulo}", fontWeight = FontWeight.SemiBold)
                    Text("Empresa: ${target.organizacion.nombre}")

                    HorizontalDivider()

                    var expandedEj by remember { mutableStateOf(false) }
                    val ejSel = uiState.ejecutivosDisponibles.find { it.id == uiState.ejecutivoSeleccionadoId }

                    ExposedDropdownMenuBox(
                        expanded = expandedEj,
                        onExpandedChange = { expandedEj = !expandedEj }
                    ) {
                        OutlinedTextField(
                            value = ejSel?.nombreCompleto ?: "Sin Asignar",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Seleccionar Ejecutivo Responsable") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEj) },
                            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedEj,
                            onDismissRequest = { expandedEj = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Sin Asignar") },
                                onClick = {
                                    viewModel.seleccionarEjecutivoParaReasignar(null)
                                    expandedEj = false
                                }
                            )
                            uiState.ejecutivosDisponibles.forEach { ej ->
                                DropdownMenuItem(
                                    text = { Text(ej.nombreCompleto) },
                                    onClick = {
                                        viewModel.seleccionarEjecutivoParaReasignar(ej.id)
                                        expandedEj = false
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { viewModel.reasignarOportunidad() }) {
                    Text("Confirmar Asignación")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.cerrarDialogoReasignar() }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun OportunidadSupervisorRow(
    item: OportunidadConDetalles,
    onReasignar: () -> Unit,
    onVerDetalle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.oportunidad.titulo, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Business, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.outline)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(item.organizacion.nombre, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AttachMoney, contentDescription = null, modifier = Modifier.size(14.dp), tint = StatusConfirmada)
                    Text(DateUtils.formatearMonto(item.oportunidad.montoEstimado), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Asignado a: ${item.ejecutivo?.nombreCompleto ?: "SIN ASIGNAR"}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = if (item.ejecutivo == null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onReasignar) {
                        Icon(Icons.Default.AssignmentInd, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reasignar")
                    }
                }
            }
        }
    }
}