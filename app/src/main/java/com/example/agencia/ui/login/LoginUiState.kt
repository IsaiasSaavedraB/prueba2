package com.example.agencia.ui.login

data class LoginUiState(
    val correo: String = "",
    val password: String = "",
    val errorCorreo: Boolean = false,
    val mensajeErrorCorreo: String? = null,
    val errorPassword: Boolean = false,
    val mensajeErrorPassword: String? = null,
    val mensajeErrorGeneral: String? = null,
    val cargando: Boolean = false,
    val loginExitoso: Boolean = false
)