package com.example.gestionalmacenpda.ui.producto

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestionalmacenpda.domain.model.Categoria
import com.example.gestionalmacenpda.domain.model.Producto
import com.example.gestionalmacenpda.domain.model.Ubicaciones
import com.example.gestionalmacenpda.domain.repository.CategoriaRepository
import com.example.gestionalmacenpda.domain.repository.UbicacionRepository
import com.example.gestionalmacenpda.domain.usecase.ProductoUseCase.InsertarProductoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

sealed class AltaProductoState {
    object Idle : AltaProductoState()
    object Guardando : AltaProductoState()
    object Guardado : AltaProductoState()
    data class Error(val mensaje: String) : AltaProductoState()
}

@HiltViewModel
class AltaProductoViewModel @Inject constructor(
    private val insertarProductoUseCase: InsertarProductoUseCase,
    private val categoriaRepository: CategoriaRepository,
    private val ubicacionRepository: UbicacionRepository
) : ViewModel() {

    var codigo by mutableStateOf("")
    var nombre by mutableStateOf("")
    var descripcion by mutableStateOf("")
    var codigoBarras by mutableStateOf("")
    var unidadMedida by mutableStateOf("UND")
    var stockInicial by mutableStateOf("0")
    var stockMinimo by mutableStateOf("0")
    var observaciones by mutableStateOf("")

    val categorias: StateFlow<List<Categoria>> = categoriaRepository.obtenerTodas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val ubicaciones: StateFlow<List<Ubicaciones>> = ubicacionRepository.obtenerTodas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var categoriaSeleccionada by mutableStateOf<Categoria?>(null)
    var ubicacionSeleccionada by mutableStateOf<Ubicaciones?>(null)

    private val _estado = MutableStateFlow<AltaProductoState>(AltaProductoState.Idle)
    val estado: StateFlow<AltaProductoState> = _estado

    fun guardar() {
        val minimo = stockMinimo.toDoubleOrNull()  ?: run { _estado.value = AltaProductoState.Error("Stock mínimo inválido"); return }
        val inicial = stockInicial.toDoubleOrNull() ?: run { _estado.value = AltaProductoState.Error("Stock inicial inválido"); return }

        _estado.value = AltaProductoState.Guardando
        viewModelScope.launch {
            try {
                val ahora = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

                insertarProductoUseCase(Producto(
                    id = 0,
                    codigo = codigo.trim(),
                    nombre = nombre.trim(),
                    descripcion = descripcion.ifBlank { null },
                    categoriaID = categoriaSeleccionada?.id,
                    ubicacionID = ubicacionSeleccionada?.id,
                    unidadMedida = unidadMedida.trim().uppercase(),
                    stockActual = inicial,
                    stockMinimo = minimo,
                    activo = 1,
                    codigoBarras = codigoBarras.ifBlank { null },
                    observaciones = observaciones.ifBlank { null },
                    fechaCreacion = ahora,
                    fechaModificacion = null
                ))
                _estado.value = AltaProductoState.Guardado
            } catch (e: Exception) {
                _estado.value = AltaProductoState.Error(
                    if (e.message?.contains("UNIQUE") == true) "Ya existe un producto con ese código"
                    else e.message ?: "Error al guardar"
                )
            }
        }
    }
}