package com.example.gestionalmacenpda.domain.model

import androidx.room.Embedded
import androidx.room.Relation



data class MovimientoConDetalles(
    @Embedded val movimiento: Movimiento,
    @Relation(
        parentColumn = "producto_id",
        entityColumn = "id"
    )
    val producto: Producto,
    @Relation(
        parentColumn = "usuario_id",
        entityColumn = "id"
    )
    val usuario: Usuario
)