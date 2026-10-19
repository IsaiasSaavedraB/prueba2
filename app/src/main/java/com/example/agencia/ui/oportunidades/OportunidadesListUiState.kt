package com.example.agencia.ui.oportunidades

import com.example.agencia.model.relations.OportunidadConDetalles

data class OportunidadesListUiState(
    val busqueda: String = "",
    val estadoFiltro: String = "TODAS",
    val prioridadFiltro: String = "TODAS",
    val soloMisOportunidades: Boolean = false,
    val esSupervisor: Boolean = false,
    val oportunidadesFiltradas: List<OportunidadConDetalles> = emptyList(),
    val totalMontoFiltrado: Double = 0.0,
    val cargando: Boolean = true
)