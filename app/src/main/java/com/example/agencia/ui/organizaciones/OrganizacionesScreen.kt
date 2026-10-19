package com.example.agencia.ui.organizaciones

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Work
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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.agencia.model.Organizacion
import com.example.agencia.model.relations.ContactoConOrganizacion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizacionesScreen(
    modifier: Modifier = Modifier,
    viewModel: OrganizacionesViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(uiState.mensajeExito) {
        uiState.mensajeExito?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.limpiarMensajeExito()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            PrimaryTabRow(selectedTabIndex = selectedTabIndex) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = { Text("Organizaciones (${uiState.organizaciones.size})") }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text("Contactos (${uiState.contactosConOrg.size})") }
                )
            }

            if (uiState.cargando) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                if (selectedTabIndex == 0) {
                    // Organizations List
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.organizaciones, key = { it.id }) { org ->
                            val contactosDeOrg = uiState.contactosConOrg.filter { it.contacto.organizacionId == org.id }
                            OrganizacionCard(
                                organizacion = org,
                                contactosCount = contactosDeOrg.size,
                                onAgregarContacto = { viewModel.abrirDialogoContacto(org.id) }
                            )
                        }
                    }
                } else {
                    // Contacts List
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.contactosConOrg, key = { it.contacto.id }) { item ->
                            ContactoCard(item = item)
                        }
                    }
                }
            }
        }

        // FAB to create new Org or Contact
        FloatingActionButton(
            onClick = {
                if (selectedTabIndex == 0) viewModel.abrirDialogoOrg()
                else viewModel.abrirDialogoContacto()
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.Add, contentDescription = "Agregar")
        }
    }

    // Dialog 1: Nueva Organización
    if (uiState.mostrarDialogoOrg) {
        AlertDialog(
            onDismissRequest = { viewModel.cerrarDialogoOrg() },
            title = { Text("Nueva Organización", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.nombreOrg,
                        onValueChange = { viewModel.cambiarNombreOrg(it) },
                        label = { Text("Nombre de Empresa *") },
                        leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                        isError = uiState.errorNombreOrg,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = uiState.rutOrg,
                        onValueChange = { viewModel.cambiarRutOrg(it) },
                        label = { Text("RUT Corporativo *") },
                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                        isError = uiState.errorRutOrg,
                        supportingText = {
                            if (uiState.errorRutOrg && uiState.mensajeErrorRut != null) {
                                Text(uiState.mensajeErrorRut!!, color = MaterialTheme.colorScheme.error)
                            } else {
                                Text("Ej: 76.123.456-7")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = uiState.industriaOrg,
                        onValueChange = { viewModel.cambiarIndustriaOrg(it) },
                        label = { Text("Industria / Rubro") },
                        leadingIcon = { Icon(Icons.Default.Work, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = uiState.direccionOrg,
                        onValueChange = { viewModel.cambiarDireccionOrg(it) },
                        label = { Text("Dirección") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = uiState.telefonoOrg,
                        onValueChange = { viewModel.cambiarTelefonoOrg(it) },
                        label = { Text("Teléfono de Contacto *") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        isError = uiState.errorTelefonoOrg,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = { viewModel.guardarOrganizacion() }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.cerrarDialogoOrg() }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Dialog 2: Nuevo Contacto
    if (uiState.mostrarDialogoContacto) {
        AlertDialog(
            onDismissRequest = { viewModel.cerrarDialogoContacto() },
            title = { Text("Nuevo Contacto Corporativo", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    var expandedOrg by remember { mutableStateOf(false) }
                    val orgSeleccionada = uiState.organizaciones.find { it.id == uiState.orgIdSeleccionada }

                    ExposedDropdownMenuBox(
                        expanded = expandedOrg,
                        onExpandedChange = { expandedOrg = !expandedOrg }
                    ) {
                        OutlinedTextField(
                            value = orgSeleccionada?.nombre ?: "Seleccionar Organización",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Organización *") },
                            leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedOrg) },
                            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedOrg,
                            onDismissRequest = { expandedOrg = false }
                        ) {
                            uiState.organizaciones.forEach { org ->
                                DropdownMenuItem(
                                    text = { Text(org.nombre) },
                                    onClick = {
                                        viewModel.cambiarOrgContacto(org.id)
                                        expandedOrg = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = uiState.nombreContacto,
                        onValueChange = { viewModel.cambiarNombreContacto(it) },
                        label = { Text("Nombre Completo *") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        isError = uiState.errorNombreContacto,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = uiState.cargoContacto,
                        onValueChange = { viewModel.cambiarCargoContacto(it) },
                        label = { Text("Cargo / Puesto") },
                        leadingIcon = { Icon(Icons.Default.Work, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = uiState.correoContacto,
                        onValueChange = { viewModel.cambiarCorreoContacto(it) },
                        label = { Text("Correo Electrónico *") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        isError = uiState.errorCorreoContacto,
                        supportingText = {
                            if (uiState.errorCorreoContacto && uiState.mensajeErrorCorreoContacto != null) {
                                Text(uiState.mensajeErrorCorreoContacto!!, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = uiState.telefonoContacto,
                        onValueChange = { viewModel.cambiarTelefonoContacto(it) },
                        label = { Text("Teléfono *") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        isError = uiState.errorTelefonoContacto,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = { viewModel.guardarContacto() }) {
                    Text("Guardar Contacto")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.cerrarDialogoContacto() }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun OrganizacionCard(
    organizacion: Organizacion,
    contactosCount: Int,
    onAgregarContacto: () -> Unit
) {
    var expandido by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = organizacion.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "RUT: ${organizacion.rut} • ${organizacion.industria}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                IconButton(onClick = { expandido = !expandido }) {
                    Icon(
                        imageVector = if (expandido) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Detalles"
                    )
                }
            }

            AnimatedVisibility(visible = expandido) {
                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Dirección: ${organizacion.direccion}", style = MaterialTheme.typography.bodySmall)
                    Text("Teléfono: ${organizacion.telefono}", style = MaterialTheme.typography.bodySmall)
                    Text("Contactos Registrados: $contactosCount", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = onAgregarContacto,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Añadir Contacto a esta empresa")
                    }
                }
            }
        }
    }
}

@Composable
fun ContactoCard(item: ContactoConOrganizacion) {
    val contacto = item.contacto
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contacto.nombre,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${contacto.cargo} en ${item.organizacion.nombre}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = "${contacto.correo} • ${contacto.telefono}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}