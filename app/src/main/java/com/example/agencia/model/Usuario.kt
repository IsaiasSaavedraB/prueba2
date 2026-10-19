package com.example.agencia.model

import androidx.room.Entity
import androidx.room.PrimaryKey

object Roles {
    const val EJECUTIVO = "Ejecutivo Comercial"
    const val SUPERVISOR = "Supervisor Comercial"
}

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val apellido: String,
    val correo: String,
    val password: String,
    val rol: String = Roles.EJECUTIVO,
    val telefono: String = ""
) {
    val nombreCompleto: String
        get() = "$nombre $apellido"

    val esSupervisor: Boolean
        get() = rol == Roles.SUPERVISOR
}