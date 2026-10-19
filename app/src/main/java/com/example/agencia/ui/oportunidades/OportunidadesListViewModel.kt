package com.example.agencia.ui.oportunidades

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.agencia.data.AppDatabase
import com.example.agencia.data.repository.OportunidadRepository
import com.example.agencia.session.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class FiltrosOportunidades(
    val busqueda: String = "",
    val estado: String = "TODAS",
    val prioridad: String = "TODAS",
    val soloMis: Boolean = false
)

class OportunidadesListViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val oportunidadRepository = OportunidadRepository(db.oportunidadDao())

    private val _filtros = MutableStateFlow(FiltrosOportunidades())

    private val _uiState = MutableStateFlow(OportunidadesListUiState())
    val uiState: StateFlow<OportunidadesListUiState> = _uiState.asStateFlow()

    init {
        cargarOportunidades()
    }

    fun cambiarBusqueda(query: String) {
        _filtros.value = _filtros.value.copy(busqueda = query)
    }

    fun cambiarEstadoFiltro(estado: String) {
        _filtros.value = _filtros.value.copy(estado = estado)
    }

    fun cambiarPrioridadFiltro(prioridad: String) {
        _filtros.value = _filtros.value.copy(prioridad = prioridad)
    }

    fun cambiarSoloMisOportunidades(soloMis: Boolean) {
        _filtros.value = _filtros.value.copy(soloMis = soloMis)
    }

    private fun cargarOportunidades() {
        viewModelScope.launch {
            combine(
                SessionManager.currentUser,
                oportunidadRepository.obtenerTodasConDetalles(),
                _filtros
            ) { usuario, todas, filtros ->
                if (usuario == null) return@combine OportunidadesListUiState(cargando = false)

                val esSupervisor = usuario.esSupervisor

                // Filter by User Role / Mis Oportunidades
                var lista = if (!esSupervisor || filtros.soloMis) {
                    todas.filter { it.oportunidad.ejecutivoId == usuario.id }
                } else {
                    todas
                }

                // Filter by Search Query
                if (filtros.busqueda.isNotBlank()) {
                    val query = filtros.busqueda.trim().lowercase()
                    lista = lista.filter {
                        it.oportunidad.titulo.lowercase().contains(query) ||
                                it.organizacion.nombre.lowercase().contains(query) ||
                                it.contacto.nombre.lowercase().contains(query) ||
                                it.oportunidad.ubicacion.lowercase().contains(query)
                    }
                }

                // Filter by State
                if (filtros.estado != "TODAS") {
                    lista = lista.filter { it.oportunidad.estado == filtros.estado }
                }

                // Filter by Priority
                if (filtros.prioridad != "TODAS") {
                    lista = lista.filter { it.oportunidad.prioridad == filtros.prioridad }
                }

                val totalMonto = lista.sumOf { it.oportunidad.montoEstimado }

                OportunidadesListUiState(
                    busqueda = filtros.busqueda,
                    estadoFiltro = filtros.estado,
                    prioridadFiltro = filtros.prioridad,
                    soloMisOportunidades = filtros.soloMis,
                    esSupervisor = esSupervisor,
                    oportunidadesFiltradas = lista,
                    totalMontoFiltrado = totalMonto,
                    cargando = false
                )
            }.collect { estado ->
                _uiState.value = estado
            }
        }
    }
}