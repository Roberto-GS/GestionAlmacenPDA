package com.example.gestionalmacenpda.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.ColumnInfo
import androidx.room.Index

@Entity(
    tableName = "productos",
    foreignKeys = [
        ForeignKey(
            entity = Categoria::class,
            parentColumns = ["id"],
            childColumns = ["categoria_id"],
            onDelete = ForeignKey.SET_NULL // Si borramos la categoría, el producto se queda sin categoría
        ),
        ForeignKey(
            entity = Ubicaciones::class,
            parentColumns = ["id"],
            childColumns = ["ubicacion_id"],
            onDelete = ForeignKey.SET_NULL // Si borramos la ubicación, el producto se queda sin ubicación
        )
    ],
    indices = [Index("categoria_id"), Index("ubicacion_id")]
)
data class Producto (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "codigo")
    val codigo: String,
    @ColumnInfo(name = "nombre")
    val nombre: String,
    @ColumnInfo(name = "descripcion")
    val descripcion: String?,
    @ColumnInfo(name = "categoria_id")
    val categoriaID: Int?,
    @ColumnInfo(name = "ubicacion_id")
    val ubicacionID: Int?,
    @ColumnInfo(name = "unidad_medida")
    val unidadMedida: String,
    @ColumnInfo(name = "stock_actual")
    val stockActual: Double,
    @ColumnInfo(name = "stock_minimo")
    val stockMinimo: Double,
    @ColumnInfo(name = "activo")
    val activo: Int = 1,
    @ColumnInfo(name = "codigo_barras")
    val codigoBarras: String?,
    @ColumnInfo(name = "observaciones")
    val observaciones: String?,
    @ColumnInfo(name = "fecha_creacion")
    val fechaCreacion: String? = null,
    @ColumnInfo(name = "fecha_modificacion")
    val fechaModificacion: String? = null
)