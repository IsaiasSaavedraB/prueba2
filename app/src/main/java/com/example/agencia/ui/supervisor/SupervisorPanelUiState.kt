package com.example.agencia.ui.supervisor

import com.example.agencia.model.Usuario
import com.example.agencia.model.relations.OportunidadConDetalles

data class ResumenEjecutivo(
    val ejecutivo: Usuario,
    val cantidadOportunidades: Int,
    val montoTotalPipeline: Double
)

data class SupervisorPanelUiState(
    val esSupervisor: Boolean = false,
    val ejecutivosResumen: List<ResumenEjecutivo> = emptyList(),
    val ejecutivosDisponibles: List<Usuario> = emptyList(),
    val todasOportunidades: List<OportunidadConDetalles> = emptyList(),
    val oportunidadesSinAsignar: List<OportunidadConDetalles> = emptyList(),

    // Reassignment Dialog State
    val oportunidadParaReasignar: OportunidadConDetalles? = null,
    val ejecutivoSeleccionadoId: Int? = null,

    val cargando: Boolean = true,
    val mensajeExito: String? = null
)