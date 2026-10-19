package com.example.agencia.model.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.agencia.model.Contacto
import com.example.agencia.model.Oportunidad
import com.example.agencia.model.Organizacion
import com.example.agencia.model.Usuario

data class OportunidadConDetalles(
    @Embedded val oportunidad: Oportunidad,

    @Relation(
        parentColumn = "organizacionId",
        entityColumn = "id"
    )
    val organizacion: Organizacion,

    @Relation(
        parentColumn = "contactoId",
        entityColumn = "id"
    )
    val contacto: Contacto,

    @Relation(
        parentColumn = "ejecutivoId",
        entityColumn = "id"
    )
    val ejecutivo: Usuario?
)