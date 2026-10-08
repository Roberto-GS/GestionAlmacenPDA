package com.example.gestionalmacenpda.ui.producto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestionalmacenpda.domain.model.Producto
import com.example.gestionalmacenpda.domain.repository.ProductoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductoFiltroViewModel @Inject constructor(
    private val productoRepository: ProductoRepository
) : ViewModel() {

    private val _listaProductos = MutableStateFlow<List<Producto>>(emptyList())
    val listaProductos: StateFlow<List<Producto>> = _listaProductos.asStateFlow()

    fun cargar(filtroId: Int, tipoFiltro: String) {
        viewModelScope.launch {
            productoRepository.obtenerTodosLosProductos().collect { lista ->
                _listaProductos.value = when (tipoFiltro) {
                    "CATEGORIA" -> lista.filter { it.categoriaID == filtroId }
                    "UBICACION" -> lista.filter { it.ubicacionID == filtroId }
                    else -> lista
                }
            }
        }
    }
}