package com.example.gestionalmacenpda.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class RolUsuario {
    administrador,
    operario
}

@Entity(
    tableName = "usuarios",
    foreignKeys = [
        ForeignKey(
            entity = Rol::class,
            parentColumns = ["id"],
            childColumns = ["rol_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("rol_id"), Index("username", unique = true)]
)
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "username")
    val username: String,

    @ColumnInfo(name = "password_hash")
    val password_hash: String,

    @ColumnInfo(name = "nombre_completo")
    val nombre_completo: String,

    @ColumnInfo(name = "rol_id")
    val rol_id: Int,

    // 1 = activo, 0 = desactivado
    @ColumnInfo(name = "activo")
    val activo: Int = 1,

    @ColumnInfo(name = "fecha_creacion")
    val fecha_creacion: String? = null
)