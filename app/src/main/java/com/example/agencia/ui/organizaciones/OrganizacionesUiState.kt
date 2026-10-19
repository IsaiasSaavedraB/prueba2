package com.example.agencia.ui.organizaciones

import com.example.agencia.model.Organizacion
import com.example.agencia.model.relations.ContactoConOrganizacion

data class OrganizacionesUiState(
    val organizaciones: List<Organizacion> = emptyList(),
    val contactosConOrg: List<ContactoConOrganizacion> = emptyList(),

    // Form Nueva Organización State & Validations
    val mostrarDialogoOrg: Boolean = false,
    val nombreOrg: String = "",
    val rutOrg: String = "",
    val industriaOrg: String = "",
    val direccionOrg: String = "",
    val telefonoOrg: String = "",
    val errorNombreOrg: Boolean = false,
    val errorRutOrg: Boolean = false,
    val mensajeErrorRut: String? = null,
    val errorTelefonoOrg: Boolean = false,

    // Form Nuevo Contacto State & Validations
    val mostrarDialogoContacto: Boolean = false,
    val orgIdSeleccionada: Int? = null,
    val nombreContacto: String = "",
    val cargoContacto: String = "",
    val correoContacto: String = "",
    val telefonoContacto: String = "",
    val errorNombreContacto: Boolean = false,
    val errorCorreoContacto: Boolean = false,
    val mensajeErrorCorreoContacto: String? = null,
    val errorTelefonoContacto: Boolean = false,

    val cargando: Boolean = true,
    val mensajeExito: String? = null
)