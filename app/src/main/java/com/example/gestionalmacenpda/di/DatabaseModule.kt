package com.example.gestionalmacenpda.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.gestionalmacenpda.data.local.AlmacenDatabase
import com.example.gestionalmacenpda.data.local.dao.*
import com.example.gestionalmacenpda.util.HashUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AlmacenDatabase {
        return Room.databaseBuilder(context, AlmacenDatabase::class.java, "almacen_db")
            .fallbackToDestructiveMigration()
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)

                    // Roles
                    db.execSQL("INSERT INTO roles (nombre) VALUES ('ADMIN')")
                    db.execSQL("INSERT INTO roles (nombre) VALUES ('OPERARIO')")

                    // Tipos de movimiento
                    db.execSQL("INSERT INTO tipos_movimiento (codigo,nombre) VALUES ('ENTRADA','Entrada')")
                    db.execSQL("INSERT INTO tipos_movimiento (codigo,nombre) VALUES ('SALIDA','Salida')")
                    db.execSQL("INSERT INTO tipos_movimiento (codigo,nombre) VALUES ('AJUSTE_POS','Ajuste Positivo')")
                    db.execSQL("INSERT INTO tipos_movimiento (codigo,nombre) VALUES ('AJUSTE_NEG','Ajuste Negativo')")

                    db.execSQL("""
                        INSERT INTO usuarios (username, password_hash, nombre_completo, rol_id, activo) 
                        VALUES ('admin', '${HashUtils.sha256("admin123")}', 'Administrador de Prueba', 1, 1)
                    """.trimIndent())

                    db.execSQL("""
                        INSERT INTO usuarios (username, password_hash, nombre_completo, rol_id, activo) 
                        VALUES ('usuario', '${HashUtils.sha256("1234")}', 'Operario de Prueba', 2, 1)
                    """.trimIndent())
                }
            })
            .build()
    }

    @Provides fun provideUsuarioDao(db: AlmacenDatabase): UsuarioDao = db.usuarioDao()
    @Provides fun provideProductoDao(db: AlmacenDatabase): ProductoDao = db.productoDao()
    @Provides fun provideMovimientoDao(db: AlmacenDatabase): MovimientoDao = db.movimientoDao()
    @Provides fun provideCategoriaDao(db: AlmacenDatabase): CategoriaDao = db.categoriaDao()
    @Provides fun provideUbicacionDao(db: AlmacenDatabase): UbicacionDao = db.ubicacionDao()
    @Provides fun provideRolDao(db: AlmacenDatabase): RolDao = db.rolDao()
}