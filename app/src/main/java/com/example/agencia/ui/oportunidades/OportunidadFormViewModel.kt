package com.example.agencia.ui.oportunidades

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.agencia.data.AppDatabase
import com.example.agencia.data.repository.ContactoRepository
import com.example.agencia.data.repository.OportunidadRepository
import com.example.agencia.data.repository.OrganizacionRepository
import com.example.agencia.data.repository.UsuarioRepository
import com.example.agencia.model.Oportunidad
import com.example.agencia.session.SessionManager
import com.example.agencia.util.DateUtils
import com.example.agencia.util.validarMonto
import com.example.agencia.util.validarPublico
import com.example.agencia.util.validarTextoNoVacio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OportunidadFormViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val oportunidadRepo = OportunidadRepository(db.oportunidadDao())
    private val organizacionRepo = OrganizacionRepository(db.organizacionDao())
    private val contactoRepo = ContactoRepository(db.contactoDao())
    private val usuarioRepo = UsuarioRepository(db.usuarioDao())

    private val _uiState = MutableStateFlow(OportunidadFormUiState())
    val uiState: StateFlow<OportunidadFormUiState> = _uiState.asStateFlow()

    init {
        cargarListasBase()
    }

    private fun cargarListasBase() {
        viewModelScope.launch {
            combine(
                SessionManager.currentUser,
                organizacionRepo.obtenerTodas(),
                usuarioRepo.obtenerEjecutivos()
            ) { usuario, orgs, ejecutivos ->
                val esSupervisor = usuario?.esSupervisor == true
                val ejecutivoDefecto = if (!esSupervisor) usuario?.id else null

                _uiState.update { current ->
                    current.copy(
                        organizacionesDisponibles = orgs,
                        ejecutivosDisponibles = ejecutivos,
                        esSupervisor = esSupervisor,
                        ejecutivoId = current.ejecutivoId ?: ejecutivoDefecto
                    )
                }
            }.collect {}
        }
    }

    fun cargarParaEditar(oportunidadId: Int) {
        if (oportunidadId <= 0) return
        viewModelScope.launch {
            val op = oportunidadRepo.buscarPorId(oportunidadId) ?: return@launch
            cargarContactosPorOrganizacion(op.organizacionId)

            _uiState.update { current ->
                current.copy(
                    id = op.id,
                    titulo = op.titulo,
                    organizacionId = op.organizacionId,
                    contactoId = op.contactoId,
                    ejecutivoId = op.ejecutivoId,
                    montoStr = if (op.montoEstimado % 1.0 == 0.0) op.montoEstimado.toLong().toString() else op.montoEstimado.toString(),
                    fechaEvento = op.fechaEvento,
                    ubicacion = op.ubicacion,
                    tipoEvento = op.tipoEvento,
                    publicoStr = op.publicoEsperado.toString(),
                    prioridad = op.prioridad,
                    estado = op.estado,
                    esEdicion = true
                )
            }
        }
    }

    fun cambiarOrganizacion(orgId: Int) {
        _uiState.update { it.copy(organizacionId = orgId, contactoId = null, errorOrganizacion = false) }
        cargarContactosPorOrganizacion(orgId)
    }

    private fun cargarContactosPorOrganizacion(orgId: Int) {
        viewModelScope.launch {
            val contactos = contactoRepo.obtenerPorOrganizacion(orgId).first()
            _uiState.update { it.copy(contactosDisponibles = contactos) }
        }
    }

    fun cambiarTitulo(texto: String) { _uiState.update { it.copy(titulo = texto, errorTitulo = false) } }
    fun cambiarContacto(contactoId: Int) { _uiState.update { it.copy(contactoId = contactoId, errorContacto = false) } }
    fun cambiarEjecutivo(ejecutivoId: Int?) { _uiState.update { it.copy(ejecutivoId = ejecutivoId) } }
    fun cambiarMonto(monto: String) { _uiState.update { it.copy(montoStr = monto, errorMonto = false) } }
    fun cambiarFecha(fecha: String) { _uiState.update { it.copy(fechaEvento = fecha, errorFecha = false) } }
    fun cambiarUbicacion(ubicacion: String) { _uiState.update { it.copy(ubicacion = ubicacion, errorUbicacion = false) } }
    fun cambiarTipoEvento(tipo: String) { _uiState.update { it.copy(tipoEvento = tipo) } }
    fun cambiarPublico(publico: String) { _uiState.update { it.copy(publicoStr = publico, errorPublico = false) } }
    fun cambiarPrioridad(prioridad: String) { _uiState.update { it.copy(prioridad = prioridad) } }
    fun cambiarEstado(estado: String) { _uiState.update { it.copy(estado = estado) } }

    // Requirement 4: Field Validations & Business Logic
    fun guardar() {
        val s = _uiState.value

        val errorTitulo = !validarTextoNoVacio(s.titulo)
        val errorOrg = s.organizacionId == null
        val errorContacto = s.contactoId == null
        val errorMonto = !validarMonto(s.montoStr)
        val errorFecha = !validarTextoNoVacio(s.fechaEvento) || !s.fechaEvento.matches(Regex("^\\d{4}-\\d{2}-\\d{2}\$"))
        val errorUbicacion = !validarTextoNoVacio(s.ubicacion)
        val errorPublico = !validarPublico(s.publicoStr)

        _uiState.update { current ->
            current.copy(
                errorTitulo = errorTitulo,
                mensajeErrorTitulo = if (errorTitulo) "El título es obligatorio" else null,
                errorOrganizacion = errorOrg,
                errorContacto = errorContacto,
                errorMonto = errorMonto,
                mensajeErrorMonto = if (errorMonto) "Monto debe ser un número positivo mayor o igual a 0" else null,
                errorFecha = errorFecha,
                mensajeErrorFecha = if (errorFecha) "Ingrese una fecha válida en formato YYYY-MM-DD" else null,
                errorUbicacion = errorUbicacion,
                errorPublico = errorPublico,
                mensajeErrorPublico = if (errorPublico) "Ingrese una cantidad válida de público" else null
            )
        }

        val esValido = !errorTitulo && !errorOrg && !errorContacto && !errorMonto && !errorFecha && !errorUbicacion && !errorPublico

        if (!esValido) return

        _uiState.update { it.copy(cargando = true) }

        viewModelScope.launch {
            val oportunidad = Oportunidad(
                id = s.id,
                titulo = s.titulo.trim(),
                organizacionId = s.organizacionId!!,
                contactoId = s.contactoId!!,
                ejecutivoId = s.ejecutivoId,
                montoEstimado = s.montoStr.toDouble(),
                fechaEvento = s.fechaEvento.trim(),
                ubicacion = s.ubicacion.trim(),
                tipoEvento = s.tipoEvento,
                publicoEsperado = s.publicoStr.toInt(),
                prioridad = s.prioridad,
                estado = s.estado,
                fechaCreacion = if (s.esEdicion) DateUtils.obtenerFechaActual() else DateUtils.obtenerFechaActual()
            )

            if (s.esEdicion) {
                oportunidadRepo.actualizar(oportunidad)
            } else {
                oportunidadRepo.guardar(oportunidad)
            }

            _uiState.update { it.copy(cargando = false, guardadoExitoso = true) }
        }
    }
}