package com.example.gestionalmacenpda.ui.categoria

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gestionalmacenpda.domain.model.Categoria

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaGestionCategorias(
    viewModel: CategoriaViewModel = hiltViewModel(),
    onVolver: () -> Unit,
    onVerProductosCategoria: (Int, String) -> Unit
) {
    val categorias by viewModel.categorias.collectAsState()
    var mostrarDialogoCrear by remember { mutableStateOf(false) }
    var categoriaAEliminar by remember { mutableStateOf<Categoria?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Categorías") },
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
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialogoCrear = true }) {
                Icon(Icons.Default.Add, contentDescription = "Nueva categoría")
            }
        }
    ) { padding ->
        if (categorias.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Category, contentDescription = null,
                        modifier = Modifier.size(56.dp), tint = MaterialTheme.colorScheme.outline)
                    Spacer(Modifier.height(12.dp))
                    Text("No hay categorías creadas", style = MaterialTheme.typography.bodyLarge)
                    Text("Pulsa + para añadir una", style = MaterialTheme.typography.bodySmall,
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
                    Text("${categorias.size} categoría(s)",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp))
                }
                items(categorias, key = { it.id }) { categoria ->
                    CardCategoria(
                        categoria = categoria,
                        onVerProductos = { onVerProductosCategoria(categoria.id, categoria.nombre) },
                        onEliminar = { categoriaAEliminar = categoria }
                    )
                }
                item { Spacer(Modifier.height(72.dp)) }
            }
        }
    }

    // Diálogo para crear la categoría
    if (mostrarDialogoCrear) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoCrear = false; viewModel.limpiarMensaje() },
            title = { Text("Nueva categoría") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = viewModel.nombreNueva,
                        onValueChange = { viewModel.nombreNueva = it },
                        label = { Text("Nombre *") },
                        modifier = Modifier.fillMaxWidth(), singleLine = true
                    )
                    OutlinedTextField(
                        value = viewModel.descripcionNueva,
                        onValueChange = { viewModel.descripcionNueva = it },
                        label = { Text("Descripción (opcional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    viewModel.mensajeError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.crearCategoria()
                    if (viewModel.mensajeError == null) mostrarDialogoCrear = false
                }) { Text("GUARDAR") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoCrear = false; viewModel.limpiarMensaje() }) {
                    Text("CANCELAR")
                }
            }
        )
    }

    // Diálogo para confirmar la eliminación
    categoriaAEliminar?.let { cat ->
        AlertDialog(
            onDismissRequest = { categoriaAEliminar = null },
            title = { Text("Eliminar categoría") },
            text = { Text("¿Seguro que quieres eliminar \"${cat.nombre}\"? Los productos que la tengan asignada quedarán sin categoría.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.eliminarCategoria(cat)
                    categoriaAEliminar = null
                }) { Text("ELIMINAR", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { categoriaAEliminar = null }) { Text("CANCELAR") }
            }
        )
    }
}

@Composable
fun CardCategoria(
    categoria: Categoria,
    onVerProductos: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Category, contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(categoria.nombre, style = MaterialTheme.typography.titleSmall)
                if (!categoria.descripcion.isNullOrBlank()) {
                    Text(categoria.descripcion, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            IconButton(onClick = onVerProductos) {
                Icon(Icons.Default.Inventory, contentDescription = "Ver productos",
                    tint = MaterialTheme.colorScheme.secondary)
            }
            IconButton(onClick = onEliminar) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}