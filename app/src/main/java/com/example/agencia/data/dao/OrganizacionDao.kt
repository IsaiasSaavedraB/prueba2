package com.example.agencia.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.agencia.model.Organizacion
import kotlinx.coroutines.flow.Flow

@Dao
interface OrganizacionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(organizacion: Organizacion): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodas(organizaciones: List<Organizacion>)

    @Update
    suspend fun actualizar(organizacion: Organizacion)

    @Query("SELECT * FROM organizaciones ORDER BY nombre ASC")
    fun obtenerTodas(): Flow<List<Organizacion>>

    @Query("SELECT * FROM organizaciones WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Int): Organizacion?

    @Query("SELECT COUNT(*) FROM organizaciones")
    suspend fun contar(): Int
}