package com.example.agencia.ui.supervisor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.agencia.data.AppDatabase
import com.example.agencia.data.repository.OportunidadRepository
import com.example.agencia.data.repository.UsuarioRepository
import com.example.agencia.model.relations.OportunidadConDetalles
import com.example.agencia.session.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SupervisorPanelViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val oportunidadRepo = OportunidadRepository(db.oportunidadDao())
    private val usuarioRepo = UsuarioRepository(db.usuarioDao())

    private val _uiState = MutableStateFlow(SupervisorPanelUiState())
    val uiState: StateFlow<SupervisorPanelUiState> = _uiState.asStateFlow()

    init {
        cargarDatos()
    }

    private fun cargarDatos() {
        viewModelScope.launch {
            combine(
                SessionManager.currentUser,
                usuarioRepo.obtenerEjecutivos(),
                oportunidadRepo.obtenerTodasConDetalles()
            ) { usuario, ejecutivos, oportunidades ->
                val esSupervisor = usuario?.esSupervisor == true

                val resumenes = ejecutivos.map { ej ->
                    val deEjecutivo = oportunidades.filter { it.oportunidad.ejecutivoId == ej.id }
                    ResumenEjecutivo(
                        ejecutivo = ej,
                        cantidadOportunidades = deEjecutivo.size,
                        montoTotalPipeline = deEjecutivo.sumOf { it.oportunidad.montoEstimado }
                    )
                }

                val sinAsignar = oportunidades.filter { it.oportunidad.ejecutivoId == null }

                _uiState.update { current ->
                    current.copy(
                        esSupervisor = esSupervisor,
                        ejecutivosResumen = resumenes,
                        ejecutivosDisponibles = ejecutivos,
                        todasOportunidades = oportunidades,
                        oportunidadesSinAsignar = sinAsignar,
                        cargando = false
                    )
                }
            }.collect {}
        }
    }

    fun abrirDialogoReasignar(item: OportunidadConDetalles) {
        _uiState.update {
            it.copy(
                oportunidadParaReasignar = item,
                ejecutivoSeleccionadoId = item.oportunidad.ejecutivoId
            )
        }
    }

    fun cerrarDialogoReasignar() {
        _uiState.update { it.copy(oportunidadParaReasignar = null) }
    }

    fun seleccionarEjecutivoParaReasignar(ejecutivoId: Int?) {
        _uiState.update { it.copy(ejecutivoSeleccionadoId = ejecutivoId) }
    }

    // Requirement 1: Supervisor Assignment / Reassignment
    fun reasignarOportunidad() {
        val opItem = _uiState.value.oportunidadParaReasignar ?: return
        val nuevoEjecutivoId = _uiState.value.ejecutivoSeleccionadoId

        viewModelScope.launch {
            oportunidadRepo.reasignarEjecutivo(opItem.oportunidad.id, nuevoEjecutivoId)
            val ejecutivoNombre = _uiState.value.ejecutivosDisponibles.find { it.id == nuevoEjecutivoId }?.nombreCompleto ?: "Sin Asignar"
            _uiState.update {
                it.copy(
                    oportunidadParaReasignar = null,
                    mensajeExito = "Oportunidad reasignada a $ejecutivoNombre correctamente"
                )
            }
        }
    }

    fun limpiarMensajeExito() {
        _uiState.update { it.copy(mensajeExito = null) }
    }
}