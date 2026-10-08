package com.example.gestionalmacenpda.ui.producto

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaEditarProducto(
    idProducto: Int,
    viewModel: EditarProductoViewModel = hiltViewModel(),
    onVolver: () -> Unit,
    onGuardado: () -> Unit
) {
    val estado by viewModel.estado.collectAsState()
    val categorias  by viewModel.categorias.collectAsState()
    val ubicaciones by viewModel.ubicaciones.collectAsState()

    var expandirCategoria by remember { mutableStateOf(false) }
    var expandirUbicacion by remember { mutableStateOf(false) }
    var confirmarBaja by remember { mutableStateOf(false) }

    LaunchedEffect(idProducto) { viewModel.cargarProducto(idProducto) }
    LaunchedEffect(estado) { if (estado is EditarProductoState.Guardado) onGuardado() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Producto") },
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
        when (estado) {
            is EditarProductoState.Cargando ->
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            else ->
                Column(
                    modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    EtiquetaSeccion("Datos del producto")
                    OutlinedTextField(viewModel.nombre, { viewModel.nombre = it },
                        label = { Text("Nombre *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    OutlinedTextField(viewModel.descripcion, { viewModel.descripcion = it },
                        label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                    OutlinedTextField(viewModel.unidadMedida, { viewModel.unidadMedida = it },
                        label = { Text("Unidad de medida *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    OutlinedTextField(viewModel.stockMinimo, { viewModel.stockMinimo = it },
                        label = { Text("Stock mínimo *") }, modifier = Modifier.fillMaxWidth(), singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                    OutlinedTextField(viewModel.codigoBarras, { viewModel.codigoBarras = it },
                        label = { Text("Código de barras") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

                    HorizontalDivider()
                    EtiquetaSeccion("Clasificación")

                    // Picker categoría
                    ExposedDropdownMenuBox(expanded = expandirCategoria, onExpandedChange = { expandirCategoria = it }) {
                        OutlinedTextField(
                            value = viewModel.categoriaSeleccionada?.nombre ?: "Sin categoría",
                            onValueChange = {}, readOnly = true, label = { Text("Categoría") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandirCategoria) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(expanded = expandirCategoria, onDismissRequest = { expandirCategoria = false }) {
                            DropdownMenuItem(text = { Text("Sin categoría") }, onClick = {
                                viewModel.categoriaSeleccionada = null; expandirCategoria = false
                            })
                            categorias.forEach { cat ->
                                DropdownMenuItem(text = { Text(cat.nombre) }, onClick = {
                                    viewModel.categoriaSeleccionada = cat; expandirCategoria = false
                                })
                            }
                        }
                    }

                    // Picker ubicación
                    ExposedDropdownMenuBox(expanded = expandirUbicacion, onExpandedChange = { expandirUbicacion = it }) {
                        OutlinedTextField(
                            value = viewModel.ubicacionSeleccionada?.let { "${it.codigo}${if (!it.descripcion.isNullOrBlank()) " — ${it.descripcion}" else ""}" } ?: "Sin ubicación",
                            onValueChange = {}, readOnly = true, label = { Text("Ubicación") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandirUbicacion) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(expanded = expandirUbicacion, onDismissRequest = { expandirUbicacion = false }) {
                            DropdownMenuItem(text = { Text("Sin ubicación") }, onClick = {
                                viewModel.ubicacionSeleccionada = null; expandirUbicacion = false
                            })
                            ubicaciones.forEach { ubic ->
                                DropdownMenuItem(
                                    text = { Text("${ubic.codigo}${if (!ubic.descripcion.isNullOrBlank()) " — ${ubic.descripcion}" else ""}") },
                                    onClick = { viewModel.ubicacionSeleccionada = ubic; expandirUbicacion = false }
                                )
                            }
                        }
                    }

                    HorizontalDivider()
                    OutlinedTextField(viewModel.observaciones, { viewModel.observaciones = it },
                        label = { Text("Observaciones") }, modifier = Modifier.fillMaxWidth(), minLines = 2)

                    if (estado is EditarProductoState.Error) {
                        Text((estado as EditarProductoState.Error).mensaje,
                            color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                    }

                    val valido = viewModel.nombre.isNotBlank() && viewModel.unidadMedida.isNotBlank()
                            && viewModel.stockMinimo.toDoubleOrNull() != null

                    Button(onClick = { viewModel.guardar() },
                        modifier = Modifier.fillMaxWidth().height(56.dp), shape = MaterialTheme.shapes.small,
                        enabled = valido && estado !is EditarProductoState.Guardando
                    ) {
                        if (estado is EditarProductoState.Guardando)
                            CircularProgressIndicator(Modifier.size(22.dp), color = MaterialTheme.colorScheme.onPrimary)
                        else Text("GUARDAR CAMBIOS", style = MaterialTheme.typography.labelLarge)
                    }

                    Spacer(Modifier.height(8.dp))
                    HorizontalDivider()
                    Text("Zona de peligro", style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.error)
                    OutlinedButton(onClick = { confirmarBaja = true },
                        modifier = Modifier.fillMaxWidth().height(50.dp), shape = MaterialTheme.shapes.small,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) { Text("DAR DE BAJA EL PRODUCTO") }
                    Spacer(Modifier.height(8.dp))
                }
        }
    }

    if (confirmarBaja) {
        AlertDialog(
            onDismissRequest = { confirmarBaja = false },
            title = { Text("Dar de baja") },
            text = { Text("El producto quedará inactivo. El historial de movimientos se conserva.") },
            confirmButton = {
                TextButton(onClick = { viewModel.desactivar(); confirmarBaja = false }) {
                    Text("DAR DE BAJA", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmarBaja = false }) { Text("CANCELAR") }
            }
        )
    }
}

@Composable
private fun EtiquetaSeccion(texto: String) {
    Text(texto, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
}