package com.example.agencia.ui.registro

data class RegistroUiState (
    // Variables
    var nombre:  String = "",
    var apellido: String = "",
    var correo: String = "",
    var password: String = "",
    var repetirPassword: String = "",
    var aceptaTerminos: Boolean = false,

    // Estados de error
    var errorNombre: Boolean = false,
    var errorApellido: Boolean = false,
    var errorCorreo: Boolean = false,
    var errorPassword: Boolean = false,
    var errorRepetirPassword: Boolean = false,
    var errorTerminos: Boolean = false
)