package com.example.gestionalmacenpda.data.repository

import com.example.gestionalmacenpda.data.local.dao.RolDao
import com.example.gestionalmacenpda.domain.model.Rol
import com.example.gestionalmacenpda.domain.repository.RolRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RolRepositoryImpl @Inject constructor(private val rolDao: RolDao) : RolRepository {
    override fun obtenerTodos(): Flow<List<Rol>> = rolDao.obtenerTodos()
}