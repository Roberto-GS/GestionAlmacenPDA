package com.example.gestionalmacenpda.domain.repository

import com.example.gestionalmacenpda.domain.model.Rol
import kotlinx.coroutines.flow.Flow

interface RolRepository {
    fun obtenerTodos(): Flow<List<Rol>>
}