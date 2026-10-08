package com.example.gestionalmacenpda.ui.producto

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gestionalmacenpda.ui.theme.FondoStockBajo
import com.example.gestionalmacenpda.ui.theme.NaranjaAjuste
import com.example.gestionalmacenpda.ui.theme.RojoSalida
import com.example.gestionalmacenpda.ui.theme.VerdeEntrada

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalleProducto(
    idProducto: Int,
    viewModel: DetalleProductoViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onRegistrarMovimiento: (Int, Int) -> Unit,
    onEditarProducto: (Int) -> Unit,
    onVerHistorialProducto: (Int) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(idProducto) { viewModel.cargar(idProducto) }

    // Diálogo para elegir el tipo de ajuste directamente desde la ficha
    var mostrarDialogoAjuste by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Artículo") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        state.producto?.let { onVerHistorialProducto(it.id) }
                    }) {
                        Icon(Icons.Default.History, contentDescription = "Historial del producto")
                    }
                    IconButton(onClick = {
                        state.producto?.let { onEditarProducto(it.id) }
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar producto")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        when {
            state.cargando -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            state.producto == null -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text("Producto no encontrado",
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            else -> {
                val prod = state.producto!!
                val esStockBajo = prod.stockActual <= prod.stockMinimo
                val nombreCat = state.categoria?.nombre   ?: "Sin categoría"
                val nombreUbic = state.ubicacion?.let {
                    "${it.codigo}${if (!it.descripcion.isNullOrBlank()) " — ${it.descripcion}" else ""}"
                } ?: "Sin ubicación"

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())  // scroll por si hay mucho contenido
                        .padding(16.dp)
                ) {
                    // Cabecera
                    Text(prod.nombre,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold)
                    Text("Código: ${prod.codigo}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary)

                    // Alerta de bajo stock
                    if (esStockBajo) {
                        Spacer(Modifier.height(8.dp))
                        Card(colors = CardDefaults.cardColors(containerColor = FondoStockBajo)) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = null,
                                    tint = NaranjaAjuste, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Stock por debajo del mínimo",
                                    color = NaranjaAjuste,
                                    style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Bloque: Stock
                    EtiquetaSeccionDetalle("Stock")
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilaDato("Stock actual",  "${prod.stockActual} ${prod.unidadMedida}",  Icons.Default.Inventory)
                            FilaDato("Stock mínimo",  "${prod.stockMinimo} ${prod.unidadMedida}",  Icons.Default.Warning)
                            FilaDato("Unidad de medida", prod.unidadMedida,                        Icons.Default.Scale)
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Bloque: Localización
                    EtiquetaSeccionDetalle("Localización")
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilaDato("Categoría",  nombreCat, Icons.Default.Category)
                            FilaDato("Ubicación",  nombreUbic, Icons.Default.LocationOn)
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Bloque: Identificación
                    EtiquetaSeccionDetalle("Identificación")
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilaDato("Código interno",   prod.codigo, Icons.Default.Tag)
                            FilaDato("Código de barras", prod.codigoBarras ?: "---", Icons.Default.QrCode)
                        }
                    }

                    // Bloque: Descripción
                    if (!prod.descripcion.isNullOrBlank()) {
                        Spacer(Modifier.height(12.dp))
                        EtiquetaSeccionDetalle("Descripción")
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors   = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Text(prod.descripcion,
                                modifier = Modifier.padding(12.dp),
                                style    = MaterialTheme.typography.bodyMedium)
                        }
                    }

                    // Bloque: Observaciones
                    if (!prod.observaciones.isNullOrBlank()) {
                        Spacer(Modifier.height(12.dp))
                        EtiquetaSeccionDetalle("Observaciones")
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors   = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Text(prod.observaciones,
                                modifier = Modifier.padding(12.dp),
                                style    = MaterialTheme.typography.bodyMedium)
                        }
                    }

                    // Bloque: Fechas
                    Spacer(Modifier.height(12.dp))
                    EtiquetaSeccionDetalle("Registro")
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors   = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilaDato("Creado",         prod.fechaCreacion      ?: "---", Icons.Default.DateRange)
                            FilaDato("Última modificación", prod.fechaModificacion ?: "---", Icons.Default.Edit)
                            FilaDato("Estado", if (prod.activo == 1) "Activo" else "Inactivo", Icons.Default.CheckCircle)
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    // Botones de movimiento
                    // Solo la ENTRADA y SALIDA como botones directos.
                    // AJUSTE abre un pequeño diálogo para elegir + o - sin salir de la ficha.
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onRegistrarMovimiento(prod.id, 1) },
                            modifier = Modifier.weight(1f).height(52.dp),
                            shape = MaterialTheme.shapes.small,
                            colors = ButtonDefaults.buttonColors(containerColor = VerdeEntrada)
                        ) {
                            Icon(Icons.Default.AddCircle, null, Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("ENTRADA", style = MaterialTheme.typography.labelLarge)
                        }

                        Button(
                            onClick = { onRegistrarMovimiento(prod.id, 2) },
                            modifier = Modifier.weight(1f).height(52.dp),
                            shape = MaterialTheme.shapes.small,
                            colors = ButtonDefaults.buttonColors(containerColor = RojoSalida)
                        ) {
                            Icon(Icons.Default.RemoveCircle, null, Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("SALIDA", style = MaterialTheme.typography.labelLarge)
                        }

                        OutlinedButton(
                            onClick = { mostrarDialogoAjuste = true },
                            modifier = Modifier.weight(1f).height(52.dp),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Icon(Icons.Default.Tune, null, Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("AJUSTE", style = MaterialTheme.typography.labelLarge)
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // Diálogo para elegir el tipo de ajuste
                    if (mostrarDialogoAjuste) {
                        AlertDialog(
                            onDismissRequest = { mostrarDialogoAjuste = false },
                            title = { Text("Tipo de ajuste") },
                            text  = {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    // Ajuste positivo
                                    Card(
                                        onClick = {
                                            mostrarDialogoAjuste = false
                                            onRegistrarMovimiento(prod.id, 3)
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(
                                            containerColor = VerdeEntrada.copy(alpha = 0.1f))
                                    ) {
                                        Row(modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.AddCircleOutline,
                                                contentDescription = null,
                                                tint = VerdeEntrada,
                                                modifier = Modifier.size(28.dp))
                                            Spacer(Modifier.width(12.dp))
                                            Column {
                                                Text("Ajuste positivo",
                                                    style = MaterialTheme.typography.titleSmall,
                                                    fontWeight = FontWeight.Bold)
                                                Text("Añadir unidades al stock",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }
                                    }
                                    // Ajuste negativo
                                    Card(
                                        onClick = {
                                            mostrarDialogoAjuste = false
                                            onRegistrarMovimiento(prod.id, 4)
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(
                                            containerColor = RojoSalida.copy(alpha = 0.1f))
                                    ) {
                                        Row(modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.RemoveCircleOutline,
                                                contentDescription = null,
                                                tint = RojoSalida,
                                                modifier = Modifier.size(28.dp))
                                            Spacer(Modifier.width(12.dp))
                                            Column {
                                                Text("Ajuste negativo",
                                                    style = MaterialTheme.typography.titleSmall,
                                                    fontWeight = FontWeight.Bold)
                                                Text("Restar unidades del stock",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }
                                    }
                                }
                            },
                            confirmButton = {},
                            dismissButton = {
                                TextButton(onClick = { mostrarDialogoAjuste = false }) {
                                    Text("CANCELAR")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EtiquetaSeccionDetalle(texto: String) {
    Text(
        text = texto.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

@Composable
fun FilaDato(label: String, value: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(10.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium)
        }
    }
}