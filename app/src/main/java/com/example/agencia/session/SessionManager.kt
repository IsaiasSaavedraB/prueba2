package com.example.agencia.session

import com.example.agencia.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object SessionManager {

    private val _currentUser = MutableStateFlow<Usuario?>(null)
    val currentUser: StateFlow<Usuario?> = _currentUser.asStateFlow()

    fun iniciarSesion(usuario: Usuario) {
        _currentUser.value = usuario
    }

    fun cerrarSesion() {
        _currentUser.value = null
    }

    val estaAutenticado: Boolean
        get() = _currentUser.value != null

    val esSupervisor: Boolean
        get() = _currentUser.value?.esSupervisor == true

    val usuarioActualId: Int?
        get() = _currentUser.value?.id
}