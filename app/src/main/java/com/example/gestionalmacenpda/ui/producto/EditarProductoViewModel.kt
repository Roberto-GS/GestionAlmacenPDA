package com.example.gestionalmacenpda.ui.producto

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestionalmacenpda.domain.model.Categoria
import com.example.gestionalmacenpda.domain.model.Producto
import com.example.gestionalmacenpda.domain.model.Ubicaciones
import com.example.gestionalmacenpda.domain.repository.CategoriaRepository
import com.example.gestionalmacenpda.domain.repository.ProductoRepository
import com.example.gestionalmacenpda.domain.repository.UbicacionRepository
import com.example.gestionalmacenpda.domain.usecase.ProductoUseCase.ObtenerPorIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class EditarProductoState {
    object Cargando: EditarProductoState()
    object Listo: EditarProductoState()
    object Guardando: EditarProductoState()
    object Guardado: EditarProductoState()
    data class Error(val mensaje: String): EditarProductoState()
}

@HiltViewModel
class EditarProductoViewModel @Inject constructor(
    private val obtenerPorIdUseCase: ObtenerPorIdUseCase,
    private val productoRepository: ProductoRepository,
    private val categoriaRepository: CategoriaRepository,
    private val ubicacionRepository: UbicacionRepository
) : ViewModel() {

    private var productoOriginal: Producto? = null

    var nombre by mutableStateOf("")
    var descripcion by mutableStateOf("")
    var unidadMedida by mutableStateOf("")
    var stockMinimo by mutableStateOf("")
    var codigoBarras by mutableStateOf("")
    var observaciones by mutableStateOf("")

    val categorias: StateFlow<List<Categoria>> = categoriaRepository.obtenerTodas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val ubicaciones: StateFlow<List<Ubicaciones>> = ubicacionRepository.obtenerTodas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var categoriaSeleccionada by mutableStateOf<Categoria?>(null)
    var ubicacionSeleccionada by mutableStateOf<Ubicaciones?>(null)

    private val _estado = MutableStateFlow<EditarProductoState>(EditarProductoState.Cargando)
    val estado: StateFlow<EditarProductoState> = _estado

    fun cargarProducto(id: Int) {
        viewModelScope.launch {
            obtenerPorIdUseCase(id).filterNotNull().first().let { p ->
                productoOriginal = p
                nombre = p.nombre
                descripcion = p.descripcion   ?: ""
                unidadMedida = p.unidadMedida
                stockMinimo = p.stockMinimo.toString()
                codigoBarras = p.codigoBarras  ?: ""
                observaciones = p.observaciones ?: ""
                // Preseleccionamos la categoría y ubicación actuales
                p.categoriaID?.let { cid -> categoriaSeleccionada = categoriaRepository.obtenerPorId(cid) }
                p.ubicacionID?.let { uid -> ubicacionSeleccionada = ubicacionRepository.obtenerPorId(uid) }
                _estado.value = EditarProductoState.Listo
            }
        }
    }

    fun guardar() {
        val original = productoOriginal ?: return
        val minimo = stockMinimo.toDoubleOrNull() ?: run {
            _estado.value = EditarProductoState.Error("Stock mínimo inválido"); return
        }
        _estado.value = EditarProductoState.Guardando
        viewModelScope.launch {
            try {
                productoRepository.actualizarProducto(original.copy(
                    nombre = nombre.trim(),
                    descripcion = descripcion.ifBlank { null },
                    unidadMedida = unidadMedida.trim().uppercase(),
                    stockMinimo = minimo,
                    codigoBarras = codigoBarras.ifBlank { null },
                    observaciones = observaciones.ifBlank { null },
                    categoriaID = categoriaSeleccionada?.id,
                    ubicacionID = ubicacionSeleccionada?.id
                ))
                _estado.value = EditarProductoState.Guardado
            } catch (e: Exception) {
                _estado.value = EditarProductoState.Error(e.message ?: "Error al guardar")
            }
        }
    }

    fun desactivar() {
        viewModelScope.launch {
            productoOriginal?.let {
                productoRepository.desactivarProducto(it.id)
                _estado.value = EditarProductoState.Guardado
            }
        }
    }
}