package com.example.gestionalmacenpda.domain.usecase

import com.example.gestionalmacenpda.domain.model.Movimiento
import com.example.gestionalmacenpda.domain.model.Producto
import com.example.gestionalmacenpda.domain.model.TipoMovimientoEnum
import com.example.gestionalmacenpda.domain.repository.MovimientoRepository
import javax.inject.Inject

class RegistrarMovimientoUseCase @Inject constructor(
    private val repository: MovimientoRepository
) {
    suspend operator fun invoke(
        producto: Producto,
        cantidad: Double,
        tipo: TipoMovimientoEnum,
        usuarioId: Int,
        referencia: String?,
        observaciones: String?
    ) {
        if (cantidad <= 0) throw Exception("La cantidad debe ser mayor que cero")

        val stockAnterior = producto.stockActual

        val stockFinal = if (tipo.esPositivo) {
            stockAnterior + cantidad
        } else {
            val resultado = stockAnterior - cantidad
            if (resultado < 0 && tipo == TipoMovimientoEnum.SALIDA) {
                throw Exception("Stock insuficiente. Disponible: $stockAnterior ${producto.unidadMedida}")
            }
            resultado
        }

        val movimiento = Movimiento(
            productoId = producto.id,
            tipoMovimientoId = tipo.id,
            cantidad = cantidad,
            stockAnterior = stockAnterior,
            stockResultante = stockFinal,
            observaciones = observaciones,
            referencia = referencia,
            usuarioId = usuarioId,
            fechaMovimiento = System.currentTimeMillis()
        )

        repository.registrarMovimientoCompleto(movimiento, stockFinal)
    }
}