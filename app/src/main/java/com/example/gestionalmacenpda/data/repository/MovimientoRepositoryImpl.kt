package com.example.gestionalmacenpda.data.repository

import com.example.gestionalmacenpda.data.local.dao.MovimientoDao
import com.example.gestionalmacenpda.data.local.dao.ProductoDao
import com.example.gestionalmacenpda.domain.model.Movimiento
import com.example.gestionalmacenpda.domain.model.MovimientoConDetalles
import com.example.gestionalmacenpda.domain.repository.MovimientoRepository
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class MovimientoRepositoryImpl @Inject constructor(
    private val movimientoDao: MovimientoDao,
    private val productoDao:   ProductoDao
) : MovimientoRepository {

    private fun ahora(): String =
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

    override suspend fun registrarMovimientoCompleto(movimiento: Movimiento, nuevoStock: Double) {
        // 1. Guardamos el movimiento en el historial
        movimientoDao.insertarMovimiento(movimiento)
        // 2. Actualizamos el stock, y generamos la fecha de modificación
        productoDao.actualizarStock(movimiento.productoId, nuevoStock, ahora())
    }

    override fun obtenerHistorialPorProducto(productoId: Int): Flow<List<Movimiento>> =
        movimientoDao.obtenerMovimientosPorProducto(productoId)

    override fun obtenerHistorialGeneral(): Flow<List<MovimientoConDetalles>> =
        movimientoDao.obtenerHistorialConDetalles()
}