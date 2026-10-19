package com.example.agencia.ui.oportunidades

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.agencia.data.AppDatabase
import com.example.agencia.data.repository.OportunidadRepository
import com.example.agencia.data.repository.SeguimientoRepository
import com.example.agencia.session.SessionManager
import com.example.agencia.util.validarTransicionEstado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OportunidadDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val oportunidadRepository = OportunidadRepository(db.oportunidadDao())
    private val seguimientoRepository = SeguimientoRepository(db.seguimientoDao())

    private val _uiState = MutableStateFlow(OportunidadDetailUiState())
    val uiState: StateFlow<OportunidadesDetailUiStateWrapper> = MutableStateFlow(OportunidadesDetailUiStateWrapper(_uiState)).asStateFlow()
    val estadoUi: StateFlow<OportunidadDetailUiState> = _uiState.asStateFlow()

    fun cargarOportunidad(oportunidadId: Int) {
        viewModelScope.launch {
            combine(
                SessionManager.currentUser,
                oportunidadRepository.obtenerPorIdConDetalles(oportunidadId),
                seguimientoRepository.obtenerPorOportunidad(oportunidadId)
            ) { usuario, detalle, listaSeguimientos ->
                val esSupervisor = usuario?.esSupervisor == true
                _uiState.update {
                    it.copy(
                        oportunidadDetalle = detalle,
                        seguimientos = listaSeguimientos,
                        esSupervisor = esSupervisor,
                        cargando = false
                    )
                }
            }.collect {}
        }
    }

    fun cambiarEstado(nuevoEstado: String) {
        val detalle = _uiState.value.oportunidadDetalle ?: return
        val esSupervisor = _uiState.value.esSupervisor

        val errorTransicion = validarTransicionEstado(
            estadoActual = detalle.oportunidad.estado,
            nuevoEstado = nuevoEstado,
            esSupervisor = esSupervisor
        )

        if (errorTransicion != null) {
            _uiState.update {
                it.copy(
                    mensajeErrorTransicion = errorTransicion,
                    mensajeExito = null
                )
            }
            return
        }

        viewModelScope.launch {
            oportunidadRepository.actualizarEstado(detalle.oportunidad.id, nuevoEstado)
            _uiState.update {
                it.copy(
                    mensajeErrorTransicion = null,
                    mensajeExito = "Estado actualizado a $nuevoEstado exitosamente"
                )
            }
        }
    }

    fun guardarFotoPath(fotoPath: String) {
        val detalle = _uiState.value.oportunidadDetalle ?: return
        viewModelScope.launch {
            oportunidadRepository.actualizarFoto(detalle.oportunidad.id, fotoPath)
            _uiState.update {
                it.copy(mensajeExito = "Evidencia fotográfica guardada con éxito")
            }
        }
    }

    fun limpiarMensajes() {
        _uiState.update {
            it.copy(mensajeErrorTransicion = null, mensajeExito = null)
        }
    }
}

class OportunidadesDetailUiStateWrapper(val state: StateFlow<OportunidadDetailUiState>)