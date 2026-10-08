package com.example.gestionalmacenpda.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.gestionalmacenpda.data.local.dao.*
import com.example.gestionalmacenpda.domain.model.*

// Por cada cambio que se haga en la estructura de base de datos
// hay que aumentar la versión sino no se actualizara
@Database(
    entities = [
        Rol::class,
        Usuario::class,
        Categoria::class,
        Ubicaciones::class,
        Producto::class,
        tipoMovimiento::class,
        Movimiento::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AlmacenDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun productoDao(): ProductoDao
    abstract fun movimientoDao(): MovimientoDao
    abstract fun categoriaDao(): CategoriaDao
    abstract fun ubicacionDao(): UbicacionDao
    abstract fun rolDao(): RolDao
}