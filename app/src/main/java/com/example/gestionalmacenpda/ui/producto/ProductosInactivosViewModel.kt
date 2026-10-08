package com.example.gestionalmacenpda.ui.producto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestionalmacenpda.domain.model.Producto
import com.example.gestionalmacenpda.domain.usecase.ProductoUseCase.ObtenerInactivosUseCase
import com.example.gestionalmacenpda.domain.usecase.ProductoUseCase.ReactivarProductoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductosInactivosViewModel @Inject constructor(
    private val obtenerInactivosUseCase: ObtenerInactivosUseCase,
    private val reactivarUseCase: ReactivarProductoUseCase
) : ViewModel() {

    val productos: StateFlow<List<Producto>> = obtenerInactivosUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError.asStateFlow()

    private val _productoReactivado = MutableStateFlow(false)
    val productoReactivado: StateFlow<Boolean> = _productoReactivado.asStateFlow()

    fun reactivar(producto: Producto) {
        viewModelScope.launch {
            try {
                reactivarUseCase(producto.id)
                _productoReactivado.value = true
            } catch (e: Exception) {
                _mensajeError.value = "Error al reactivar: ${e.message}"
            }
        }
    }

    fun limpiarReactivado() { _productoReactivado.value = false }
    fun limpiarError() { _mensajeError.value = null }
}