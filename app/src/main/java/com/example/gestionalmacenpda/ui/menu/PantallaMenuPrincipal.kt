package com.example.gestionalmacenpda.ui.menu

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.gestionalmacenpda.domain.model.RolUsuario
import com.example.gestionalmacenpda.domain.model.Usuario
import com.example.gestionalmacenpda.ui.theme.NaranjaAjuste
import com.example.gestionalmacenpda.ui.theme.RojoSalida
import com.example.gestionalmacenpda.ui.theme.VerdeEntrada

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMenuPrincipal(
    usuario: Usuario,
    onNavegarAInventario: () -> Unit,
    onNavegarAHistorial: () -> Unit,
    onNavegarAStockBajo: () -> Unit,
    onNavegarAEntrada: () -> Unit,
    onNavegarASalida: () -> Unit,
    onNavegarAAjuste: () -> Unit,
    onNavegarACategorias: () -> Unit,
    onNavegarAUbicaciones: () -> Unit,
    onNavegarAUsuarios: () -> Unit,
    onNavegarAInactivos: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Control") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(usuario.nombre_completo, style = MaterialTheme.typography.titleMedium)
                    Text(
                        if (usuario.rol_id == 1) "Administrador" else "Operario",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            when (usuario.rol_id) {
                RolUsuario.administrador.ordinal + 1 -> MenuAdministrador(
                    onInventario = onNavegarAInventario,
                    onHistorial = onNavegarAHistorial,
                    onStockBajo = onNavegarAStockBajo,
                    onEntrada = onNavegarAEntrada,
                    onSalida = onNavegarASalida,
                    onAjuste = onNavegarAAjuste,
                    onCategorias = onNavegarACategorias,
                    onUbicaciones = onNavegarAUbicaciones,
                    onUsuarios = onNavegarAUsuarios,
                    onInactivos = onNavegarAInactivos
                )
                else -> MenuOperario(
                    onEntrada = onNavegarAEntrada,
                    onSalida = onNavegarASalida,
                    onAjuste = onNavegarAAjuste,
                    onHistorial = onNavegarAHistorial
                )
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun MenuAdministrador(
    onInventario: () -> Unit,
    onHistorial: () -> Unit,
    onStockBajo: () -> Unit,
    onEntrada: () -> Unit,
    onSalida: () -> Unit,
    onAjuste: () -> Unit,
    onCategorias: () -> Unit,
    onUbicaciones: () -> Unit,
    onUsuarios: () -> Unit,
    onInactivos: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

        EtiquetaSeccion("Consulta")
        BotonMenu("INVENTARIO",               Icons.Default.Inventory,    MaterialTheme.colorScheme.primary, onInventario)
        BotonMenu("HISTORIAL DE MOVIMIENTOS", Icons.Default.History,      MaterialTheme.colorScheme.primary, onHistorial)
        BotonMenu("PRODUCTOS CON STOCK BAJO", Icons.Default.Warning,      NaranjaAjuste, onStockBajo)

        Spacer(Modifier.height(4.dp))
        EtiquetaSeccion("Operaciones")
        BotonMenu("REGISTRAR ENTRADA",  Icons.Default.AddCircle,    VerdeEntrada,                        onEntrada)
        BotonMenu("REGISTRAR SALIDA",   Icons.Default.RemoveCircle, RojoSalida,                          onSalida)
        BotonMenu("REGISTRAR AJUSTE",   Icons.Default.Tune,         MaterialTheme.colorScheme.secondary, onAjuste)

        Spacer(Modifier.height(4.dp))
        EtiquetaSeccion("Gestión")
        BotonMenu("CATEGORÍAS",          Icons.Default.Category,          MaterialTheme.colorScheme.secondary, onCategorias)
        BotonMenu("UBICACIONES",         Icons.Default.LocationOn,        MaterialTheme.colorScheme.secondary, onUbicaciones)
        BotonMenu("USUARIOS",            Icons.Default.People,            MaterialTheme.colorScheme.secondary, onUsuarios)
        BotonMenu("PRODUCTOS INACTIVOS", Icons.Default.Tune, MaterialTheme.colorScheme.secondary, onInactivos)
    }
}

@Composable
fun MenuOperario(
    onEntrada: () -> Unit, onSalida: () -> Unit,
    onAjuste: () -> Unit,  onHistorial: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        EtiquetaSeccion("Operaciones")
        BotonMenu("REGISTRAR ENTRADA",    Icons.Default.AddCircle,    VerdeEntrada,                        onEntrada)
        BotonMenu("REGISTRAR SALIDA",     Icons.Default.RemoveCircle, RojoSalida,                          onSalida)
        BotonMenu("REGISTRAR AJUSTE",     Icons.Default.Tune,         MaterialTheme.colorScheme.secondary, onAjuste)
        BotonMenu("VER HISTORIAL",        Icons.Default.History,      MaterialTheme.colorScheme.primary,   onHistorial)
    }
}

@Composable
fun EtiquetaSeccion(texto: String) {
    Text(texto, style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 4.dp))
}

@Composable
fun BotonMenu(texto: String, icono: ImageVector, color: Color, onClick: () -> Unit) {
    Button(
        onClick  = onClick,
        modifier = Modifier.fillMaxWidth().height(54.dp),
        shape    = MaterialTheme.shapes.small,
        colors   = ButtonDefaults.buttonColors(containerColor = color)
    ) {
        Icon(icono, null, Modifier.size(20.dp))
        Spacer(Modifier.width(10.dp))
        Text(texto, style = MaterialTheme.typography.labelLarge)
    }
}