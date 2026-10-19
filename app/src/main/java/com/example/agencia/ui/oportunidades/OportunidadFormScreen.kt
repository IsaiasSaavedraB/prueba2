package com.example.agencia.ui.oportunidades

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.agencia.model.EstadosOportunidad
import com.example.agencia.model.Prioridades
import com.example.agencia.model.TiposEvento

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OportunidadFormScreen(
    oportunidadId: Int = 0,
    onGuardadoExitoso: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OportunidadFormViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(oportunidadId) {
        if (oportunidadId > 0) {
            viewModel.cargarParaEditar(oportunidadId)
        }
    }

    LaunchedEffect(uiState.guardadoExitoso) {
        if (uiState.guardadoExitoso) {
            Toast.makeText(context, "Oportunidad guardada correctamente", Toast.LENGTH_SHORT).show()
            onGuardadoExitoso()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (uiState.esEdicion) "Editar Oportunidad Comercial" else "Crear Nueva Oportunidad",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Title
                OutlinedTextField(
                    value = uiState.titulo,
                    onValueChange = { viewModel.cambiarTitulo(it) },
                    label = { Text("Título del Evento / Oportunidad *") },
                    leadingIcon = { Icon(Icons.Default.Title, contentDescription = null) },
                    isError = uiState.errorTitulo,
                    supportingText = {
                        if (uiState.errorTitulo && uiState.mensajeErrorTitulo != null) {
                            Text(uiState.mensajeErrorTitulo!!, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Organization Dropdown
                var expandedOrg by remember { mutableStateOf(false) }
                val orgSeleccionada = uiState.organizacionesDisponibles.find { it.id == uiState.organizacionId }

                ExposedDropdownMenuBox(
                    expanded = expandedOrg,
                    onExpandedChange = { expandedOrg = !expandedOrg }
                ) {
                    OutlinedTextField(
                        value = orgSeleccionada?.nombre ?: "Seleccionar Organización *",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Organización / Empresa *") },
                        leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedOrg) },
                        isError = uiState.errorOrganizacion,
                        supportingText = {
                            if (uiState.errorOrganizacion) Text("Debe seleccionar una organización", color = MaterialTheme.colorScheme.error)
                        },
                        modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedOrg,
                        onDismissRequest = { expandedOrg = false }
                    ) {
                        uiState.organizacionesDisponibles.forEach { org ->
                            DropdownMenuItem(
                                text = { Text("${org.nombre} (${org.rut})") },
                                onClick = {
                                    viewModel.cambiarOrganizacion(org.id)
                                    expandedOrg = false
                                }
                            )
                        }
                    }
                }

                // Contact Dropdown (filtered by selected organization)
                var expandedContacto by remember { mutableStateOf(false) }
                val contactoSeleccionado = uiState.contactosDisponibles.find { it.id == uiState.contactoId }

                ExposedDropdownMenuBox(
                    expanded = expandedContacto,
                    onExpandedChange = { expandedContacto = !expandedContacto }
                ) {
                    OutlinedTextField(
                        value = contactoSeleccionado?.nombre ?: if (uiState.organizacionId == null) "Primero seleccione empresa" else "Seleccionar Contacto *",
                        onValueChange = {},
                        readOnly = true,
                        enabled = uiState.organizacionId != null,
                        label = { Text("Contacto Corporativo *") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedContacto) },
                        isError = uiState.errorContacto,
                        supportingText = {
                            if (uiState.errorContacto) Text("Debe seleccionar un contacto", color = MaterialTheme.colorScheme.error)
                        },
                        modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedContacto,
                        onDismissRequest = { expandedContacto = false }
                    ) {
                        uiState.contactosDisponibles.forEach { contacto ->
                            DropdownMenuItem(
                                text = { Text("${contacto.nombre} - ${contacto.cargo}") },
                                onClick = {
                                    viewModel.cambiarContacto(contacto.id)
                                    expandedContacto = false
                                }
                            )
                        }
                    }
                }

                // Executive Dropdown (Supervisor only or displayed)
                if (uiState.esSupervisor) {
                    var expandedEjecutivo by remember { mutableStateOf(false) }
                    val ejecutivoSeleccionado = uiState.ejecutivosDisponibles.find { it.id == uiState.ejecutivoId }

                    ExposedDropdownMenuBox(
                        expanded = expandedEjecutivo,
                        onExpandedChange = { expandedEjecutivo = !expandedEjecutivo }
                    ) {
                        OutlinedTextField(
                            value = ejecutivoSeleccionado?.nombreCompleto ?: "Sin Asignar",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Ejecutivo Responsable (Supervisor)") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEjecutivo) },
                            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedEjecutivo,
                            onDismissRequest = { expandedEjecutivo = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Sin Asignar") },
                                onClick = {
                                    viewModel.cambiarEjecutivo(null)
                                    expandedEjecutivo = false
                                }
                            )
                            uiState.ejecutivosDisponibles.forEach { ej ->
                                DropdownMenuItem(
                                    text = { Text(ej.nombreCompleto) },
                                    onClick = {
                                        viewModel.cambiarEjecutivo(ej.id)
                                        expandedEjecutivo = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Amount ($)
                OutlinedTextField(
                    value = uiState.montoStr,
                    onValueChange = { viewModel.cambiarMonto(it) },
                    label = { Text("Monto Estimado ($) *") },
                    leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = uiState.errorMonto,
                    supportingText = {
                        if (uiState.errorMonto && uiState.mensajeErrorMonto != null) {
                            Text(uiState.mensajeErrorMonto!!, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Date YYYY-MM-DD
                OutlinedTextField(
                    value = uiState.fechaEvento,
                    onValueChange = { viewModel.cambiarFecha(it) },
                    label = { Text("Fecha de Evento (YYYY-MM-DD) *") },
                    leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                    isError = uiState.errorFecha,
                    supportingText = {
                        if (uiState.errorFecha && uiState.mensajeErrorFecha != null) {
                            Text(uiState.mensajeErrorFecha!!, color = MaterialTheme.colorScheme.error)
                        } else {
                            Text("Ejemplo: 2026-05-15")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Location
                OutlinedTextField(
                    value = uiState.ubicacion,
                    onValueChange = { viewModel.cambiarUbicacion(it) },
                    label = { Text("Ubicación del Evento *") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    isError = uiState.errorUbicacion,
                    supportingText = {
                        if (uiState.errorUbicacion) Text("La ubicación es obligatoria", color = MaterialTheme.colorScheme.error)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Event Type Dropdown
                var expandedTipo by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedTipo,
                    onExpandedChange = { expandedTipo = !expandedTipo }
                ) {
                    OutlinedTextField(
                        value = uiState.tipoEvento,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo de Evento *") },
                        leadingIcon = { Icon(Icons.Default.Event, contentDescription = null) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTipo) },
                        modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedTipo,
                        onDismissRequest = { expandedTipo = false }
                    ) {
                        TiposEvento.TODOS.forEach { tipo ->
                            DropdownMenuItem(
                                text = { Text(tipo) },
                                onClick = {
                                    viewModel.cambiarTipoEvento(tipo)
                                    expandedTipo = false
                                }
                            )
                        }
                    }
                }

                // Public Expected
                OutlinedTextField(
                    value = uiState.publicoStr,
                    onValueChange = { viewModel.cambiarPublico(it) },
                    label = { Text("Público Esperado (Asistentes) *") },
                    leadingIcon = { Icon(Icons.Default.Groups, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = uiState.errorPublico,
                    supportingText = {
                        if (uiState.errorPublico && uiState.mensajeErrorPublico != null) {
                            Text(uiState.mensajeErrorPublico!!, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Priority Dropdown
                var expandedPrioridad by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedPrioridad,
                    onExpandedChange = { expandedPrioridad = !expandedPrioridad }
                ) {
                    OutlinedTextField(
                        value = uiState.prioridad,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Prioridad Comercial *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPrioridad) },
                        modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedPrioridad,
                        onDismissRequest = { expandedPrioridad = false }
                    ) {
                        Prioridades.TODAS.forEach { pri ->
                            DropdownMenuItem(
                                text = { Text(pri) },
                                onClick = {
                                    viewModel.cambiarPrioridad(pri)
                                    expandedPrioridad = false
                                }
                            )
                        }
                    }
                }

                // Initial State Dropdown
                var expandedEstado by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedEstado,
                    onExpandedChange = { expandedEstado = !expandedEstado }
                ) {
                    OutlinedTextField(
                        value = uiState.estado,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Estado Comercial *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEstado) },
                        modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedEstado,
                        onDismissRequest = { expandedEstado = false }
                    ) {
                        EstadosOportunidad.TODOS.forEach { est ->
                            DropdownMenuItem(
                                text = { Text(est) },
                                onClick = {
                                    viewModel.cambiarEstado(est)
                                    expandedEstado = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick = { viewModel.guardar() },
            enabled = !uiState.cargando,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (uiState.cargando) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text(if (uiState.esEdicion) "Guardar Cambios" else "Crear Oportunidad")
            }
        }
    }
}