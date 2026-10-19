package com.example.agencia.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.agencia.data.AppDatabase
import com.example.agencia.data.repository.UsuarioRepository
import com.example.agencia.model.Usuario
import com.example.agencia.session.SessionManager
import com.example.agencia.util.validarCorreo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val usuarioRepository = UsuarioRepository(
        AppDatabase.getInstance(application).usuarioDao()
    )

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    val usuariosMock = usuarioRepository.obtenerUsuarios()

    fun cambiarCorreo(nuevoCorreo: String) {
        _uiState.update {
            it.copy(correo = nuevoCorreo, errorCorreo = false, mensajeErrorGeneral = null)
        }
    }

    fun cambiarPassword(nuevaPassword: String) {
        _uiState.update {
            it.copy(password = nuevaPassword, errorPassword = false, mensajeErrorGeneral = null)
        }
    }

    fun iniciarSesionConMock(usuario: Usuario) {
        SessionManager.iniciarSesion(usuario)
        _uiState.update { it.copy(loginExitoso = true) }
    }

    fun iniciarSesion() {
        val estado = _uiState.value
        val correoValido = validarCorreo(estado.correo)
        val passwordValida = estado.password.length >= 8

        if (!correoValido) {
            _uiState.update {
                it.copy(
                    errorCorreo = true,
                    mensajeErrorCorreo = "Formato de correo inválido"
                )
            }
            return
        }

        if (!passwordValida) {
            _uiState.update {
                it.copy(
                    errorPassword = true,
                    mensajeErrorPassword = "La contraseña debe tener al menos 8 caracteres"
                )
            }
            return
        }

        _uiState.update { it.copy(cargando = true) }

        viewModelScope.launch {
            val usuario = usuarioRepository.buscarPorCorreo(estado.correo.trim())
            if (usuario != null && usuario.password == estado.password) {
                SessionManager.iniciarSesion(usuario)
                _uiState.update { it.copy(cargando = false, loginExitoso = true) }
            } else {
                _uiState.update {
                    it.copy(
                        cargando = false,
                        mensajeErrorGeneral = "Credenciales incorrectas o usuario no registrado"
                    )
                }
            }
        }
    }

    fun resetEstado() {
        _uiState.value = LoginUiState()
    }
}