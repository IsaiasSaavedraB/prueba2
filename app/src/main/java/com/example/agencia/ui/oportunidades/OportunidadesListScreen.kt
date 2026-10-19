package com.example.agencia.ui.oportunidades

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.agencia.model.EstadosOportunidad
import com.example.agencia.model.Prioridades
import com.example.agencia.model.relations.OportunidadConDetalles
import com.example.agencia.ui.theme.PriorityAlta
import com.example.agencia.ui.theme.PriorityBaja
import com.example.agencia.ui.theme.PriorityMedia
import com.example.agencia.ui.theme.StatusConfirmada
import com.example.agencia.ui.theme.StatusDescartada
import com.example.agencia.ui.theme.StatusEnGestion
import com.example.agencia.ui.theme.StatusNegociacion
import com.example.agencia.ui.theme.StatusNueva
import com.example.agencia.ui.theme.StatusPropuesta
import com.example.agencia.util.DateUtils

@Composable
fun OportunidadesListScreen(
    onVerDetalle: (Int) -> Unit,
    onCrearNueva: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OportunidadesListViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = uiState.busqueda,
                onValueChange = { viewModel.cambiarBusqueda(it) },
                placeholder = { Text("Buscar por título, empresa, ubicación...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (uiState.busqueda.isNotEmpty()) {
                        IconButton(onClick = { viewModel.cambiarBusqueda("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // State Filter Chips
            Text(
                text = "Estado:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            val estadosFiltro = listOf("TODAS") + EstadosOportunidad.TODOS

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(estadosFiltro) { estado ->
                    FilterChip(
                        selected = uiState.estadoFiltro == estado,
                        onClick = { viewModel.cambiarEstadoFiltro(estado) },
                        label = { Text(if (estado == "TODAS") "Todas" else estado) }
                    )
                }
            }

            // Priority Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Prioridad:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (uiState.esSupervisor) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Solo mis oportunidades",
                            style = MaterialTheme.typography.labelSmall
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Switch(
                            checked = uiState.soloMisOportunidades,
                            onCheckedChange = { viewModel.cambiarSoloMisOportunidades(it) }
                        )
                    }
                }
            }

            val prioridadesFiltro = listOf("TODAS") + Prioridades.TODAS

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(prioridadesFiltro) { prioridad ->
                    FilterChip(
                        selected = uiState.prioridadFiltro == prioridad,
                        onClick = { viewModel.cambiarPrioridadFiltro(prioridad) },
                        label = { Text(if (prioridad == "TODAS") "Todas" else prioridad) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Summary Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mostrando ${uiState.oportunidadesFiltradas.size} resultados",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = "Total: ${DateUtils.formatearMonto(uiState.totalMontoFiltrado)}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.cargando) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.oportunidadesFiltradas.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No se encontraron oportunidades",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Intente cambiando los filtros o la búsqueda",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(uiState.oportunidadesFiltradas, key = { it.oportunidad.id }) { item ->
                        OportunidadCard(
                            item = item,
                            onClick = { onVerDetalle(item.oportunidad.id) }
                        )
                    }
                }
            }
        }

        // Floating Action Button to create opportunity
        FloatingActionButton(
            onClick = onCrearNueva,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.Add, contentDescription = "Crear Oportunidad")
        }
    }
}

@Composable
fun OportunidadCard(
    item: OportunidadConDetalles,
    onClick: () -> Unit
) {
    val op = item.oportunidad
    val estadoColor by animateColorAsState(
        targetValue = when (op.estado) {
            EstadosOportunidad.NUEVA -> StatusNueva
            EstadosOportunidad.EN_GESTION -> StatusEnGestion
            EstadosOportunidad.PROPUESTA -> StatusPropuesta
            EstadosOportunidad.EN_NEGOCIACION -> StatusNegociacion
            EstadosOportunidad.CONFIRMADA -> StatusConfirmada
            EstadosOportunidad.DESCARTADA -> StatusDescartada
            else -> MaterialTheme.colorScheme.primary
        },
        label = "estadoColor"
    )

    val prioridadColor = when (op.prioridad) {
        Prioridades.ALTA -> PriorityAlta
        Prioridades.MEDIA -> PriorityMedia
        Prioridades.BAJA -> PriorityBaja
        else -> MaterialTheme.colorScheme.outline
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Title and Organization
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = op.titulo,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Business,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = item.organizacion.nombre,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Status Badge
                Surface(
                    color = estadoColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = op.estado,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = estadoColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Details row: Amount, Event Date, Priority, Executive
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.AttachMoney,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = StatusConfirmada
                    )
                    Text(
                        text = DateUtils.formatearMonto(op.montoEstimado),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CalendarMonth,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = op.fechaEvento,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Surface(
                    color = prioridadColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "Pri: ${op.prioridad}",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = prioridadColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Executive Assigned
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.ejecutivo?.nombreCompleto ?: "Sin Asignar",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (item.ejecutivo == null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (item.ejecutivo == null) FontWeight.Bold else FontWeight.Normal
                    )
                }

                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}