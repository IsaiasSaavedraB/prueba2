package com.example.agencia.data.repository

import com.example.agencia.data.dao.OrganizacionDao
import com.example.agencia.model.Organizacion
import kotlinx.coroutines.flow.Flow

class OrganizacionRepository(private val organizacionDao: OrganizacionDao) {

    suspend fun guardar(organizacion: Organizacion): Long {
        return organizacionDao.insertar(organizacion)
    }

    suspend fun actualizar(organizacion: Organizacion) {
        organizacionDao.actualizar(organizacion)
    }

    fun obtenerTodas(): Flow<List<Organizacion>> {
        return organizacionDao.obtenerTodas()
    }

    suspend fun buscarPorId(id: Int): Organizacion? {
        return organizacionDao.obtenerPorId(id)
    }
}