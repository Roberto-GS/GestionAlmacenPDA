package com.example.gestionalmacenpda.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.gestionalmacenpda.domain.model.Rol
import kotlinx.coroutines.flow.Flow

@Dao
interface RolDao {
    // Obtenemos todos los roles
    @Query("SELECT * FROM roles ORDER BY id ASC")
    fun obtenerTodos(): Flow<List<Rol>>
}