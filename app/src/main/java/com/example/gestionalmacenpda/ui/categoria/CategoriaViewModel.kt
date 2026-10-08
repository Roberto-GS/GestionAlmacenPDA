package com.example.gestionalmacenpda.ui.categoria

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestionalmacenpda.domain.model.Categoria
import com.example.gestionalmacenpda.domain.repository.CategoriaRepository
import com.example.gestionalmacenpda.domain.repository.ProductoRepository
import com.example.gestionalmacenpda.domain.model.Producto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriaViewModel @Inject constructor(
    private val categoriaRepository: CategoriaRepository,
    private val productoRepository: ProductoRepository
) : ViewModel() {

    val categorias: StateFlow<List<Categoria>> = categoriaRepository.obtenerTodas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var nombreNueva by mutableStateOf("")
    var descripcionNueva by mutableStateOf("")
    var mensajeError by mutableStateOf<String?>(null)

    // Productos de una categoría seleccionada
    private val _productosCategoria = MutableStateFlow<List<Producto>>(emptyList())
    val productosCategoria: StateFlow<List<Producto>> = _productosCategoria.asStateFlow()

    fun crearCategoria() {
        if (nombreNueva.isBlank()) { mensajeError = "El nombre es obligatorio"; return }
        viewModelScope.launch {
            try {
                categoriaRepository.insertar(
                    Categoria(nombre = nombreNueva.trim(), descripcion = descripcionNueva.ifBlank { null })
                )
                nombreNueva    = ""
                descripcionNueva = ""
                mensajeError   = null
            } catch (e: Exception) {
                mensajeError = if (e.message?.contains("UNIQUE") == true)
                    "Ya existe una categoría con ese nombre" else e.message
            }
        }
    }

    fun eliminarCategoria(categoria: Categoria) {
        viewModelScope.launch {
            try {
                categoriaRepository.eliminar(categoria)
            } catch (e: Exception) {
                mensajeError = "No se puede eliminar: tiene productos asociados"
            }
        }
    }

    fun cargarProductosDe(categoriaId: Int) {
        viewModelScope.launch {
            productoRepository.obtenerTodosLosProductos()
                .collect { lista ->
                    _productosCategoria.value = lista.filter { it.categoriaID == categoriaId }
                }
        }
    }

    fun limpiarMensaje() { mensajeError = null }
}