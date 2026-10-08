package com.example.gestionalmacenpda.domain.usecase

import com.example.gestionalmacenpda.domain.model.Usuario
import com.example.gestionalmacenpda.domain.repository.UsuarioRepository
import com.example.gestionalmacenpda.util.HashUtils
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: UsuarioRepository
) {
    suspend operator fun invoke(username: String, password: String): Usuario? {
        if (username.isBlank() || password.isBlank()) return null

        // Hasheamos la contraseña antes de comparar con la base de datos.
        val passwordHash = HashUtils.sha256(password)
        return repository.login(username, passwordHash)
    }
}