package com.example.agencia.ui.dashboard

import com.example.agencia.model.Usuario
import com.example.agencia.model.relations.OportunidadConDetalles

data class DashboardUiState(
    val usuarioActual: Usuario? = null,
    val totalOportunidades: Int = 0,
    val totalPipelineMonto: Double = 0.0,
    val oportunidadesPorEstado: Map<String, Int> = emptyMap(),
    val oportunidadesProximas: List<OportunidadConDetalles> = emptyList(),
    val gestionesAtrasadasCount: Int = 0,
    val sinAsignarCount: Int = 0,
    val cargando: Boolean = true
)