package com.example.agencia.ui.registro

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.agencia.util.passwordsCoinciden
import com.example.agencia.util.validarApellido
import com.example.agencia.util.validarCorreo
import com.example.agencia.util.validarNombre
import com.example.agencia.util.validarPassword
import com.example.agencia.data.AppDatabase
import com.example.agencia.data.UsuarioRepository
import com.example.agencia.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegistroViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    private val repository = UsuarioRepository(database.usuarioDao())

    private val _uiState = MutableStateFlow(RegistroUiState())
    val uiState: StateFlow<RegistroUiState> = _uiState.asStateFlow()

    fun cambiarNombre(nuevoNombre: String) {
        _uiState.update { estadoActual ->
            estadoActual.copy(nombre = nuevoNombre)
        }
    }

    fun cambiarApellido(nuevoApellido: String) {
        _uiState.update { estadoActual ->
            estadoActual.copy(apellido = nuevoApellido)
        }
    }

    fun cambiarCorreo(nuevoCorreo: String) {
        _uiState.update { estadoActual ->
            estadoActual.copy(correo = nuevoCorreo)
        }
    }

    fun cambiarPassword(nuevoPassword: String) {
        _uiState.update { estadoActual ->
            estadoActual.copy(password = nuevoPassword)
        }
    }

    fun cambiarRepetirPassword(nuevoRepetirPassword: String) {
        _uiState.update { estadoActual ->
            estadoActual.copy(repetirPassword = nuevoRepetirPassword)
        }
    }

    fun cambiarTerminos(nuevoTerminos: Boolean) {
        _uiState.update { estadoActual ->
            estadoActual.copy(aceptaTerminos = nuevoTerminos)
        }
    }

    // Acción del formulario
    fun registrar() {
        val estado = _uiState.value
        val errorNombre = !validarNombre(estado.nombre)
        val errorApellido = !validarApellido(estado.apellido)
        val errorCorreo = !validarCorreo(estado.correo)
        val errorPassword = !validarPassword(estado.password)
        val errorRepetirPassword =
            !passwordsCoinciden(estado.password, estado.repetirPassword)
        val errorTerminos = !estado.aceptaTerminos

        _uiState.update { estadoActual ->
            estadoActual.copy(
                errorNombre = errorNombre,
                errorApellido = errorApellido,
                errorCorreo = errorCorreo,
                errorPassword = errorPassword,
                errorRepetirPassword = errorRepetirPassword,
                errorTerminos = errorTerminos,
            )
        }

        val formularioValido =
            !errorNombre &&
                    !errorApellido &&
                    !errorCorreo &&
                    !errorPassword &&
                    !errorRepetirPassword &&
                    !errorTerminos

        if (formularioValido) {
            val usuario = Usuario(
                nombre = estado.nombre,
                apellido = estado.apellido,
                correo = estado.correo,
                password = estado.password,
            )
            viewModelScope.launch {
                repository.guardar(usuario)
                println("Usuario registrado correctamente")
            }

            _uiState.value = RegistroUiState()
        }

    }
}