package com.example.agencia.data.repository

import com.example.agencia.data.dao.OportunidadDao
import com.example.agencia.model.Oportunidad
import com.example.agencia.model.relations.OportunidadConDetalles
import kotlinx.coroutines.flow.Flow

class OportunidadRepository(private val oportunidadDao: OportunidadDao) {

    suspend fun guardar(oportunidad: Oportunidad): Long {
        return oportunidadDao.insertar(oportunidad)
    }

    suspend fun actualizar(oportunidad: Oportunidad) {
        oportunidadDao.actualizar(oportunidad)
    }

    fun obtenerTodasConDetalles(): Flow<List<OportunidadConDetalles>> {
        return oportunidadDao.obtenerTodasConDetalles()
    }

    fun obtenerPorEjecutivoConDetalles(ejecutivoId: Int): Flow<List<OportunidadConDetalles>> {
        return oportunidadDao.obtenerPorEjecutivoConDetalles(ejecutivoId)
    }

    fun obtenerPorIdConDetalles(id: Int): Flow<OportunidadConDetalles?> {
        return oportunidadDao.obtenerPorIdConDetalles(id)
    }

    suspend fun buscarPorId(id: Int): Oportunidad? {
        return oportunidadDao.obtenerPorId(id)
    }

    suspend fun reasignarEjecutivo(oportunidadId: Int, nuevoEjecutivoId: Int?) {
        oportunidadDao.reasignarEjecutivo(oportunidadId, nuevoEjecutivoId)
    }

    suspend fun actualizarEstado(oportunidadId: Int, nuevoEstado: String) {
        oportunidadDao.actualizarEstado(oportunidadId, nuevoEstado)
    }

    suspend fun actualizarFoto(oportunidadId: Int, fotoPath: String?) {
        oportunidadDao.actualizarFoto(oportunidadId, fotoPath)
    }
}