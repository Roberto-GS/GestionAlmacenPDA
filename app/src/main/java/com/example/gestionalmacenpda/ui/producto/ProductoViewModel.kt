package com.example.gestionalmacenpda.ui.producto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestionalmacenpda.domain.model.Producto
import com.example.gestionalmacenpda.domain.model.Ubicaciones
import com.example.gestionalmacenpda.domain.repository.UbicacionRepository
import com.example.gestionalmacenpda.domain.usecase.ProductoUseCase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductoViewModel @Inject constructor(
    private val obtenerProductosUseCase: ObtenerProductosUseCase,
    private val buscarProductosUseCase: BuscarProductosUseCase,
    private val buscarPorCodigoUseCase: BuscarPorCodigoUseCase,
    private val obtenerPorIdUseCase: ObtenerPorIdUseCase,
    private val obtenerStockBajoUseCase: ObtenerStockBajoUseCase,
    private val ubicacionRepository: UbicacionRepository
) : ViewModel() {

    private val _busqueda = MutableStateFlow("")
    val busqueda = _busqueda.asStateFlow()

    private val ubicacionesFlow: Flow<List<Ubicaciones>> = ubicacionRepository.obtenerTodas()

    // Lista de productos
    val productos: StateFlow<List<ProductoConUbicacion>> = _busqueda
        .flatMapLatest { criterio ->
            val productosFlow = if (criterio.isEmpty()) obtenerProductosUseCase()
            else buscarProductosUseCase(criterio)
            productosFlow.combine(ubicacionesFlow) { listaProductos, listaUbicaciones ->
                val mapaUbicaciones = listaUbicaciones.associateBy { it.id }
                listaProductos.map { prod ->
                    ProductoConUbicacion(
                        producto        = prod,
                        nombreUbicacion = prod.ubicacionID
                            ?.let { mapaUbicaciones[it]?.codigo ?: "Ubic: $it" }
                            ?: "Sin ubicación"
                    )
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val productosStockBajo: StateFlow<List<Producto>> = obtenerStockBajoUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onBusquedaChanged(texto: String) {
        _busqueda.value = texto
        if (_mensajeEscaneo.value != null) _mensajeEscaneo.value = null
    }

    private val _productoEscaneado = MutableStateFlow<Producto?>(null)
    val productoEscaneado = _productoEscaneado.asStateFlow()

    private val _mensajeEscaneo = MutableStateFlow<String?>(null)
    val mensajeEscaneo = _mensajeEscaneo.asStateFlow()

    fun procesarCodigoEscaneado(codigo: String) {
        if (codigo.isBlank()) return
        viewModelScope.launch {
            val producto = buscarPorCodigoUseCase(codigo)
            if (producto != null) {
                _productoEscaneado.value = producto
                _mensajeEscaneo.value    = null
            } else {
                _mensajeEscaneo.value = "Código \"$codigo\" no encontrado. Busca por nombre o código."
            }
        }
    }

    fun limpiarEscaneo() {
        _productoEscaneado.value = null
        _mensajeEscaneo.value = null
    }

    fun obtenerProductoPorId(id: Int): Flow<Producto?> = obtenerPorIdUseCase(id)
}