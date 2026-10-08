package com.example.gestionalmacenpda.domain.repository

import com.example.gestionalmacenpda.domain.model.Ubicaciones
import kotlinx.coroutines.flow.Flow

interface UbicacionRepository {
    fun obtenerTodas(): Flow<List<Ubicaciones>>
    suspend fun obtenerPorId(id: Int): Ubicaciones?
    suspend fun insertar(ubicacion: Ubicaciones)
    suspend fun eliminar(ubicacion: Ubicaciones)
}