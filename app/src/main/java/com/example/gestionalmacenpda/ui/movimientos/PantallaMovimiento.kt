package com.example.gestionalmacenpda.ui.movimientos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gestionalmacenpda.domain.model.TipoMovimientoEnum
import com.example.gestionalmacenpda.ui.theme.NaranjaAjuste
import com.example.gestionalmacenpda.ui.theme.RojoSalida
import com.example.gestionalmacenpda.ui.theme.VerdeEntrada

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMovimiento(
    productoId: Int,
    tipoMovimientoId: Int,
    usuarioId: Int,
    viewModel: MovimientoViewModel = hiltViewModel(),
    onMovimientoExitoso: () -> Unit,
    onVolver: () -> Unit
) {
    val tipo = TipoMovimientoEnum.desdeId(tipoMovimientoId)
    val producto by viewModel.producto.collectAsState()
    val estadoGuardado by viewModel.estadoGuardado.collectAsState()
    val mensajeError by viewModel.mensajeError.collectAsState()



    LaunchedEffect(productoId) { viewModel.cargarProducto(productoId) }
    LaunchedEffect(estadoGuardado) {
        if (estadoGuardado == true) { onMovimientoExitoso(); viewModel.resetEstado() }
    }

    val colorAccion = when {
        tipo.esPositivo && tipoMovimientoId == 1 -> VerdeEntrada
        !tipo.esPositivo && tipoMovimientoId == 2 -> RojoSalida
        else -> NaranjaAjuste
    }

    val tituloOperacion = when (tipoMovimientoId) {
        1 -> "Registrar ENTRADA"
        2 -> "Registrar SALIDA"
        3 -> "Registrar AJUSTE POSITIVO"
        4 -> "Registrar AJUSTE NEGATIVO"
        else -> "Registrar Movimiento"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(tituloOperacion) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorAccion,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        if (producto == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val prod = producto!!
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Tarjeta informativa del producto
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(prod.nombre, style = MaterialTheme.typography.titleLarge)
                        Text(
                            "Stock actual: ${prod.stockActual} ${prod.unidadMedida}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = viewModel.cantidadInput,
                    onValueChange = { viewModel.cantidadInput = it },
                    label = { Text("Cantidad *") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = viewModel.referenciaInput,
                    onValueChange = { viewModel.referenciaInput = it },
                    label = { Text("Referencia (albarán, pedido…)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = viewModel.observacionesInput,
                    onValueChange = { viewModel.observacionesInput = it },
                    label = {
                        Text(if (tipoMovimientoId >= 3) "Motivo del ajuste *" else "Observaciones")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                if (mensajeError != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = mensajeError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(Modifier.weight(1f))

                val esAjuste    = tipoMovimientoId >= 3
                val camposValidos = viewModel.cantidadInput.isNotEmpty() &&
                        (!esAjuste || viewModel.observacionesInput.isNotEmpty())

                Button(
                    onClick = { viewModel.registrar(tipo, usuarioId) },
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    shape = MaterialTheme.shapes.small,
                    colors = ButtonDefaults.buttonColors(containerColor = colorAccion),
                    enabled = camposValidos
                ) {
                    Text(
                        when (tipoMovimientoId) {
                            1 -> "CONFIRMAR ENTRADA"
                            2 -> "CONFIRMAR SALIDA"
                            3 -> "CONFIRMAR AJUSTE +"
                            4 -> "CONFIRMAR AJUSTE -"
                            else -> "CONFIRMAR"
                        },
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        }
    }
}