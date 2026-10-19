package com.example.agencia.util

import com.example.agencia.model.EstadosOportunidad

fun validarNombre(nombre: String): Boolean {
    return nombre.trim().length >= 2
}

fun validarApellido(apellido: String): Boolean {
    return apellido.trim().length >= 2
}

fun validarCorreo(correo: String): Boolean {
    val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}\$")
    return correo.trim().matches(emailRegex)
}

fun validarTelefono(telefono: String): Boolean {
    val clean = telefono.trim()
    return clean.length >= 8 && clean.all { it.isDigit() || it == '+' || it == ' ' || it == '-' }
}

fun validarRUT(rut: String): Boolean {
    val clean = rut.trim().replace(".", "").replace("-", "").uppercase()
    if (clean.length < 8) return false
    val cuerpo = clean.substring(0, clean.length - 1)
    val dv = clean.last()
    if (!cuerpo.all { it.isDigit() }) return false

    var suma = 0
    var multiplicador = 2
    for (i in cuerpo.length - 1 downTo 0) {
        suma += (cuerpo[i] - '0') * multiplicador
        multiplicador = if (multiplicador == 7) 2 else multiplicador + 1
    }
    val resto = 11 - (suma % 11)
    val dvEsperado = when (resto) {
        11 -> '0'
        10 -> 'K'
        else -> resto.toString()[0]
    }
    return dv == dvEsperado
}

fun validarMonto(montoStr: String): Boolean {
    val monto = montoStr.toDoubleOrNull()
    return monto != null && monto >= 0
}

fun validarPublico(publicoStr: String): Boolean {
    val pub = publicoStr.toIntOrNull()
    return pub != null && pub >= 0
}

fun validarTextoNoVacio(texto: String): Boolean {
    return texto.trim().isNotBlank()
}

fun validarPassword(password: String): Boolean {
    return password.length >= 8
}

fun passwordsCoinciden(password: String, repetirPassword: String): Boolean {
    return password == repetirPassword
}

/**
 * Business Rule Check: Transitions between opportunity states.
 * - Executives CANNOT confirm or discard opportunities.
 * - Allowed states transition flow: NUEVA -> EN_GESTION -> PROPUESTA -> EN_NEGOCIACION -> CONFIRMADA / DESCARTADA.
 */
fun validarTransicionEstado(
    estadoActual: String,
    nuevoEstado: String,
    esSupervisor: Boolean
): String? {
    if (estadoActual == nuevoEstado) return null

    // Executive restriction
    if (!esSupervisor) {
        if (nuevoEstado == EstadosOportunidad.CONFIRMADA || nuevoEstado == EstadosOportunidad.DESCARTADA) {
            return "Solo un Supervisor Comercial puede confirmar o descartar definitivamente una oportunidad."
        }
    }

    // Terminal state restriction
    if (estadoActual == EstadosOportunidad.CONFIRMADA || estadoActual == EstadosOportunidad.DESCARTADA) {
        if (!esSupervisor) {
            return "La oportunidad ya se encuentra en estado final ($estadoActual)."
        }
    }

    return null
}