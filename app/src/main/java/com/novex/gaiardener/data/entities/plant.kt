package com.novex.gaiardener.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plant")
data class Plant(
    @PrimaryKey(autoGenerate = true) val plantId: Int = 0,
    val nombre: String,
    val nombreCientifico: String,
    val datosGenerales: String,
    val datosCuriosos: String,
    val rangoTemperatura: String,
    val rangoPh: String,
    val rangoHumedad: String,
    val rangoLuzSolar: String,
    val recomendaciones: String,
    val imagenes: List<String>
)
