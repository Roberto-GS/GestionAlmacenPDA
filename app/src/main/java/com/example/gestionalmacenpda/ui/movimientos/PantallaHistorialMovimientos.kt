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
import com.example.gestionalmacenpda.domain.model.MovimientoConDetalles
import com.example.gestionalmacenpda.ui.theme.ColorAjusteNegHistorial
import com.example.gestionalmacenpda.ui.theme.ColorAjustePosHistorial
import com.example.gestionalmacenpda.ui.theme.ColorEntradaHistorial
import com.example.gestionalmacenpda.ui.theme.ColorSalidaHistorial

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaHistorialMovimientos(
    viewModel: HistorialMovimientosViewModel = hiltViewModel(),
    onVolver: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Movimientos") },
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
                is HistorialUiState.Cargando ->
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

                is HistorialUiState.Vacio ->
                    Text("No hay movimientos registrados",
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.onSurfaceVariant)

                is HistorialUiState.Error ->
                    Text("Error: ${s.mensaje}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center))

                is HistorialUiState.Exito ->
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(s.movimientos) { item -> CardMovimiento(item) }
                    }
            }
        }
    }
}

@Composable
fun CardMovimiento(item: MovimientoConDetalles) {
    val tipoId = item.movimiento.tipoMovimientoId
    val colorTipo = when (tipoId) {
        1 -> ColorEntradaHistorial
        2 -> ColorSalidaHistorial
        3 -> ColorAjustePosHistorial
        else -> ColorAjusteNegHistorial
    }
    val etiquetaTipo = when (tipoId) {
        1 -> "ENTRADA"
        2 -> "SALIDA"
        3 -> "AJUSTE +"
        else -> "AJUSTE -"
    }
    val esPositivo = item.movimiento.stockResultante >= item.movimiento.stockAnterior
    val simbolo = if (esPositivo) "+" else "-"

    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(14.dp)) {

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    // Etiqueta del tipo con fondo de color
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = colorTipo.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = etiquetaTipo,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = colorTipo,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = item.producto.nombre,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formatearFechaHistorial(item.movimiento.fechaMovimiento),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "$simbolo${item.movimiento.cantidad}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = colorTipo,
                    fontWeight = FontWeight.Black
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Operario: ${item.usuario.nombre_completo}", style = MaterialTheme.typography.bodySmall)
                if (!item.movimiento.referencia.isNullOrBlank()) {
                    Text("Ref: ${item.movimiento.referencia}",
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = FontStyle.Italic)
                }
            }

            Text(
                text = "Stock: ${item.movimiento.stockAnterior}  →  ${item.movimiento.stockResultante} ${item.producto.unidadMedida}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )

            if (!item.movimiento.observaciones.isNullOrBlank()) {
                Text(
                    text = item.movimiento.observaciones,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

private fun formatearFechaHistorial(milis: Long): String {
    val sdf = java.text.SimpleDateFormat("dd/MM/yyyy  HH:mm", java.util.Locale.getDefault())
    return sdf.format(java.util.Date(milis))
}