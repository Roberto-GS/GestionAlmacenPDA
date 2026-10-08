package com.example.gestionalmacenpda.data.repository

import com.example.gestionalmacenpda.data.local.dao.UbicacionDao
import com.example.gestionalmacenpda.domain.model.Ubicaciones
import com.example.gestionalmacenpda.domain.repository.UbicacionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UbicacionRepositoryImpl @Inject constructor(private val dao: UbicacionDao) : UbicacionRepository {
    override fun obtenerTodas(): Flow<List<Ubicaciones>> = dao.obtenerTodas()
    override suspend fun obtenerPorId(id: Int): Ubicaciones? = dao.obtenerPorId(id)
    override suspend fun insertar(ubicacion: Ubicaciones) = dao.insertar(ubicacion)
    override suspend fun eliminar(ubicacion: Ubicaciones) = dao.eliminar(ubicacion)
}