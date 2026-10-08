package com.example.gestionalmacenpda.domain.repository

import com.example.gestionalmacenpda.domain.model.Movimiento
import com.example.gestionalmacenpda.domain.model.MovimientoConDetalles
import kotlinx.coroutines.flow.Flow

interface MovimientoRepository {
    // Registramos el movimiento y actualizamos el stock del producto en una sola operación
    suspend fun registrarMovimientoCompleto(movimiento: Movimiento, nuevoStock: Double)

    fun obtenerHistorialPorProducto(productoId: Int): Flow<List<Movimiento>>

    fun obtenerHistorialGeneral(): Flow<List<MovimientoConDetalles>>
}