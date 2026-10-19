package com.example.agencia.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.agencia.model.Oportunidad
import com.example.agencia.model.relations.OportunidadConDetalles
import kotlinx.coroutines.flow.Flow

@Dao
interface OportunidadDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(oportunidad: Oportunidad): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodas(oportunidades: List<Oportunidad>)

    @Update
    suspend fun actualizar(oportunidad: Oportunidad)

    @Transaction
    @Query("SELECT * FROM oportunidades ORDER BY fechaEvento ASC")
    fun obtenerTodasConDetalles(): Flow<List<OportunidadConDetalles>>

    @Transaction
    @Query("SELECT * FROM oportunidades WHERE ejecutivoId = :ejecutivoId ORDER BY fechaEvento ASC")
    fun obtenerPorEjecutivoConDetalles(ejecutivoId: Int): Flow<List<OportunidadConDetalles>>

    @Transaction
    @Query("SELECT * FROM oportunidades WHERE id = :id LIMIT 1")
    fun obtenerPorIdConDetalles(id: Int): Flow<OportunidadConDetalles?>

    @Query("SELECT * FROM oportunidades WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Int): Oportunidad?

    @Query("UPDATE oportunidades SET ejecutivoId = :nuevoEjecutivoId WHERE id = :oportunidadId")
    suspend fun reasignarEjecutivo(oportunidadId: Int, nuevoEjecutivoId: Int?)

    @Query("UPDATE oportunidades SET estado = :nuevoEstado WHERE id = :oportunidadId")
    suspend fun actualizarEstado(oportunidadId: Int, nuevoEstado: String)

    @Query("UPDATE oportunidades SET fotoPath = :fotoPath WHERE id = :oportunidadId")
    suspend fun actualizarFoto(oportunidadId: Int, fotoPath: String?)

    @Query("SELECT COUNT(*) FROM oportunidades")
    suspend fun contar(): Int
}