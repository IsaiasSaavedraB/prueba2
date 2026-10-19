package com.example.agencia.ui.organizaciones

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.agencia.data.AppDatabase
import com.example.agencia.data.repository.ContactoRepository
import com.example.agencia.data.repository.OrganizacionRepository
import com.example.agencia.model.Contacto
import com.example.agencia.model.Organizacion
import com.example.agencia.util.validarCorreo
import com.example.agencia.util.validarRUT
import com.example.agencia.util.validarTelefono
import com.example.agencia.util.validarTextoNoVacio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OrganizacionesViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val orgRepo = OrganizacionRepository(db.organizacionDao())
    private val contactoRepo = ContactoRepository(db.contactoDao())

    private val _uiState = MutableStateFlow(OrganizacionesUiState())
    val uiState: StateFlow<OrganizacionesUiState> = _uiState.asStateFlow()

    init {
        cargarDatos()
    }

    private fun cargarDatos() {
        viewModelScope.launch {
            combine(
                orgRepo.obtenerTodas(),
                contactoRepo.obtenerTodosConOrganizacion()
            ) { orgs, contactos ->
                _uiState.update { current ->
                    current.copy(
                        organizaciones = orgs,
                        contactosConOrg = contactos,
                        cargando = false
                    )
                }
            }.collect {}
        }
    }

    // Dialog Org Controls
    fun abrirDialogoOrg() { _uiState.update { it.copy(mostrarDialogoOrg = true) } }
    fun cerrarDialogoOrg() { _uiState.update { it.copy(mostrarDialogoOrg = false) } }
    fun cambiarNombreOrg(value: String) { _uiState.update { it.copy(nombreOrg = value, errorNombreOrg = false) } }
    fun cambiarRutOrg(value: String) { _uiState.update { it.copy(rutOrg = value, errorRutOrg = false) } }
    fun cambiarIndustriaOrg(value: String) { _uiState.update { it.copy(industriaOrg = value) } }
    fun cambiarDireccionOrg(value: String) { _uiState.update { it.copy(direccionOrg = value) } }
    fun cambiarTelefonoOrg(value: String) { _uiState.update { it.copy(telefonoOrg = value, errorTelefonoOrg = false) } }

    fun guardarOrganizacion() {
        val s = _uiState.value
        val errNombre = !validarTextoNoVacio(s.nombreOrg)
        val errRut = !validarRUT(s.rutOrg)
        val errTel = !validarTelefono(s.telefonoOrg)

        _uiState.update {
            it.copy(
                errorNombreOrg = errNombre,
                errorRutOrg = errRut,
                mensajeErrorRut = if (errRut) "RUT inválido. Ejemplo: 76.123.456-7" else null,
                errorTelefonoOrg = errTel
            )
        }

        if (errNombre || errRut || errTel) return

        viewModelScope.launch {
            val org = Organizacion(
                nombre = s.nombreOrg.trim(),
                rut = s.rutOrg.trim(),
                industria = s.industriaOrg.trim(),
                direccion = s.direccionOrg.trim(),
                telefono = s.telefonoOrg.trim()
            )
            orgRepo.guardar(org)
            _uiState.update {
                it.copy(
                    mostrarDialogoOrg = false,
                    nombreOrg = "", rutOrg = "", industriaOrg = "", direccionOrg = "", telefonoOrg = "",
                    mensajeExito = "Organización registrada correctamente"
                )
            }
        }
    }

    // Dialog Contacto Controls
    fun abrirDialogoContacto(orgId: Int? = null) {
        _uiState.update { it.copy(mostrarDialogoContacto = true, orgIdSeleccionada = orgId ?: it.organizaciones.firstOrNull()?.id) }
    }
    fun cerrarDialogoContacto() { _uiState.update { it.copy(mostrarDialogoContacto = false) } }
    fun cambiarOrgContacto(orgId: Int) { _uiState.update { it.copy(orgIdSeleccionada = orgId) } }
    fun cambiarNombreContacto(value: String) { _uiState.update { it.copy(nombreContacto = value, errorNombreContacto = false) } }
    fun cambiarCargoContacto(value: String) { _uiState.update { it.copy(cargoContacto = value) } }
    fun cambiarCorreoContacto(value: String) { _uiState.update { it.copy(correoContacto = value, errorCorreoContacto = false) } }
    fun cambiarTelefonoContacto(value: String) { _uiState.update { it.copy(telefonoContacto = value, errorTelefonoContacto = false) } }

    fun guardarContacto() {
        val s = _uiState.value
        val errNombre = !validarTextoNoVacio(s.nombreContacto)
        val errCorreo = !validarCorreo(s.correoContacto)
        val errTel = !validarTelefono(s.telefonoContacto)
        val errOrg = s.orgIdSeleccionada == null

        _uiState.update {
            it.copy(
                errorNombreContacto = errNombre,
                errorCorreoContacto = errCorreo,
                mensajeErrorCorreoContacto = if (errCorreo) "Formato de correo electrónico inválido" else null,
                errorTelefonoContacto = errTel
            )
        }

        if (errNombre || errCorreo || errTel || errOrg) return

        viewModelScope.launch {
            val contacto = Contacto(
                organizacionId = s.orgIdSeleccionada!!,
                nombre = s.nombreContacto.trim(),
                cargo = s.cargoContacto.trim(),
                correo = s.correoContacto.trim(),
                telefono = s.telefonoContacto.trim()
            )
            contactoRepo.guardar(contacto)
            _uiState.update {
                it.copy(
                    mostrarDialogoContacto = false,
                    nombreContacto = "", cargoContacto = "", correoContacto = "", telefonoContacto = "",
                    mensajeExito = "Contacto corporativo registrado correctamente"
                )
            }
        }
    }

    fun limpiarMensajeExito() {
        _uiState.update { it.copy(mensajeExito = null) }
    }
}