package com.example.agencia.data.repository

import com.example.agencia.data.dao.SeguimientoDao
import com.example.agencia.model.Seguimiento
import kotlinx.coroutines.flow.Flow

class SeguimientoRepository(private val seguimientoDao: SeguimientoDao) {

    suspend fun guardar(seguimiento: Seguimiento): Long {
        return seguimientoDao.insertar(seguimiento)
    }

    fun obtenerPorOportunidad(oportunidadId: Int): Flow<List<Seguimiento>> {
        return seguimientoDao.obtenerPorOportunidad(oportunidadId)
    }

    fun obtenerTodos(): Flow<List<Seguimiento>> {
        return seguimientoDao.obtenerTodos()
    }
}