package com.example.gestionalmacenpda.ui.producto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestionalmacenpda.domain.model.Categoria
import com.example.gestionalmacenpda.domain.model.Producto
import com.example.gestionalmacenpda.domain.model.Ubicaciones
import com.example.gestionalmacenpda.domain.repository.CategoriaRepository
import com.example.gestionalmacenpda.domain.repository.UbicacionRepository
import com.example.gestionalmacenpda.domain.usecase.ProductoUseCase.ObtenerPorIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetalleProductoUiState(
    val producto: Producto? = null,
    val categoria: Categoria? = null,
    val ubicacion: Ubicaciones? = null,
    val cargando: Boolean = true
)

@HiltViewModel
class DetalleProductoViewModel @Inject constructor(
    private val obtenerPorIdUseCase: ObtenerPorIdUseCase,
    private val categoriaRepository: CategoriaRepository,
    private val ubicacionRepository: UbicacionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetalleProductoUiState())
    val uiState: StateFlow<DetalleProductoUiState> = _uiState.asStateFlow()

    fun cargar(productoId: Int) {
        viewModelScope.launch {
            obtenerPorIdUseCase(productoId).collect { prod ->
                if (prod == null) {
                    _uiState.value = DetalleProductoUiState(cargando = false)
                    return@collect
                }
                // Resolvemos el nombre de categoría y ubicación
                val cat  = prod.categoriaID?.let { categoriaRepository.obtenerPorId(it) }
                val ubic = prod.ubicacionID?.let { ubicacionRepository.obtenerPorId(it) }
                _uiState.value = DetalleProductoUiState(
                    producto  = prod,
                    categoria = cat,
                    ubicacion = ubic,
                    cargando  = false
                )
            }
        }
    }
}