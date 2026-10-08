package com.example.gestionalmacenpda.data.local.dao

import androidx.room.*
import com.example.gestionalmacenpda.domain.model.Movimiento
import com.example.gestionalmacenpda.domain.model.MovimientoConDetalles
import kotlinx.coroutines.flow.Flow

@Dao
interface MovimientoDao {
    // Añadimos un movimiento
    @Insert
    suspend fun insertarMovimiento(movimiento: Movimiento)

    // Obtenemos los movimientos relacionados con un producto
    @Query("SELECT * FROM movimientos WHERE producto_id = :productoId ORDER BY fecha_movimiento DESC")
    fun obtenerMovimientosPorProducto(productoId: Int): Flow<List<Movimiento>>

    // Obtenemos todos los movimientos
    @Transaction
    @Query("SELECT * FROM movimientos ORDER BY fecha_movimiento DESC")
    fun obtenerHistorialConDetalles(): Flow<List<MovimientoConDetalles>>
}