package com.example.gestionalmacenpda.data.local.dao

import androidx.room.*
import com.example.gestionalmacenpda.domain.model.Ubicaciones
import kotlinx.coroutines.flow.Flow

@Dao
interface UbicacionDao {
    // Obtenemos todas las ubicaciones
    @Query("SELECT * FROM ubicaciones ORDER BY codigo ASC")
    fun obtenerTodas(): Flow<List<Ubicaciones>>

    // Buscamos una ubicación por su id
    @Query("SELECT * FROM ubicaciones WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Int): Ubicaciones?

    // Añadimos una nueva ubicación
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(ubicacion: Ubicaciones)

    // Eliminamos una ubicación
    @Delete
    suspend fun eliminar(ubicacion: Ubicaciones)
}