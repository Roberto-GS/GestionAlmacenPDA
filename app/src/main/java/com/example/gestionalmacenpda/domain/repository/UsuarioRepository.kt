package com.example.gestionalmacenpda.domain.repository

import com.example.gestionalmacenpda.domain.model.Usuario
import kotlinx.coroutines.flow.Flow

interface UsuarioRepository {
    suspend fun login(username: String, password: String): Usuario?
    fun obtenerTodos(): Flow<List<Usuario>>
    suspend fun cambiarEstado(id: Int, activo: Int)
    suspend fun insertar(usuario: Usuario)
}