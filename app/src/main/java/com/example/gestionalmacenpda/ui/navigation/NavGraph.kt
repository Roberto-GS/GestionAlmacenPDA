package com.example.gestionalmacenpda.ui.navigation

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gestionalmacenpda.ui.categoria.PantallaGestionCategorias
import com.example.gestionalmacenpda.ui.producto.PantallaProductosPorFiltro
import com.example.gestionalmacenpda.ui.login.PantallaLogin
import com.example.gestionalmacenpda.ui.menu.PantallaMenuPrincipal
import com.example.gestionalmacenpda.ui.movimientos.*
import com.example.gestionalmacenpda.ui.producto.*
import com.example.gestionalmacenpda.ui.sesion.SesionViewModel
import com.example.gestionalmacenpda.ui.ubicacion.PantallaGestionUbicaciones
import com.example.gestionalmacenpda.ui.usuario.PantallaGestionUsuarios

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val sesionViewModel: SesionViewModel = hiltViewModel()
    val usuario by sesionViewModel.usuario.collectAsState()

    NavHost(navController = navController, startDestination = "pantalla_login") {

        // LOGIN
        composable("pantalla_login") {
            PantallaLogin(onLoginSuccess = { u ->
                sesionViewModel.iniciarSesion(u)
                navController.navigate("pantalla_menu") {
                    popUpTo("pantalla_login") { inclusive = true }
                }
            })
        }

        // MENÚ PRINCIPAL
        composable("pantalla_menu") {
            usuario?.let { u ->
                PantallaMenuPrincipal(
                    usuario = u,
                    onNavegarAInventario = { navController.navigate("pantalla_lista_productos") },
                    onNavegarAHistorial = { navController.navigate("pantalla_historial") },
                    onNavegarAStockBajo = { navController.navigate("pantalla_stock_bajo") },
                    onNavegarAEntrada = { navController.navigate("lista_productos/ENTRADA") },
                    onNavegarASalida = { navController.navigate("lista_productos/SALIDA") },
                    onNavegarAAjuste = { navController.navigate("pantalla_selector_ajuste") },
                    onNavegarACategorias = { navController.navigate("pantalla_categorias") },
                    onNavegarAUbicaciones = { navController.navigate("pantalla_ubicaciones") },
                    onNavegarAUsuarios = { navController.navigate("pantalla_usuarios") },
                    onNavegarAInactivos = { navController.navigate("pantalla_productos_inactivos") }
                )
            }
        }

        // LISTA DE PRODUCTOS
        composable("pantalla_lista_productos") {
            PantallaListaProductos(
                onProductoClick = { id -> navController.navigate("pantalla_detalle/$id") },
                onAltaProducto = { navController.navigate("pantalla_alta_producto") }
            )
        }

        // LISTA DE LOS MOVIMIENTOS
        composable("lista_productos/{modo}") { back ->
            val modo = back.arguments?.getString("modo") ?: "CONSULTA"
            PantallaListaProductos(
                onProductoClick = { id ->
                    when (modo) {
                        "ENTRADA" -> navController.navigate("pantalla_movimiento/$id/1")
                        "SALIDA" -> navController.navigate("pantalla_movimiento/$id/2")
                        "AJUSTE_POS" -> navController.navigate("pantalla_movimiento/$id/3")
                        "AJUSTE_NEG" -> navController.navigate("pantalla_movimiento/$id/4")
                        else -> navController.navigate("pantalla_detalle/$id")
                    }
                },
                onAltaProducto = { navController.navigate("pantalla_alta_producto") }
            )
        }

        // FICHA DE UN PRODUCTO
        composable(
            "pantalla_detalle/{idProducto}",
            arguments = listOf(navArgument("idProducto") { type = NavType.IntType })
        ) { back ->
            val id = back.arguments?.getInt("idProducto") ?: 0
            PantallaDetalleProducto(
                idProducto = id,
                onBack = { navController.popBackStack() },
                onRegistrarMovimiento = { idProd, tipoId ->
                    navController.navigate("pantalla_movimiento/$idProd/$tipoId")
                },
                onEditarProducto = { idProd ->
                    navController.navigate("pantalla_editar_producto/$idProd")
                },
                onVerHistorialProducto = { idProd ->
                    navController.navigate("pantalla_historial_producto/$idProd")
                }
            )
        }

        // ALTA DE PRODUCTO
        composable("pantalla_alta_producto") {
            PantallaAltaProducto(
                onVolver = { navController.popBackStack() },
                onGuardado = { navController.popBackStack() }
            )
        }

        // EDICIÓN DE UN PRODUCTO
        composable(
            "pantalla_editar_producto/{idProducto}",
            arguments = listOf(navArgument("idProducto") { type = NavType.IntType })
        ) { back ->
            val id = back.arguments?.getInt("idProducto") ?: 0
            PantallaEditarProducto(
                idProducto = id,
                onVolver = { navController.popBackStack() },
                onGuardado = {
                    navController.popBackStack("pantalla_lista_productos", inclusive = false)
                }
            )
        }

        // SELECTOR DEL TIPO DE AJUSTE
        composable("pantalla_selector_ajuste") {
            PantallaSelectorAjuste(
                onAjustePositivo = { navController.navigate("lista_productos/AJUSTE_POS") },
                onAjusteNegativo = { navController.navigate("lista_productos/AJUSTE_NEG") },
                onVolver = { navController.popBackStack() }
            )
        }

        // HISTORIAL GENERAL
        composable("pantalla_historial") {
            PantallaHistorialMovimientos(onVolver = { navController.popBackStack() })
        }

        // HISTORIAL POR PRODUCTO
        composable(
            "pantalla_historial_producto/{idProducto}",
            arguments = listOf(navArgument("idProducto") { type = NavType.IntType })
        ) { back ->
            val id = back.arguments?.getInt("idProducto") ?: 0
            PantallaHistorialProducto(
                productoId = id,
                onVolver = { navController.popBackStack() }
            )
        }

        // STOCK BAJO
        composable("pantalla_stock_bajo") {
            PantallaStockBajo(
                onVolver = { navController.popBackStack() },
                onProductoClick = { id -> navController.navigate("pantalla_detalle/$id") }
            )
        }

        // REGISTRO DE MOVIMIENTO
        composable(
            "pantalla_movimiento/{idProducto}/{tipoId}",
            arguments = listOf(
                navArgument("idProducto") { type = NavType.IntType },
                navArgument("tipoId") { type = NavType.IntType }
            )
        ) { back ->
            val idProd = back.arguments?.getInt("idProducto") ?: 0
            val idTipo = back.arguments?.getInt("tipoId")     ?: 1
            PantallaMovimiento(
                productoId = idProd,
                tipoMovimientoId = idTipo,
                usuarioId = usuario?.id ?: 0,
                onMovimientoExitoso = {
                    navController.popBackStack("pantalla_menu", inclusive = false)
                },
                onVolver = { navController.popBackStack() }
            )
        }

        // GESTIÓN DE CATEGORÍAS
        composable("pantalla_categorias") {
            PantallaGestionCategorias(
                onVolver = { navController.popBackStack() },
                onVerProductosCategoria = { id, nombre ->
                    navController.navigate("productos_por_filtro/$id/CATEGORIA/$nombre")
                }
            )
        }

        // GESTIÓN DE UBICACIONES
        composable("pantalla_ubicaciones") {
            PantallaGestionUbicaciones(
                onVolver = { navController.popBackStack() },
                onVerProductosUbicacion = { id, codigo ->
                    navController.navigate("productos_por_filtro/$id/UBICACION/$codigo")
                }
            )
        }

        // GESTIÓN DE USUARIOS
        composable("pantalla_usuarios") {
            PantallaGestionUsuarios(
                usuarioActualId = usuario?.id ?: 0,
                onVolver = { navController.popBackStack() }
            )
        }

        // PRODUCTOS INACTIVOS
        composable("pantalla_productos_inactivos") {
            PantallaProductosInactivos(
                onVolver = { navController.popBackStack() }
            )
        }

        // PRODUCTOS FILTRADOS POR CATEGORÍA O UBICACIÓN
        composable(
            "productos_por_filtro/{filtroId}/{tipoFiltro}/{titulo}",
            arguments = listOf(
                navArgument("filtroId") { type = NavType.IntType },
                navArgument("tipoFiltro") { type = NavType.StringType },
                navArgument("titulo") { type = NavType.StringType }
            )
        ) { back ->
            val filtroId   = back.arguments?.getInt("filtroId") ?: 0
            val tipoFiltro = back.arguments?.getString("tipoFiltro") ?: "CATEGORIA"
            val titulo     = back.arguments?.getString("titulo") ?: ""
            PantallaProductosPorFiltro(
                titulo = titulo,
                filtroId = filtroId,
                tipoFiltro = tipoFiltro,
                onVolver = { navController.popBackStack() },
                onProductoClick = { id -> navController.navigate("pantalla_detalle/$id") }
            )
        }
    }
}