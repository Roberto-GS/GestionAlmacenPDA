package com.example.gestionalmacenpda.ui.producto

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gestionalmacenpda.domain.model.Producto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaProductosInactivos(
    viewModel: ProductosInactivosViewModel = hiltViewModel(),
    onVolver: () -> Unit
) {
    val productos by viewModel.productos.collectAsState()
    val mensajeError by viewModel.mensajeError.collectAsState()
    val productoReactivado by viewModel.productoReactivado.collectAsState()

    var productoAReactivar by remember { mutableStateOf<Producto?>(null) }

    // Usamos un Snackbar para confirmar que se reactivó
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(productoReactivado) {
        if (productoReactivado) {
            snackbarHostState.showSnackbar("Producto reactivado correctamente")
            viewModel.limpiarReactivado()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Productos Inactivos") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            // Banner de error
            mensajeError?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(error,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f))
                        IconButton(onClick = { viewModel.limpiarError() }) {
                            Icon(Icons.Default.Close, null,
                                tint = MaterialTheme.colorScheme.onErrorContainer)
                        }
                    }
                }
            }

            if (productos.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CheckCircle, null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(56.dp))
                        Spacer(Modifier.height(12.dp))
                        Text("No hay productos inactivos",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold)
                        Text("Todos los productos están activos",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Info, null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    "${productos.size} producto(s) inactivo(s). " +
                                            "Pulsa el botón para reactivar un producto.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    items(productos, key = { it.id }) { producto ->
                        CardProductoInactivo(
                            producto = producto,
                            onReactivar = { productoAReactivar = producto }
                        )
                    }
                }
            }
        }
    }

    // Diálogo de confirmación
    productoAReactivar?.let { prod ->
        AlertDialog(
            onDismissRequest = { productoAReactivar = null },
            icon = {
                Icon(Icons.Default.RestoreFromTrash, null,
                    tint = MaterialTheme.colorScheme.primary)
            },
            title = { Text("Reactivar producto") },
            text = {
                Text("¿Reactivar \"${prod.nombre}\"? Volverá a aparecer en el inventario con su stock actual de ${prod.stockActual} ${prod.unidadMedida}.")
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.reactivar(prod)
                    productoAReactivar = null
                }) { Text("REACTIVAR") }
            },
            dismissButton = {
                TextButton(onClick = { productoAReactivar = null }) {
                    Text("CANCELAR")
                }
            }
        )
    }
}

@Composable
fun CardProductoInactivo(
    producto: Producto,
    onReactivar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Inventory, null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    producto.nombre,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    "Cód: ${producto.codigo}  ·  Stock: ${producto.stockActual} ${producto.unidadMedida}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (!producto.fechaModificacion.isNullOrBlank()) {
                    Text(
                        "Baja: ${producto.fechaModificacion}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            // Botón de reactivar
            FilledTonalButton(
                onClick = onReactivar,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.RestoreFromTrash, null,
                    modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Reactivar", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}