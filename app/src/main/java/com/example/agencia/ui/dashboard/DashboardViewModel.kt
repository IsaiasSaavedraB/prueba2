package com.example.agencia.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.agencia.data.AppDatabase
import com.example.agencia.data.repository.OportunidadRepository
import com.example.agencia.data.repository.SeguimientoRepository
import com.example.agencia.session.SessionManager
import com.example.agencia.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val oportunidadRepository = OportunidadRepository(db.oportunidadDao())
    private val seguimientoRepository = SeguimientoRepository(db.seguimientoDao())

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        cargarDashboard()
    }

    fun cargarDashboard() {
        viewModelScope.launch {
            combine(
                SessionManager.currentUser,
                oportunidadRepository.obtenerTodasConDetalles(),
                seguimientoRepository.obtenerTodos()
            ) { usuario, todasOportunidades, todosSeguimientos ->
                if (usuario == null) {
                    return@combine DashboardUiState(cargando = false)
                }

                val esSupervisor = usuario.esSupervisor
                val listaRelevante = if (esSupervisor) {
                    todasOportunidades
                } else {
                    todasOportunidades.filter { it.oportunidad.ejecutivoId == usuario.id }
                }

                val totalCount = listaRelevante.size
                val totalMonto = listaRelevante.sumOf { it.oportunidad.montoEstimado }

                val porEstado = listaRelevante.groupBy { it.oportunidad.estado }
                    .mapValues { it.value.size }

                val proximas = listaRelevante
                    .filter { DateUtils.esFechaProxima(it.oportunidad.fechaEvento) }
                    .sortedBy { it.oportunidad.fechaEvento }

                val atrasadasCount = todosSeguimientos
                    .filter { DateUtils.esGestionAtrasada(it.fechaHora) }
                    .size

                val sinAsignar = todasOportunidades.count { it.oportunidad.ejecutivoId == null }

                DashboardUiState(
                    usuarioActual = usuario,
                    totalOportunidades = totalCount,
                    totalPipelineMonto = totalMonto,
                    oportunidadesPorEstado = porEstado,
                    oportunidadesProximas = proximas,
                    gestionesAtrasadasCount = atrasadasCount,
                    sinAsignarCount = sinAsignar,
                    cargando = false
                )
            }.collect { estado ->
                _uiState.value = estado
            }
        }
    }
}