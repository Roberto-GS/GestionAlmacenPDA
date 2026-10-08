package com.example.gestionalmacenpda.ui.movimientos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestionalmacenpda.domain.model.MovimientoConDetalles
import com.example.gestionalmacenpda.domain.usecase.HistorialMovimientosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistorialMovimientosViewModel @Inject constructor(
    private val historialUseCase: HistorialMovimientosUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HistorialUiState>(HistorialUiState.Cargando)
    val uiState: StateFlow<HistorialUiState> = _uiState.asStateFlow()

    init {
        cargarHistorial()
    }

    private fun cargarHistorial() {
        viewModelScope.launch {
            historialUseCase()
                .catch { e ->
                    _uiState.value = HistorialUiState.Error(e.message ?: "Error al cargar historial")
                }
                .collect { lista ->
                    _uiState.value = if (lista.isEmpty()) {
                        HistorialUiState.Vacio
                    } else {
                        HistorialUiState.Exito(lista)
                    }
                }
        }
    }
}

sealed class HistorialUiState {
    object Cargando : HistorialUiState()
    object Vacio : HistorialUiState()
    data class Exito(val movimientos: List<MovimientoConDetalles>) : HistorialUiState()
    data class Error(val mensaje: String) : HistorialUiState()
}