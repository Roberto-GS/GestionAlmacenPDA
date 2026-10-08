package com.example.gestionalmacenpda.ui.ubicacion

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestionalmacenpda.domain.model.Ubicaciones
import com.example.gestionalmacenpda.domain.repository.UbicacionRepository
import com.example.gestionalmacenpda.domain.repository.ProductoRepository
import com.example.gestionalmacenpda.domain.model.Producto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UbicacionViewModel @Inject constructor(
    private val ubicacionRepository: UbicacionRepository,
    private val productoRepository: ProductoRepository
) : ViewModel() {

    val ubicaciones: StateFlow<List<Ubicaciones>> = ubicacionRepository.obtenerTodas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var codigoNuevo by mutableStateOf("")
    var descripcionNueva by mutableStateOf("")
    var mensajeError by mutableStateOf<String?>(null)

    private val _productosUbicacion = MutableStateFlow<List<Producto>>(emptyList())
    val productosUbicacion: StateFlow<List<Producto>> = _productosUbicacion.asStateFlow()

    fun crearUbicacion() {
        if (codigoNuevo.isBlank()) { mensajeError = "El código es obligatorio"; return }
        viewModelScope.launch {
            try {
                ubicacionRepository.insertar(
                    Ubicaciones(codigo = codigoNuevo.trim().uppercase(), descripcion = descripcionNueva.ifBlank { null })
                )
                codigoNuevo      = ""
                descripcionNueva = ""
                mensajeError     = null
            } catch (e: Exception) {
                mensajeError = if (e.message?.contains("UNIQUE") == true)
                    "Ya existe una ubicación con ese código" else e.message
            }
        }
    }

    fun eliminarUbicacion(ubicacion: Ubicaciones) {
        viewModelScope.launch {
            try {
                ubicacionRepository.eliminar(ubicacion)
            } catch (e: Exception) {
                mensajeError = "No se puede eliminar: tiene productos asociados"
            }
        }
    }

    fun cargarProductosDe(ubicacionId: Int) {
        viewModelScope.launch {
            productoRepository.obtenerTodosLosProductos()
                .collect { lista ->
                    _productosUbicacion.value = lista.filter { it.ubicacionID == ubicacionId }
                }
        }
    }

    fun limpiarMensaje() { mensajeError = null }
}