package com.example.gestionalmacenpda.ui.producto

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gestionalmacenpda.ui.theme.FondoStockBajo
import com.example.gestionalmacenpda.ui.theme.TextoStockBajo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaListaProductos(
    viewModel: ProductoViewModel = hiltViewModel(),
    onProductoClick: (Int) -> Unit,
    onAltaProducto: () -> Unit
) {
    val productos by viewModel.productos.collectAsState()
    val textoBusqueda by viewModel.busqueda.collectAsState()
    val productoEscaneado by viewModel.productoEscaneado.collectAsState()
    val mensajeEscaneo by viewModel.mensajeEscaneo.collectAsState()

    LaunchedEffect(productoEscaneado) {
        productoEscaneado?.let {
            onProductoClick(it.id)
            viewModel.limpiarEscaneo()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventario Actual") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAltaProducto) {
                Icon(Icons.Default.Add, contentDescription = "Añadir producto")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 12.dp)
        ) {
            OutlinedTextField(
                value = textoBusqueda,
                onValueChange = { viewModel.onBusquedaChanged(it) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                placeholder = { Text("Buscar por nombre, código o escanear…") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                trailingIcon = {
                    if (textoBusqueda.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onBusquedaChanged("") }) {
                            Icon(Icons.Default.Clear, null)
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = { viewModel.procesarCodigoEscaneado(textoBusqueda) }
                )
            )

            if (mensajeEscaneo != null) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Text(mensajeEscaneo!!,
                        modifier = Modifier.padding(10.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodySmall)
                }
            }

            if (productos.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        if (textoBusqueda.isEmpty()) "No hay productos en el inventario"
                        else "Sin resultados para \"$textoBusqueda\"",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(productos, key = { it.producto.id }) { item ->
                        ItemProducto(
                            item = item,
                            onClick = { onProductoClick(item.producto.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ItemProducto(item: ProductoConUbicacion, onClick: () -> Unit) {
    val producto = item.producto
    val esStockBajo = producto.stockActual <= producto.stockMinimo

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (esStockBajo) FondoStockBajo
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(producto.nombre,
                    style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(
                    "Cód: ${producto.codigo}  |  ${item.nombreUbicacion}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${producto.stockActual} ${producto.unidadMedida}",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (esStockBajo) TextoStockBajo
                    else MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Black
                )
                if (esStockBajo) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, null,
                            tint = TextoStockBajo, modifier = Modifier.size(13.dp))
                        Text("STOCK BAJO",
                            style = MaterialTheme.typography.labelSmall, color = TextoStockBajo)
                    }
                }
            }
        }
    }
}