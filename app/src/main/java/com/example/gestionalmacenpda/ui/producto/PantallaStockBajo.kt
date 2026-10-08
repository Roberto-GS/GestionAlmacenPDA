package com.example.gestionalmacenpda.ui.producto

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gestionalmacenpda.domain.model.Producto
import com.example.gestionalmacenpda.ui.theme.FondoStockBajo
import com.example.gestionalmacenpda.ui.theme.NaranjaAjuste
import com.example.gestionalmacenpda.ui.theme.TextoStockBajo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaStockBajo(
    viewModel: ProductoViewModel = hiltViewModel(),
    onVolver: () -> Unit,
    onProductoClick: (Int) -> Unit
) {
    val productos by viewModel.productosStockBajo.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Stock Bajo") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NaranjaAjuste,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        if (productos.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text("¡Todo en orden!", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("No hay productos con stock bajo", style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        "${productos.size} producto(s) por debajo del mínimo",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextoStockBajo,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                items(productos) { producto ->
                    CardStockBajo(producto = producto, onClick = { onProductoClick(producto.id) })
                }
            }
        }
    }
}

@Composable
fun CardStockBajo(producto: Producto, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = FondoStockBajo)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.Warning,
                contentDescription = null,
                tint = TextoStockBajo,
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(producto.nombre, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text("Cód: ${producto.codigo}", style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${producto.stockActual} ${producto.unidadMedida}",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextoStockBajo,
                    fontWeight = FontWeight.Black
                )
                Text(
                    "Mín: ${producto.stockMinimo}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}