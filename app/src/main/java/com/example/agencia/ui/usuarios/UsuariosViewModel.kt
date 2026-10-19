package com.example.agencia.ui.usuarios

import android.app.Application
import androidx.lifecycle.*
import com.example.agencia.data.*
import com.example.agencia.model.Usuario
import kotlinx.coroutines.flow.*

class UsuariosViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val database =
        AppDatabase.getInstance(application)
    private val repository =
        UsuarioRepository(database.usuarioDao())

    val usuarios: StateFlow<List<Usuario>> =
        repository
            .obtenerUsuarios()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
}
