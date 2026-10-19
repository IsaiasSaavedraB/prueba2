package com.example.agencia.model.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.agencia.model.Contacto
import com.example.agencia.model.Organizacion

data class ContactoConOrganizacion(
    @Embedded val contacto: Contacto,

    @Relation(
        parentColumn = "organizacionId",
        entityColumn = "id"
    )
    val organizacion: Organizacion
)