package com.example.gestionalmacenpda.domain.usecase

import com.example.gestionalmacenpda.domain.model.MovimientoConDetalles
import com.example.gestionalmacenpda.domain.repository.MovimientoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject



class HistorialMovimientosUseCase @Inject constructor(
    private val repository: MovimientoRepository
) {
    // Obtenemos todos los movimientos registrados. Incluye los objetos Producto y Usuario relacionados
    operator fun invoke(): Flow<List<MovimientoConDetalles>> {
        return repository.obtenerHistorialGeneral()
    }
}