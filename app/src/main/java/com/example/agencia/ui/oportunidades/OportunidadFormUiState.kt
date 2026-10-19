package com.example.agencia.ui.oportunidades

import com.example.agencia.model.Contacto
import com.example.agencia.model.EstadosOportunidad
import com.example.agencia.model.Organizacion
import com.example.agencia.model.Prioridades
import com.example.agencia.model.TiposEvento
import com.example.agencia.model.Usuario

data class OportunidadFormUiState(
    val id: Int = 0,
    val titulo: String = "",
    val organizacionId: Int? = null,
    val contactoId: Int? = null,
    val ejecutivoId: Int? = null,
    val montoStr: String = "",
    val fechaEvento: String = "",
    val ubicacion: String = "",
    val tipoEvento: String = TiposEvento.CONFERENCIA,
    val publicoStr: String = "",
    val prioridad: String = Prioridades.MEDIA,
    val estado: String = EstadosOportunidad.NUEVA,

    // Validation Flags
    val errorTitulo: Boolean = false,
    val mensajeErrorTitulo: String? = null,
    val errorOrganizacion: Boolean = false,
    val errorContacto: Boolean = false,
    val errorMonto: Boolean = false,
    val mensajeErrorMonto: String? = null,
    val errorFecha: Boolean = false,
    val mensajeErrorFecha: String? = null,
    val errorUbicacion: Boolean = false,
    val errorPublico: Boolean = false,
    val mensajeErrorPublico: String? = null,

    // Lists for Dropdowns
    val organizacionesDisponibles: List<Organizacion> = emptyList(),
    val contactosDisponibles: List<Contacto> = emptyList(),
    val ejecutivosDisponibles: List<Usuario> = emptyList(),

    val esSupervisor: Boolean = false,
    val esEdicion: Boolean = false,
    val cargando: Boolean = false,
    val guardadoExitoso: Boolean = false
)