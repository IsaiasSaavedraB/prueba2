package com.example.agencia.ui.oportunidades

import com.example.agencia.model.Seguimiento
import com.example.agencia.model.relations.OportunidadConDetalles

data class OportunidadDetailUiState(
    val oportunidadDetalle: OportunidadConDetalles? = null,
    val seguimientos: List<Seguimiento> = emptyList(),
    val esSupervisor: Boolean = false,
    val mensajeErrorTransicion: String? = null,
    val mensajeExito: String? = null,
    val fotoTempUri: String? = null,
    val cargando: Boolean = true
)