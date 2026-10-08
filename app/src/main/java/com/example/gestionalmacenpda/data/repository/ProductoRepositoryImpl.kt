package com.example.gestionalmacenpda.data.repository

import com.example.gestionalmacenpda.data.local.dao.ProductoDao
import com.example.gestionalmacenpda.domain.model.Producto
import com.example.gestionalmacenpda.domain.repository.ProductoRepository
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class ProductoRepositoryImpl @Inject constructor(
    private val productoDao: ProductoDao
) : ProductoRepository {

    // Formato de fecha consistente en todo el repositorio
    private fun ahora(): String =
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

    override fun obtenerTodosLosProductos(): Flow<List<Producto>> =
        productoDao.obtenerTodos()

    override fun buscarProducto(criterio: String): Flow<List<Producto>> =
        productoDao.buscarPorCriterio("%$criterio%")

    override fun obtenerPorId(id: Int): Flow<Producto?> =
        productoDao.obtenerPorId(id)

    override fun obtenerConStockBajo(): Flow<List<Producto>> =
        productoDao.obtenerConStockBajo()

    override fun obtenerInactivos(): Flow<List<Producto>> =
        productoDao.obtenerInactivos()

    override suspend fun buscarPorCodigoBarras(codigoBarras: String): Producto? =
        productoDao.buscarPorCodigoBarras(codigoBarras)

    override suspend fun insertarProducto(producto: Producto): Unit =
        productoDao.insertar(producto)

    override suspend fun actualizarProducto(producto: Producto): Unit =
        productoDao.actualizar(producto)

    override suspend fun actualizarStock(id: Int, nuevaCantidad: Double) =
        productoDao.actualizarStock(id, nuevaCantidad, ahora())

    override suspend fun desactivarProducto(id: Int) =
        productoDao.desactivar(id, ahora())

    override suspend fun reactivarProducto(id: Int) =
        productoDao.reactivar(id, ahora())
}