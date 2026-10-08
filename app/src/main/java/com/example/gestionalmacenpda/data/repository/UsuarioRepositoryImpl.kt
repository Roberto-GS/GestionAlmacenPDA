package com.example.gestionalmacenpda.data.repository

import com.example.gestionalmacenpda.data.local.dao.UsuarioDao
import com.example.gestionalmacenpda.domain.model.Usuario
import com.example.gestionalmacenpda.domain.repository.UsuarioRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UsuarioRepositoryImpl @Inject constructor(private val usuarioDao: UsuarioDao) : UsuarioRepository {
    override suspend fun login(username: String, password: String): Usuario? = usuarioDao.login(username, password)
    override fun obtenerTodos(): Flow<List<Usuario>> = usuarioDao.obtenerTodos()
    override suspend fun cambiarEstado(id: Int, activo: Int) = usuarioDao.cambiarEstado(id, activo)
    override suspend fun insertar(usuario: Usuario) = usuarioDao.insertar(usuario)
}