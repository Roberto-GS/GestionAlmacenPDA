package com.example.gestionalmacenpda.ui.movimientos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestionalmacenpda.domain.model.Movimiento
import com.example.gestionalmacenpda.domain.repository.MovimientoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class HistorialProductoState {
    object Cargando : HistorialProductoState()
    object Vacio : HistorialProductoState()
    data class Exito(val movimientos: List<Movimiento>) : HistorialProductoState()
    data class Error(val mensaje: String) : HistorialProductoState()
}

@HiltViewModel
class HistorialProductoViewModel @Inject constructor(
    private val movimientoRepository: MovimientoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HistorialProductoState>(HistorialProductoState.Cargando)
    val uiState: StateFlow<HistorialProductoState> = _uiState.asStateFlow()

    fun cargar(productoId: Int) {
        viewModelScope.launch {
            movimientoRepository.obtenerHistorialPorProducto(productoId)
                .catch { e ->
                    _uiState.value = HistorialProductoState.Error(e.message ?: "Error")
                }
                .collect { lista ->
                    _uiState.value = if (lista.isEmpty()) {
                        HistorialProductoState.Vacio
                    } else {
                        HistorialProductoState.Exito(lista)
                    }
                }
        }
    }
}