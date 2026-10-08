package com.example.gestionalmacenpda.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ubicaciones")
data class Ubicaciones(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "codigo")
    val codigo: String,
    @ColumnInfo(name = "descripcion")
    val descripcion: String?
)