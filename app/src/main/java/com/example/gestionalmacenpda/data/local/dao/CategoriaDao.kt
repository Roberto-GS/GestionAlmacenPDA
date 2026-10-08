package com.example.gestionalmacenpda.data.local.dao

import androidx.room.*
import com.example.gestionalmacenpda.domain.model.Categoria
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriaDao {
    // Obtenemos todas las categorias
    @Query("SELECT * FROM categorias ORDER BY nombre ASC")
    fun obtenerTodas(): Flow<List<Categoria>>

    // Obtenemos una categoria a traves del id
    @Query("SELECT * FROM categorias WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Int): Categoria?

    // Añadimos una nueva categoría
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(categoria: Categoria)

    // Eliminamosuna categoría
    @Delete
    suspend fun eliminar(categoria: Categoria)
}