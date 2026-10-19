package com.example.agencia.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.agencia.model.Seguimiento
import kotlinx.coroutines.flow.Flow

@Dao
interface SeguimientoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(seguimiento: Seguimiento): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(seguimientos: List<Seguimiento>)

    @Query("SELECT * FROM seguimientos WHERE oportunidadId = :oportunidadId ORDER BY fechaHora DESC")
    fun obtenerPorOportunidad(oportunidadId: Int): Flow<List<Seguimiento>>

    @Query("SELECT * FROM seguimientos ORDER BY fechaHora DESC")
    fun obtenerTodos(): Flow<List<Seguimiento>>

    @Query("SELECT COUNT(*) FROM seguimientos")
    suspend fun contar(): Int
}