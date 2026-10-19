package com.example.agencia.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.agencia.model.Contacto
import com.example.agencia.model.relations.ContactoConOrganizacion
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(contacto: Contacto): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(contactos: List<Contacto>)

    @Update
    suspend fun actualizar(contacto: Contacto)

    @Query("SELECT * FROM contactos ORDER BY nombre ASC")
    fun obtenerTodos(): Flow<List<Contacto>>

    @Transaction
    @Query("SELECT * FROM contactos ORDER BY nombre ASC")
    fun obtenerTodosConOrganizacion(): Flow<List<ContactoConOrganizacion>>

    @Query("SELECT * FROM contactos WHERE organizacionId = :organizacionId ORDER BY nombre ASC")
    fun obtenerPorOrganizacion(organizacionId: Int): Flow<List<Contacto>>

    @Query("SELECT * FROM contactos WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Int): Contacto?

    @Query("SELECT COUNT(*) FROM contactos")
    suspend fun contar(): Int
}