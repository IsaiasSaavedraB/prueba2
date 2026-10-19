package com.example.agencia.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

object EstadosOportunidad {
    const val NUEVA = "NUEVA"
    const val EN_GESTION = "EN_GESTION"
    const val PROPUESTA = "PROPUESTA"
    const val EN_NEGOCIACION = "EN_NEGOCIACION"
    const val CONFIRMADA = "CONFIRMADA"
    const val DESCARTADA = "DESCARTADA"

    val TODOS = listOf(NUEVA, EN_GESTION, PROPUESTA, EN_NEGOCIACION, CONFIRMADA, DESCARTADA)
}

object Prioridades {
    const val ALTA = "ALTA"
    const val MEDIA = "MEDIA"
    const val BAJA = "BAJA"

    val TODAS = listOf(ALTA, MEDIA, BAJA)
}

object TiposEvento {
    const val CONFERENCIA = "Conferencia"
    const val FERIA = "Feria Comercial"
    const val SEMINARIO = "Seminario / Workshop"
    const val LANZAMIENTO = "Lanzamiento de Producto"
    const val CORPORATIVO = "Evento Corporativo"

    val TODOS = listOf(CONFERENCIA, FERIA, SEMINARIO, LANZAMIENTO, CORPORATIVO)
}

@Entity(
    tableName = "oportunidades",
    foreignKeys = [
        ForeignKey(
            entity = Organizacion::class,
            parentColumns = ["id"],
            childColumns = ["organizacionId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = Contacto::class,
            parentColumns = ["id"],
            childColumns = ["contactoId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["ejecutivoId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["organizacionId"]),
        Index(value = ["contactoId"]),
        Index(value = ["ejecutivoId"])
    ]
)
data class Oportunidad(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titulo: String,
    val organizacionId: Int,
    val contactoId: Int,
    val ejecutivoId: Int? = null,
    val montoEstimado: Double,
    val fechaEvento: String, // ISO YYYY-MM-DD
    val ubicacion: String,
    val tipoEvento: String,
    val publicoEsperado: Int,
    val prioridad: String = Prioridades.MEDIA,
    val estado: String = EstadosOportunidad.NUEVA,
    val fotoPath: String? = null,
    val fechaCreacion: String
)