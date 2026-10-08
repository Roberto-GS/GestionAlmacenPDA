package com.example.gestionalmacenpda.ui.producto

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gestionalmacenpda.domain.model.Categoria
import com.example.gestionalmacenpda.domain.model.Ubicaciones

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAltaProducto(
    viewModel: AltaProductoViewModel = hiltViewModel(),
    onVolver: () -> Unit,
    onGuardado: () -> Unit
) {
    val estado by viewModel.estado.collectAsState()
    val categorias by viewModel.categorias.collectAsState()
    val ubicaciones by viewModel.ubicaciones.collectAsState()

    var expandirCategoria by remember { mutableStateOf(false) }
    var expandirUbicacion by remember { mutableStateOf(false) }

    LaunchedEffect(estado) { if (estado is AltaProductoState.Guardado) onGuardado() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Producto") },
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
        Column(
            modifier = Modifier
                .fillMaxSize().padding(padding).padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Identificación
            EtiquetaSeccion("Identificación")
            OutlinedTextField(viewModel.codigo, { viewModel.codigo = it },
                label = { Text("Código interno *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(viewModel.nombre, { viewModel.nombre = it },
                label = { Text("Nombre *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(viewModel.descripcion, { viewModel.descripcion = it },
                label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
            OutlinedTextField(viewModel.codigoBarras, { viewModel.codigoBarras = it },
                label = { Text("Código de barras") }, modifier = Modifier.fillMaxWidth(), singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))

            HorizontalDivider()

            // Clasificación con pickers
            EtiquetaSeccion("Clasificación")

            // Picker de Categoría
            ExposedDropdownMenuBox(
                expanded = expandirCategoria,
                onExpandedChange = { expandirCategoria = it }
            ) {
                OutlinedTextField(
                    value = viewModel.categoriaSeleccionada?.nombre ?: "Sin categoría",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
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

            // Picker de Ubicación
            ExposedDropdownMenuBox(
                expanded = expandirUbicacion,
                onExpandedChange = { expandirUbicacion = it }
            ) {
                OutlinedTextField(
                    value = viewModel.ubicacionSeleccionada?.let { "${it.codigo} — ${it.descripcion ?: ""}" } ?: "Sin ubicación",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Ubicación") },
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

            // Stock
            EtiquetaSeccion("Stock")
            OutlinedTextField(viewModel.unidadMedida, { viewModel.unidadMedida = it },
                label = { Text("Unidad de medida * (UND, KG, M…)") },
                modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(viewModel.stockInicial, { viewModel.stockInicial = it },
                label = { Text("Stock inicial *") }, modifier = Modifier.fillMaxWidth(), singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
            OutlinedTextField(viewModel.stockMinimo, { viewModel.stockMinimo = it },
                label = { Text("Stock mínimo *") }, modifier = Modifier.fillMaxWidth(), singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))

            HorizontalDivider()
            OutlinedTextField(viewModel.observaciones, { viewModel.observaciones = it },
                label = { Text("Observaciones") }, modifier = Modifier.fillMaxWidth(), minLines = 2)

            if (estado is AltaProductoState.Error) {
                Text((estado as AltaProductoState.Error).mensaje,
                    color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }

            val valido = viewModel.codigo.isNotBlank() && viewModel.nombre.isNotBlank()
                    && viewModel.unidadMedida.isNotBlank()
                    && viewModel.stockMinimo.toDoubleOrNull() != null
                    && viewModel.stockInicial.toDoubleOrNull() != null

            Button(onClick = { viewModel.guardar() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = MaterialTheme.shapes.small,
                enabled = valido && estado !is AltaProductoState.Guardando
            ) {
                if (estado is AltaProductoState.Guardando) {
                    CircularProgressIndicator(Modifier.size(22.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("GUARDAR PRODUCTO", style = MaterialTheme.typography.labelLarge)
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun EtiquetaSeccion(texto: String) {
    Text(texto, style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant)
}