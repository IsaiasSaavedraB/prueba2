package com.example.agencia.data.repository

import com.example.agencia.data.dao.UsuarioDao
import com.example.agencia.model.Roles
import com.example.agencia.model.Usuario
import kotlinx.coroutines.flow.Flow

class UsuarioRepository(private val usuarioDao: UsuarioDao) {

    suspend fun guardar(usuario: Usuario): Long {
        return usuarioDao.insertar(usuario)
    }

    suspend fun actualizar(usuario: Usuario) {
        usuarioDao.actualizar(usuario)
    }

    fun obtenerUsuarios(): Flow<List<Usuario>> {
        return usuarioDao.obtenerUsuarios()
    }

    fun obtenerEjecutivos(): Flow<List<Usuario>> {
        return usuarioDao.obtenerUsuariosPorRol(Roles.EJECUTIVO)
    }

    suspend fun buscarPorCorreo(correo: String): Usuario? {
        return usuarioDao.obtenerPorCorreo(correo)
    }

    suspend fun buscarPorId(id: Int): Usuario? {
        return usuarioDao.obtenerPorId(id)
    }
}