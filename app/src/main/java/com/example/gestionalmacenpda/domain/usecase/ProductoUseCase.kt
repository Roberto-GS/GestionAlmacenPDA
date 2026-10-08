package com.example.gestionalmacenpda.domain.usecase

import com.example.gestionalmacenpda.domain.model.Producto
import com.example.gestionalmacenpda.domain.repository.ProductoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProductoUseCase {

    class ObtenerProductosUseCase @Inject constructor(private val repo: ProductoRepository) {
        operator fun invoke() = repo.obtenerTodosLosProductos()
    }

    class BuscarProductosUseCase @Inject constructor(private val repo: ProductoRepository) {
        operator fun invoke(criterio: String) = repo.buscarProducto(criterio)
    }

    class ActualizarStockUseCase @Inject constructor(private val repo: ProductoRepository) {
        suspend operator fun invoke(id: Int, cantidad: Double) {
            if (cantidad < 0) return
            repo.actualizarStock(id, cantidad)
        }
    }

    class BuscarPorCodigoUseCase @Inject constructor(private val repo: ProductoRepository) {
        suspend operator fun invoke(codigo: String): Producto? =
            repo.buscarPorCodigoBarras(codigo)
    }

    class ObtenerPorIdUseCase @Inject constructor(private val repo: ProductoRepository) {
        operator fun invoke(id: Int): Flow<Producto?> = repo.obtenerPorId(id)
    }

    class ObtenerStockBajoUseCase @Inject constructor(private val repo: ProductoRepository) {
        operator fun invoke() = repo.obtenerConStockBajo()
    }

    class InsertarProductoUseCase @Inject constructor(private val repo: ProductoRepository) {
        suspend operator fun invoke(producto: Producto) = repo.insertarProducto(producto)
    }

    class ObtenerInactivosUseCase @Inject constructor(private val repo: ProductoRepository) {
        operator fun invoke(): Flow<List<Producto>> = repo.obtenerInactivos()
    }

    class ReactivarProductoUseCase @Inject constructor(private val repo: ProductoRepository) {
        suspend operator fun invoke(id: Int) = repo.reactivarProducto(id)
    }
}