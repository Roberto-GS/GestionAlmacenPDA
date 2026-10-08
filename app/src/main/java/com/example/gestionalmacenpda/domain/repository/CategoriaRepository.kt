package com.example.gestionalmacenpda.domain.repository

import com.example.gestionalmacenpda.domain.model.Categoria
import kotlinx.coroutines.flow.Flow

interface CategoriaRepository {
    fun obtenerTodas(): Flow<List<Categoria>>
    suspend fun obtenerPorId(id: Int): Categoria?
    suspend fun insertar(categoria: Categoria)
    suspend fun eliminar(categoria: Categoria)
}