package com.example.agencia.ui.seguimientos

import com.example.agencia.model.Seguimiento
import com.example.agencia.model.TiposGestion
import com.example.agencia.model.relations.OportunidadConDetalles

data class SeguimientoUiState(
    val oportunidadDetalle: OportunidadConDetalles? = null,
    val historialSeguimientos: List<Seguimiento> = emptyList(),

    // Form
    val tipoGestion: String = TiposGestion.LLAMADA,
    val descripcion: String = "",
    val actualizarEstado: Boolean = false,
    val nuevoEstado: String = "",

    val errorDescripcion: Boolean = false,
    val mensajeErrorDescripcion: String? = null,

    val cargando: Boolean = true,
    val guardadoExitoso: Boolean = false
)