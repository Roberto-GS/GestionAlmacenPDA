package com.example.gestionalmacenpda.ui.usuario

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestionalmacenpda.domain.model.Rol
import com.example.gestionalmacenpda.domain.model.Usuario
import com.example.gestionalmacenpda.domain.repository.RolRepository
import com.example.gestionalmacenpda.domain.repository.UsuarioRepository
import com.example.gestionalmacenpda.util.HashUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GestionUsuariosViewModel @Inject constructor(
    private val usuarioRepository: UsuarioRepository,
    private val rolRepository: RolRepository
) : ViewModel() {

    // Lista de usuarios
    val usuarios: StateFlow<List<Usuario>> = usuarioRepository.obtenerTodos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Lista de roles para el picker
    val roles: StateFlow<List<Rol>> = rolRepository.obtenerTodos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Estado del formulario para un nuevo usuario
    var nuevoUsername by mutableStateOf("")
    var nuevoPassword by mutableStateOf("")
    var nuevoNombre by mutableStateOf("")
    var rolSeleccionado by mutableStateOf<Rol?>(null)

    // Mensajes
    private val _mensajeError  = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError.asStateFlow()

    private val _usuarioCreado = MutableStateFlow(false)
    val usuarioCreado: StateFlow<Boolean> = _usuarioCreado.asStateFlow()

    // Activar / desactivar
    fun activar(usuario: Usuario) {
        viewModelScope.launch {
            try {
                usuarioRepository.cambiarEstado(usuario.id, 1)
            } catch (e: Exception) {
                _mensajeError.value = "Error al activar: ${e.message}"
            }
        }
    }

    fun desactivar(usuario: Usuario, usuarioActualId: Int) {
        if (usuario.id == usuarioActualId) {
            _mensajeError.value = "No puedes desactivar tu propia cuenta"
            return
        }
        viewModelScope.launch {
            try {
                usuarioRepository.cambiarEstado(usuario.id, 0)
            } catch (e: Exception) {
                _mensajeError.value = "Error al desactivar: ${e.message}"
            }
        }
    }

    // Crear nuevo usuario
    fun crearUsuario() {
        // Validaciones
        if (nuevoUsername.isBlank()) {
            _mensajeError.value = "El nombre de usuario es obligatorio"
            return
        }
        if (nuevoPassword.isBlank()) {
            _mensajeError.value = "La contraseña es obligatoria"
            return
        }
        if (nuevoPassword.length < 4) {
            _mensajeError.value = "La contraseña debe tener al menos 4 caracteres"
            return
        }
        if (nuevoNombre.isBlank()) {
            _mensajeError.value = "El nombre completo es obligatorio"
            return
        }
        val rol = rolSeleccionado ?: run {
            _mensajeError.value = "Selecciona un rol"
            return
        }

        viewModelScope.launch {
            try {
                val nuevoUsuario = Usuario(
                    username = nuevoUsername.trim().lowercase(),
                    password_hash = HashUtils.sha256(nuevoPassword),
                    nombre_completo = nuevoNombre.trim(),
                    rol_id = rol.id,
                    activo = 1   // siempre activo al crearlo
                )
                usuarioRepository.insertar(nuevoUsuario)
                // Limpiamos el formulario
                nuevoUsername = ""
                nuevoPassword = ""
                nuevoNombre = ""
                rolSeleccionado = null
                _mensajeError.value = null
                _usuarioCreado.value = true
            } catch (e: Exception) {
                _mensajeError.value = if (e.message?.contains("UNIQUE") == true)
                    "Ya existe un usuario con ese nombre de usuario"
                else
                    "Error al crear el usuario: ${e.message}"
            }
        }
    }

    fun limpiarError() { _mensajeError.value  = null  }
    fun limpiarUsuarioCreado() { _usuarioCreado.value = false }
}