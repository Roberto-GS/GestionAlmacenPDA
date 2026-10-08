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
import com.example.gestionalmacenpda.ui.producto.ProductoFiltroViewModel
import com.example.gestionalmacenpda.ui.theme.FondoStockBajo
import com.example.gestionalmacenpda.ui.theme.TextoStockBajo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaProductosPorFiltro(
    titulo: String,
    filtroId: Int,
    tipoFiltro: String,
    viewModel: ProductoFiltroViewModel = hiltViewModel(),
    onVolver: () -> Unit,
    onProductoClick: (Int) -> Unit
) {
    LaunchedEffect(filtroId, tipoFiltro) {
        viewModel.cargar(filtroId, tipoFiltro)
    }

    val listaProductos by viewModel.listaProductos.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titulo) },
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
        if (listaProductos.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No hay productos en esta sección",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        "${listaProductos.size} producto(s)",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                items(
                    items = listaProductos,
                    key = { prod: Producto -> prod.id }
                ) { prod: Producto ->
                    TarjetaProductoFiltro(
                        producto = prod,
                        onClick = { onProductoClick(prod.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TarjetaProductoFiltro(
    producto: Producto,
    onClick: () -> Unit
) {
    val esStockBajo = producto.stockActual <= producto.stockMinimo
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (esStockBajo) FondoStockBajo
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Cód: ${producto.codigo}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${producto.stockActual} ${producto.unidadMedida}",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (esStockBajo) TextoStockBajo
                    else MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Black
                )
                if (esStockBajo) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = TextoStockBajo,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = " STOCK BAJO",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextoStockBajo
                        )
                    }
                }
            }
        }
    }
}