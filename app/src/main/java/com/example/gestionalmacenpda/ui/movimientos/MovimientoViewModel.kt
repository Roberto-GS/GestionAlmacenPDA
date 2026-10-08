package com.example.gestionalmacenpda.ui.movimientos

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestionalmacenpda.domain.model.Producto
import com.example.gestionalmacenpda.domain.model.TipoMovimientoEnum
import com.example.gestionalmacenpda.domain.usecase.ProductoUseCase.ObtenerPorIdUseCase
import com.example.gestionalmacenpda.domain.usecase.RegistrarMovimientoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovimientoViewModel @Inject constructor(
    private val registrarUseCase: RegistrarMovimientoUseCase,
    private val obtenerPorIdUseCase: ObtenerPorIdUseCase
) : ViewModel() {

    // Producto cargado desde BD por su ID
    private val _producto = MutableStateFlow<Producto?>(null)
    val producto: StateFlow<Producto?> = _producto.asStateFlow()

    // Campos del formulario
    var cantidadInput by mutableStateOf("")
    var referenciaInput by mutableStateOf("")
    var observacionesInput by mutableStateOf("")

    // null = sin resultado
    // true = éxito
    // false = error
    private val _estadoGuardado = MutableStateFlow<Boolean?>(null)
    val estadoGuardado = _estadoGuardado.asStateFlow()

    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError = _mensajeError.asStateFlow()

    fun cargarProducto(id: Int) {
        viewModelScope.launch {
            obtenerPorIdUseCase(id).collect { p ->
                _producto.value = p
            }
        }
    }

    fun registrar(tipo: TipoMovimientoEnum, usuarioId: Int) {
        val prod = _producto.value ?: return
        val cant = cantidadInput.toDoubleOrNull() ?: run {
            _mensajeError.value = "Introduce una cantidad válida"
            return
        }

        viewModelScope.launch {
            try {
                registrarUseCase(
                    producto = prod,
                    cantidad = cant,
                    tipo = tipo,
                    usuarioId = usuarioId,
                    referencia = referenciaInput.ifBlank { null },
                    observaciones = observacionesInput.ifBlank { null }
                )
                _estadoGuardado.value = true
            } catch (e: Exception) {
                _mensajeError.value = e.message ?: "Error al registrar el movimiento"
                _estadoGuardado.value = false
            }
        }
    }

    fun limpiarMensajeError() {
        _mensajeError.value = null
    }

    fun resetEstado() {
        _estadoGuardado.value = null
        cantidadInput = ""
        referenciaInput = ""
        observacionesInput = ""
    }
}