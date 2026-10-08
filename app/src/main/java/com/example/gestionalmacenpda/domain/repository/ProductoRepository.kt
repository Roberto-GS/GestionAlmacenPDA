package com.example.gestionalmacenpda.domain.repository

import com.example.gestionalmacenpda.domain.model.Producto
import kotlinx.coroutines.flow.Flow

interface ProductoRepository {
    fun obtenerTodosLosProductos(): Flow<List<Producto>>
    fun buscarProducto(criterio: String): Flow<List<Producto>>
    fun obtenerPorId(id: Int): Flow<Producto?>
    fun obtenerConStockBajo(): Flow<List<Producto>>
    fun obtenerInactivos(): Flow<List<Producto>>
    suspend fun buscarPorCodigoBarras(codigoBarras: String): Producto?
    suspend fun insertarProducto(producto: Producto)
    suspend fun actualizarProducto(producto: Producto)
    suspend fun actualizarStock(id: Int, nuevaCantidad: Double)
    suspend fun desactivarProducto(id: Int)
    suspend fun reactivarProducto(id: Int)
}