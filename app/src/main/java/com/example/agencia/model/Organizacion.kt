package com.example.agencia.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "organizaciones")
data class Organizacion(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val rut: String,
    val industria: String,
    val direccion: String,
    val telefono: String
)