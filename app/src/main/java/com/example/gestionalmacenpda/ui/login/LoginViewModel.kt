package com.example.gestionalmacenpda.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestionalmacenpda.domain.model.Usuario
import com.example.gestionalmacenpda.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor (
    private val loginUseCase: LoginUseCase
): ViewModel() {

    private val _estadoPantalla = MutableStateFlow<EstadoLogin>(EstadoLogin.Inactivo)
    val estadoPantalla = _estadoPantalla.asStateFlow()

    fun realizarLogin(user: String, pass: String) {
        viewModelScope.launch {
            _estadoPantalla.value = EstadoLogin.Cargando
            val usuario = loginUseCase(user, pass)

            if (usuario != null) {
                _estadoPantalla.value = EstadoLogin.Success(usuario)
            } else {
                _estadoPantalla.value = EstadoLogin.Error("El usuario o la contraseña son incorrectos")
            }
        }
    }
}

sealed class EstadoLogin {
    object Inactivo : EstadoLogin()
    object Cargando : EstadoLogin()
    data class Success(val usuario: Usuario) : EstadoLogin()
    data class Error(val mensaje: String) : EstadoLogin()
}