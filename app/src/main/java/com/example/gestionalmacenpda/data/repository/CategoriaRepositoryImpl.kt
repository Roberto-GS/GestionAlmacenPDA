package com.example.gestionalmacenpda.data.repository

import com.example.gestionalmacenpda.data.local.dao.CategoriaDao
import com.example.gestionalmacenpda.domain.model.Categoria
import com.example.gestionalmacenpda.domain.repository.CategoriaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CategoriaRepositoryImpl @Inject constructor(private val dao: CategoriaDao) : CategoriaRepository {
    override fun obtenerTodas(): Flow<List<Categoria>> = dao.obtenerTodas()
    override suspend fun obtenerPorId(id: Int): Categoria? = dao.obtenerPorId(id)
    override suspend fun insertar(categoria: Categoria) = dao.insertar(categoria)
    override suspend fun eliminar(categoria: Categoria) = dao.eliminar(categoria)
}