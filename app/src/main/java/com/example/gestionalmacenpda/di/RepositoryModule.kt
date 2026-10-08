package com.example.gestionalmacenpda.di

import com.example.gestionalmacenpda.data.repository.*
import com.example.gestionalmacenpda.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton
    abstract fun bindUsuarioRepository(impl: UsuarioRepositoryImpl): UsuarioRepository

    @Binds @Singleton
    abstract fun bindProductoRepository(impl: ProductoRepositoryImpl): ProductoRepository

    @Binds @Singleton
    abstract fun bindMovimientoRepository(impl: MovimientoRepositoryImpl): MovimientoRepository

    @Binds @Singleton
    abstract fun bindCategoriaRepository(impl: CategoriaRepositoryImpl): CategoriaRepository

    @Binds @Singleton
    abstract fun bindUbicacionRepository(impl: UbicacionRepositoryImpl): UbicacionRepository

    @Binds @Singleton
    abstract fun bindRolRepository(impl: RolRepositoryImpl): RolRepository
}