package com.example.gestionalmacenpda.ui.sesion

import androidx.lifecycle.ViewModel
import com.example.gestionalmacenpda.domain.model.Usuario
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SesionViewModel @Inject constructor() : ViewModel() {

    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario.asStateFlow()

    fun iniciarSesion(usuario: Usuario) {
        _usuario.value = usuario
    }

    fun cerrarSesion() {
        _usuario.value = null
    }
}