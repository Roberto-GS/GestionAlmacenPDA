package com.example.gestionalmacenpda.ui.movimientos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gestionalmacenpda.domain.model.Movimiento
import com.example.gestionalmacenpda.ui.theme.ColorAjusteNegHistorial
import com.example.gestionalmacenpda.ui.theme.ColorAjustePosHistorial
import com.example.gestionalmacenpda.ui.theme.ColorEntradaHistorial
import com.example.gestionalmacenpda.ui.theme.ColorSalidaHistorial

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaHistorialProducto(
    productoId: Int,
    viewModel: HistorialProductoViewModel = hiltViewModel(),
    onVolver: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(productoId) { viewModel.cargar(productoId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial del Producto") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val s = state) {
                is HistorialProductoState.Cargando ->
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

                is HistorialProductoState.Vacio ->
                    Text("No hay movimientos para este producto",
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.onSurfaceVariant)

                is HistorialProductoState.Error ->
                    Text("Error: ${s.mensaje}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center))

                is HistorialProductoState.Exito ->
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            Text("${s.movimientos.size} movimiento(s)",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 4.dp))
                        }
                        items(s.movimientos) { mov -> CardMovimientoProducto(mov) }
                    }
            }
        }
    }
}

@Composable
fun CardMovimientoProducto(mov: Movimiento) {
    val colorTipo = when (mov.tipoMovimientoId) {
        1 -> ColorEntradaHistorial
        2 -> ColorSalidaHistorial
        3 -> ColorAjustePosHistorial
        else -> ColorAjusteNegHistorial
    }
    val etiquetaTipo = when (mov.tipoMovimientoId) {
        1 -> "ENTRADA"
        2 -> "SALIDA"
        3 -> "AJUSTE +"
        else -> "AJUSTE -"
    }
    val esPositivo = mov.stockResultante >= mov.stockAnterior
    val simbolo    = if (esPositivo) "+" else "-"

    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Surface(shape = MaterialTheme.shapes.small, color = colorTipo.copy(alpha = 0.12f)) {
                        Text(etiquetaTipo,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = colorTipo, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(formatearFechaHistorial(mov.fechaMovimiento),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text("$simbolo${mov.cantidad}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = colorTipo, fontWeight = FontWeight.Black)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text("Antes: ${mov.stockAnterior}  →  Después: ${mov.stockResultante}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)

            if (!mov.referencia.isNullOrBlank()) {
                Text("Ref: ${mov.referencia}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp))
            }
            if (!mov.observaciones.isNullOrBlank()) {
                Text(mov.observaciones,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.padding(top = 2.dp))
            }
        }
    }
}

private fun formatearFechaHistorial(milis: Long): String {
    val sdf = java.text.SimpleDateFormat("dd/MM/yyyy  HH:mm", java.util.Locale.getDefault())
    return sdf.format(java.util.Date(milis))
}