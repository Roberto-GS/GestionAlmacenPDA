package com.example.gestionalmacenpda.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.ColumnInfo
import androidx.room.Index

enum class TipoMovimientoEnum(val id: Int, val codigo: String, val esPositivo: Boolean) {
    ENTRADA(1, "ENTRADA", true),
    SALIDA(2, "SALIDA", false),
    AJUSTE_POS(3, "AJUSTE_POS", true),
    AJUSTE_NEG(4, "AJUSTE_NEG", false);

    companion object {
        fun desdeId(id: Int) = values().find { it.id == id } ?: ENTRADA
    }
}

@Entity(
    tableName = "movimientos",
    foreignKeys = [
        ForeignKey(
            entity = Producto::class,
            parentColumns = ["id"],
            childColumns = ["producto_id"]
        ),
        ForeignKey(
            entity = tipoMovimiento::class,
            parentColumns = ["id"],
            childColumns = ["tipo_movimiento_id"]
        ),
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["usuario_id"]
        )
    ],
    indices = [
        Index("producto_id"),
        Index("tipo_movimiento_id"),
        Index("usuario_id")
    ]
)
data class Movimiento(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "producto_id")
    val productoId: Int,
    @ColumnInfo(name = "tipo_movimiento_id")
    val tipoMovimientoId: Int,
    @ColumnInfo(name = "cantidad")
    val cantidad: Double,
    @ColumnInfo(name = "stock_anterior")
    val stockAnterior: Double,
    @ColumnInfo(name = "stock_resultante")
    val stockResultante: Double,
    @ColumnInfo(name = "observaciones")
    val observaciones: String?,
    @ColumnInfo(name = "referencia")
    val referencia: String?,
    @ColumnInfo(name = "usuario_id")
    val usuarioId: Int,
    @ColumnInfo(name = "fecha_movimiento")
    val fechaMovimiento: Long = System.currentTimeMillis()
)