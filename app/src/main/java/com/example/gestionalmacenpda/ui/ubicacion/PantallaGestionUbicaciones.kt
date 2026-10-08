package com.example.gestionalmacenpda.ui.ubicacion

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
import com.example.gestionalmacenpda.domain.model.Ubicaciones

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaGestionUbicaciones(
    viewModel: UbicacionViewModel = hiltViewModel(),
    onVolver: () -> Unit,
    onVerProductosUbicacion: (Int, String) -> Unit
) {
    val ubicaciones by viewModel.ubicaciones.collectAsState()
    var mostrarDialogoCrear by remember { mutableStateOf(false) }
    var ubicacionAEliminar by remember { mutableStateOf<Ubicaciones?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ubicaciones") },
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
                Icon(Icons.Default.Add, contentDescription = "Nueva ubicación")
            }
        }
    ) { padding ->
        if (ubicaciones.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.LocationOn, contentDescription = null,
                        modifier = Modifier.size(56.dp), tint = MaterialTheme.colorScheme.outline)
                    Spacer(Modifier.height(12.dp))
                    Text("No hay ubicaciones creadas", style = MaterialTheme.typography.bodyLarge)
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
                    Text("${ubicaciones.size} ubicación(es)",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp))
                }
                items(ubicaciones, key = { it.id }) { ubicacion ->
                    CardUbicacion(
                        ubicacion = ubicacion,
                        onVerProductos = { onVerProductosUbicacion(ubicacion.id, ubicacion.codigo) },
                        onEliminar = { ubicacionAEliminar = ubicacion }
                    )
                }
                item { Spacer(Modifier.height(72.dp)) }
            }
        }
    }

    if (mostrarDialogoCrear) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoCrear = false; viewModel.limpiarMensaje() },
            title = { Text("Nueva ubicación") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = viewModel.codigoNuevo,
                        onValueChange = { viewModel.codigoNuevo = it },
                        label = { Text("Código * (ej: A-01-01)") },
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
                    viewModel.crearUbicacion()
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

    ubicacionAEliminar?.let { ubic ->
        AlertDialog(
            onDismissRequest = { ubicacionAEliminar = null },
            title = { Text("Eliminar ubicación") },
            text  = { Text("¿Seguro que quieres eliminar \"${ubic.codigo}\"? Los productos que la tengan asignada quedarán sin ubicación.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.eliminarUbicacion(ubic)
                    ubicacionAEliminar = null
                }) { Text("ELIMINAR", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { ubicacionAEliminar = null }) { Text("CANCELAR") }
            }
        )
    }
}

@Composable
fun CardUbicacion(
    ubicacion: Ubicaciones,
    onVerProductos: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocationOn, contentDescription = null,
                tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(ubicacion.codigo, style = MaterialTheme.typography.titleSmall)
                if (!ubicacion.descripcion.isNullOrBlank()) {
                    Text(ubicacion.descripcion, style = MaterialTheme.typography.bodySmall,
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