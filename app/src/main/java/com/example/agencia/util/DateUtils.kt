package com.example.agencia.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {

    fun formatearMonto(monto: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale.Builder().setLanguage("es").setRegion("CL").build())
        return format.format(monto)
    }

    fun obtenerFechaActual(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    fun obtenerFechaHoraActual(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return sdf.format(Date())
    }

    fun esFechaProxima(fechaISO: String, diasMargen: Int = 30): Boolean {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val fechaObj = sdf.parse(fechaISO) ?: return false
            val hoy = Date()
            val diffMs = fechaObj.time - hoy.time
            val diffDias = diffMs / (1000 * 60 * 60 * 24)
            diffDias in 0..diasMargen
        } catch (_: Exception) {
            false
        }
    }

    fun esGestionAtrasada(fechaHoraISO: String, diasAtraso: Int = 7): Boolean {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val fechaObj = sdf.parse(fechaHoraISO) ?: return false
            val hoy = Date()
            val diffMs = hoy.time - fechaObj.time
            val diffDias = diffMs / (1000 * 60 * 60 * 24)
            diffDias >= diasAtraso
        } catch (_: Exception) {
            false
        }
    }
}