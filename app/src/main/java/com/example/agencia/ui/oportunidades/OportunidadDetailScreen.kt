package com.example.agencia.ui.oportunidades

import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.agencia.model.EstadosOportunidad
import com.example.agencia.model.Prioridades
import com.example.agencia.ui.theme.PriorityAlta
import com.example.agencia.ui.theme.PriorityBaja
import com.example.agencia.ui.theme.PriorityMedia
import com.example.agencia.ui.theme.StatusConfirmada
import com.example.agencia.ui.theme.StatusDescartada
import com.example.agencia.ui.theme.StatusEnGestion
import com.example.agencia.ui.theme.StatusNegociacion
import com.example.agencia.ui.theme.StatusNueva
import com.example.agencia.ui.theme.StatusPropuesta
import com.example.agencia.util.CalendarUtils
import com.example.agencia.util.DateUtils
import com.example.agencia.util.FileUtils
import java.io.File

@Composable
fun OportunidadDetailScreen(
    oportunidadId: Int,
    onEditar: (Int) -> Unit,
    onRegistrarSeguimiento: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OportunidadDetailViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.estadoUi.collectAsStateWithLifecycle()

    var photoFile by remember { mutableStateOf<File?>(null) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    // Requirement 5: Camera Activity Result Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && photoFile != null) {
            viewModel.guardarFotoPath(photoFile!!.absolutePath)
        }
    }

    LaunchedEffect(oportunidadId) {
        viewModel.cargarOportunidad(oportunidadId)
    }

    LaunchedEffect(uiState.mensajeExito) {
        uiState.mensajeExito?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.limpiarMensajes()
        }
    }

    if (uiState.cargando || uiState.oportunidadDetalle == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val item = uiState.oportunidadDetalle!!
    val op = item.oportunidad
    val esSupervisor = uiState.esSupervisor

    val estadoColor = when (op.estado) {
        EstadosOportunidad.NUEVA -> StatusNueva
        EstadosOportunidad.EN_GESTION -> StatusEnGestion
        EstadosOportunidad.PROPUESTA -> StatusPropuesta
        EstadosOportunidad.EN_NEGOCIACION -> StatusNegociacion
        EstadosOportunidad.CONFIRMADA -> StatusConfirmada
        EstadosOportunidad.DESCARTADA -> StatusDescartada
        else -> MaterialTheme.colorScheme.primary
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title & Header Status
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = op.titulo,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Creado el ${op.fechaCreacion}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    Surface(
                        color = estadoColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = op.estado,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = estadoColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Monto Estimado", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                        Text(DateUtils.formatearMonto(op.montoEstimado), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = StatusConfirmada)
                    }

                    Column {
                        Text("Fecha Evento", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                        Text(op.fechaEvento, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    }

                    Column {
                        Text("Prioridad", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                        Text(op.prioridad, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = if (op.prioridad == Prioridades.ALTA) PriorityAlta else PriorityMedia)
                    }
                }
            }
        }

        // Native Features Action Toolbar (Camera & Calendar)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Camera Integration
            Button(
                onClick = {
                    try {
                        val file = FileUtils.crearArchivoFoto(context)
                        photoFile = file
                        val uri = FileUtils.obtenerUriParaArchivo(context, file)
                        photoUri = uri
                        cameraLauncher.launch(uri)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error abriendo la cámara: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Tomar Foto")
            }

            // Calendar Integration
            OutlinedButton(
                onClick = {
                    CalendarUtils.agregarEventoCalendario(
                        context = context,
                        titulo = op.titulo,
                        descripcion = "Oportunidad CRM - ${item.organizacion.nombre}. Contacto: ${item.contacto.nombre}",
                        ubicacion = op.ubicacion,
                        fechaISO = op.fechaEvento
                    )
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Calendario")
            }
        }

        // Requirement 5: Photo Evidence Preview Card
        if (!op.fotoPath.isNullOrBlank()) {
            val file = File(op.fotoPath)
            if (file.exists()) {
                val bitmap = remember(op.fotoPath) { BitmapFactory.decodeFile(file.absolutePath) }
                if (bitmap != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Evidencia Fotográfica Capturada",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Evidencia de oportunidad",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
        }

        // Details Breakdown Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Información de Evento", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                DetailRow(icon = Icons.Default.Event, label = "Tipo de Evento", value = op.tipoEvento)
                DetailRow(icon = Icons.Default.LocationOn, label = "Ubicación", value = op.ubicacion)
                DetailRow(icon = Icons.Default.Groups, label = "Público Esperado", value = "${op.publicoEsperado} asistentes")

                HorizontalDivider()

                Text("Organización y Contacto", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                DetailRow(icon = Icons.Default.Business, label = "Empresa", value = "${item.organizacion.nombre} (${item.organizacion.rut})")
                DetailRow(icon = Icons.Default.Person, label = "Contacto", value = "${item.contacto.nombre} - ${item.contacto.cargo}")
                DetailRow(icon = Icons.Default.Email, label = "Correo Contacto", value = item.contacto.correo)
                DetailRow(icon = Icons.Default.Phone, label = "Teléfono Contacto", value = item.contacto.telefono)

                HorizontalDivider()

                Text("Responsable Asignado", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                DetailRow(
                    icon = Icons.Default.Person,
                    label = "Ejecutivo Comercial",
                    value = item.ejecutivo?.nombreCompleto ?: "Sin Asignar (Requiere Supervisor)"
                )
            }
        }

        // Requirement 1: State Transition Control Panel & Error Feedback
        Card(
            modifier = Modifier.fillMaxWidth().animateContentSize(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Gestión de Estados (Permisos por Rol)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (esSupervisor) "Usted es Supervisor Comercial: puede avanzar, confirmar o descartar definitivamente." else "Usted es Ejecutivo Comercial: puede avanzar el flujo, pero NO confirmar o descartar.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                AnimatedVisibility(visible = uiState.mensajeErrorTransicion != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = uiState.mensajeErrorTransicion ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (op.estado == EstadosOportunidad.NUEVA) {
                        Button(onClick = { viewModel.cambiarEstado(EstadosOportunidad.EN_GESTION) }, modifier = Modifier.weight(1f)) {
                            Text("Iniciar Gestión")
                        }
                    }
                    if (op.estado == EstadosOportunidad.EN_GESTION) {
                        Button(onClick = { viewModel.cambiarEstado(EstadosOportunidad.PROPUESTA) }, modifier = Modifier.weight(1f)) {
                            Text("Enviar Propuesta")
                        }
                    }
                    if (op.estado == EstadosOportunidad.PROPUESTA) {
                        Button(onClick = { viewModel.cambiarEstado(EstadosOportunidad.EN_NEGOCIACION) }, modifier = Modifier.weight(1f)) {
                            Text("En Negociación")
                        }
                    }
                }

                // Confirm and Discard buttons (Restricted logic)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.cambiarEstado(EstadosOportunidad.CONFIRMADA) },
                        colors = ButtonDefaults.buttonColors(containerColor = StatusConfirmada),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Confirmar")
                    }

                    OutlinedButton(
                        onClick = { viewModel.cambiarEstado(EstadosOportunidad.DESCARTADA) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Descartar", color = StatusDescartada)
                    }
                }
            }
        }

        // Requirement 2: Follow-up Timeline History (Seguimientos)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.History, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Historial de Gestiones (${uiState.seguimientos.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(onClick = { onRegistrarSeguimiento(op.id) }) {
                        Icon(Icons.Default.AddComment, contentDescription = "Añadir Seguimiento", tint = MaterialTheme.colorScheme.primary)
                    }
                }

                if (uiState.seguimientos.isEmpty()) {
                    Text(
                        text = "Aún no hay gestiones de seguimiento registradas.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                } else {
                    uiState.seguimientos.forEach { seg ->
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = seg.tipoGestion,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = seg.fechaHora,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = seg.descripcion,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                Button(
                    onClick = { onRegistrarSeguimiento(op.id) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.AddComment, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Registrar Nueva Gestión")
                }
            }
        }

        // Edit Opportunity Button
        OutlinedButton(
            onClick = { onEditar(op.id) },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Icon(Icons.Default.Edit, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Editar Datos de la Oportunidad")
        }
    }
}

@Composable
fun DetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        }
    }
}