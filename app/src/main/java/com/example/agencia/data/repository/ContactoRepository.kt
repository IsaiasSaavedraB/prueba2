package com.example.agencia.data.repository

import com.example.agencia.data.dao.ContactoDao
import com.example.agencia.model.Contacto
import com.example.agencia.model.relations.ContactoConOrganizacion
import kotlinx.coroutines.flow.Flow

class ContactoRepository(private val contactoDao: ContactoDao) {

    suspend fun guardar(contacto: Contacto): Long {
        return contactoDao.insertar(contacto)
    }

    suspend fun actualizar(contacto: Contacto) {
        contactoDao.actualizar(contacto)
    }

    fun obtenerTodos(): Flow<List<Contacto>> {
        return contactoDao.obtenerTodos()
    }

    fun obtenerTodosConOrganizacion(): Flow<List<ContactoConOrganizacion>> {
        return contactoDao.obtenerTodosConOrganizacion()
    }

    fun obtenerPorOrganizacion(organizacionId: Int): Flow<List<Contacto>> {
        return contactoDao.obtenerPorOrganizacion(organizacionId)
    }

    suspend fun buscarPorId(id: Int): Contacto? {
        return contactoDao.obtenerPorId(id)
    }
}