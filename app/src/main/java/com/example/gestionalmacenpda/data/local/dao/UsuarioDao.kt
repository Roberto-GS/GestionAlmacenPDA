package com.example.gestionalmacenpda.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gestionalmacenpda.domain.model.Usuario
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {
    // Comprueba que el usuario y la contraseña sean correctos y estan activo para poder iniciar sesion
    @Query("SELECT * FROM usuarios WHERE username = :username AND password_hash = :password AND activo = 1 LIMIT 1")
    suspend fun login(username: String, password: String): Usuario?

    // Buscamos un usuario activo por su id
    @Query("SELECT * FROM usuarios WHERE id = :id AND activo = 1 LIMIT 1")
    suspend fun obtenerPorId(id: Int): Usuario?

    // Buscamos todos los usuario
    @Query("SELECT * FROM usuarios ORDER BY activo DESC, nombre_completo ASC")
    fun obtenerTodos(): Flow<List<Usuario>>

    // Cambiamos el estado actual de un usuario
    @Query("UPDATE usuarios SET activo = :activo WHERE id = :id")
    suspend fun cambiarEstado(id: Int, activo: Int)

    // Añadimos un nuevo usuario
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(usuario: Usuario)
}