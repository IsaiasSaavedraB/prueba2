package com.example.agencia.ui.seguimientos

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.agencia.data.AppDatabase
import com.example.agencia.data.repository.OportunidadRepository
import com.example.agencia.data.repository.SeguimientoRepository
import com.example.agencia.model.Seguimiento
import com.example.agencia.session.SessionManager
import com.example.agencia.util.DateUtils
import com.example.agencia.util.validarTextoNoVacio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SeguimientoViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val oportunidadRepo = OportunidadRepository(db.oportunidadDao())
    private val seguimientoRepo = SeguimientoRepository(db.seguimientoDao())

    private val _uiState = MutableStateFlow(SeguimientoUiState())
    val uiState: StateFlow<SeguimientoUiState> = _uiState.asStateFlow()

    fun cargarOportunidad(oportunidadId: Int) {
        viewModelScope.launch {
            combine(
                oportunidadRepo.obtenerPorIdConDetalles(oportunidadId),
                seguimientoRepo.obtenerPorOportunidad(oportunidadId)
            ) { opDetalle, seguimientos ->
                _uiState.update { current ->
                    current.copy(
                        oportunidadDetalle = opDetalle,
                        historialSeguimientos = seguimientos,
                        nuevoEstado = opDetalle?.oportunidad?.estado ?: "",
                        cargando = false
                    )
                }
            }.collect {}
        }
    }

    fun cambiarTipoGestion(tipo: String) { _uiState.update { it.copy(tipoGestion = tipo) } }
    fun cambiarDescripcion(texto: String) { _uiState.update { it.copy(descripcion = texto, errorDescripcion = false) } }
    fun cambiarActualizarEstado(check: Boolean) { _uiState.update { it.copy(actualizarEstado = check) } }
    fun cambiarNuevoEstado(estado: String) { _uiState.update { it.copy(nuevoEstado = estado) } }

    fun registrarSeguimiento() {
        val s = _uiState.value
        val op = s.oportunidadDetalle?.oportunidad ?: return
        val usuarioId = SessionManager.usuarioActualId ?: return

        val errorDesc = !validarTextoNoVacio(s.descripcion) || s.descripcion.trim().length < 5

        if (errorDesc) {
            _uiState.update {
                it.copy(
                    errorDescripcion = true,
                    mensajeErrorDescripcion = "Ingrese una descripción detallada de la gestión (mínimo 5 caracteres)"
                )
            }
            return
        }

        _uiState.update { it.copy(cargando = true) }

        viewModelScope.launch {
            val seguimiento = Seguimiento(
                oportunidadId = op.id,
                usuarioId = usuarioId,
                fechaHora = DateUtils.obtenerFechaHoraActual(),
                tipoGestion = s.tipoGestion,
                descripcion = s.descripcion.trim(),
                nuevoEstado = if (s.actualizarEstado) s.nuevoEstado else null
            )

            seguimientoRepo.guardar(seguimiento)

            if (s.actualizarEstado && s.nuevoEstado.isNotBlank()) {
                oportunidadRepo.actualizarEstado(op.id, s.nuevoEstado)
            }

            _uiState.update {
                it.copy(
                    cargando = false,
                    guardadoExitoso = true,
                    descripcion = ""
                )
            }
        }
    }
}