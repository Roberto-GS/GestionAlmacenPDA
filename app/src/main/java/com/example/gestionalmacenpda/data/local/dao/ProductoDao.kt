package com.example.gestionalmacenpda.data.local.dao

import androidx.room.*
import com.example.gestionalmacenpda.domain.model.Producto
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {
    // Obtenemos todos los productos activos
    @Query("SELECT * FROM productos WHERE activo = 1 ORDER BY nombre ASC")
    fun obtenerTodos(): Flow<List<Producto>>

    // Buscamos un producto a traves de un criterio en especifico
    @Query("SELECT * FROM productos WHERE (nombre LIKE :criterio OR codigo LIKE :criterio) AND activo = 1")
    fun buscarPorCriterio(criterio: String): Flow<List<Producto>>

    // Buscamos a traves del codigo de barras de un producto
    @Query("SELECT * FROM productos WHERE codigo_barras = :codigoBarras AND activo = 1 LIMIT 1")
    suspend fun buscarPorCodigoBarras(codigoBarras: String): Producto?

    // Buscamos un producto a traves de su id
    @Query("SELECT * FROM productos WHERE id = :id AND activo = 1 LIMIT 1")
    fun obtenerPorId(id: Int): Flow<Producto?>

    // Obtenemos todos los productos inactivos
    @Query("SELECT * FROM productos WHERE activo = 0 ORDER BY nombre ASC")
    fun obtenerInactivos(): Flow<List<Producto>>

    // Añadimos un nuevo producto
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(producto: Producto)

    // Actualizamos un producto
    @Update
    suspend fun actualizar(producto: Producto)

    // Actializamos elstock actual de un producto
    @Query("UPDATE productos SET stock_actual = :nuevoStock, fecha_modificacion = :fechaModificacion WHERE id = :id")
    suspend fun actualizarStock(id: Int, nuevoStock: Double, fechaModificacion: String)

    // Damos de baja un producto
    @Query("UPDATE productos SET activo = 0, fecha_modificacion = :fecha WHERE id = :id")
    suspend fun desactivar(id: Int, fecha: String)

    // Reactivamos un producto dado de baja
    @Query("UPDATE productos SET activo = 1, fecha_modificacion = :fecha WHERE id = :id")
    suspend fun reactivar(id: Int, fecha: String)

    // Buscamos todos los productos que tengan un stock pordebajo del mínimo
    @Query("SELECT * FROM productos WHERE stock_actual <= stock_minimo AND activo = 1 ORDER BY nombre ASC")
    fun obtenerConStockBajo(): Flow<List<Producto>>
}