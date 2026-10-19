package com.example.agencia.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

object TiposGestion {
    const val LLAMADA = "Llamada Telefónica"
    const val REUNION = "Reunión Presencial/Virtual"
    const val CORREO = "Envío de Correo/Propuesta"
    const val VISITA = "Visita a Terreno"

    val TODOS = listOf(LLAMADA, REUNION, CORREO, VISITA)
}

@Entity(
    tableName = "seguimientos",
    foreignKeys = [
        ForeignKey(
            entity = Oportunidad::class,
            parentColumns = ["id"],
            childColumns = ["oportunidadId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["oportunidadId"]),
        Index(value = ["usuarioId"])
    ]
)
data class Seguimiento(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val oportunidadId: Int,
    val usuarioId: Int,
    val fechaHora: String, // ISO YYYY-MM-DD HH:mm
    val tipoGestion: String,
    val descripcion: String,
    val nuevoEstado: String? = null
)