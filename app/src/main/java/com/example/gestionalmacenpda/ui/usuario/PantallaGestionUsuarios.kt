package com.example.gestionalmacenpda.ui.usuario

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gestionalmacenpda.domain.model.Usuario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaGestionUsuarios(
    usuarioActualId: Int,
    viewModel: GestionUsuariosViewModel = hiltViewModel(),
    onVolver: () -> Unit
) {
    val usuarios by viewModel.usuarios.collectAsState()
    val roles by viewModel.roles.collectAsState()
    val mensajeError by viewModel.mensajeError.collectAsState()
    val usuarioCreado by viewModel.usuarioCreado.collectAsState()

    var mostrarDialogoCrear by remember { mutableStateOf(false) }
    var usuarioADesactivar by remember { mutableStateOf<Usuario?>(null) }

    // Cerramos el diálogo al crearlo correctamente
    LaunchedEffect(usuarioCreado) {
        if (usuarioCreado) {
            mostrarDialogoCrear = false
            viewModel.limpiarUsuarioCreado()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Usuarios") },
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
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialogoCrear = true }) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Nuevo usuario")
            }
        }
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

            if (usuarios.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay usuarios registrados",
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text("${usuarios.size} usuario(s)",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 4.dp))
                    }
                    items(usuarios, key = { it.id }) { usuario ->
                        CardUsuario(
                            usuario = usuario,
                            roles  = roles,
                            esMiCuenta = usuario.id == usuarioActualId,
                            onActivar = { viewModel.activar(usuario) },
                            onDesactivar = { usuarioADesactivar = usuario }
                        )
                    }
                    item { Spacer(Modifier.height(72.dp)) }
                }
            }
        }
    }

    // Diálogo para crear un nuevo usuario
    if (mostrarDialogoCrear) {
        var passwordVisible by remember { mutableStateOf(false) }
        var expandirRol by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = {
                mostrarDialogoCrear = false
                viewModel.limpiarError()
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PersonAdd, null,
                        tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(8.dp))
                    Text("Nuevo usuario")
                }
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Username
                    OutlinedTextField(
                        value = viewModel.nuevoUsername,
                        onValueChange = { viewModel.nuevoUsername = it },
                        label = { Text("Nombre de usuario *") },
                        leadingIcon = { Icon(Icons.Default.Person, null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Contraseña con toggle de visibilidad
                    OutlinedTextField(
                        value = viewModel.nuevoPassword,
                        onValueChange = { viewModel.nuevoPassword = it },
                        label = { Text("Contraseña * (mín. 4 caracteres)") },
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Default.VisibilityOff
                                    else Icons.Default.Visibility,
                                    contentDescription = if (passwordVisible) "Ocultar" else "Mostrar"
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None
                        else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Nombre completo
                    OutlinedTextField(
                        value = viewModel.nuevoNombre,
                        onValueChange = { viewModel.nuevoNombre = it },
                        label = { Text("Nombre completo *") },
                        leadingIcon = { Icon(Icons.Default.Badge, null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Picker de rol
                    ExposedDropdownMenuBox(
                        expanded = expandirRol,
                        onExpandedChange = { expandirRol = it }
                    ) {
                        OutlinedTextField(
                            value = viewModel.rolSeleccionado?.nombre ?: "Selecciona un rol *",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Rol") },
                            leadingIcon = { Icon(Icons.Default.AdminPanelSettings, null) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandirRol) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expandirRol,
                            onDismissRequest = { expandirRol = false }
                        ) {
                            roles.forEach { rol ->
                                DropdownMenuItem(
                                    text = { Text(rol.nombre) },
                                    onClick = {
                                        viewModel.rolSeleccionado = rol
                                        expandirRol = false
                                    }
                                )
                            }
                        }
                    }

                    // Nota informativa
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Info, null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "El usuario se creará como activo. La contraseña se guarda de forma segura (hash SHA-256).",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Error inline en el diálogo
                    mensajeError?.let {
                        Text(it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.crearUsuario() },
                    enabled = viewModel.nuevoUsername.isNotBlank()
                            && viewModel.nuevoPassword.isNotBlank()
                            && viewModel.nuevoNombre.isNotBlank()
                            && viewModel.rolSeleccionado != null
                ) { Text("CREAR") }
            },
            dismissButton = {
                TextButton(onClick = {
                    mostrarDialogoCrear = false
                    viewModel.limpiarError()
                }) { Text("CANCELAR") }
            }
        )
    }

    // Diálogo para confirmar la desactivación
    usuarioADesactivar?.let { u ->
        AlertDialog(
            onDismissRequest = { usuarioADesactivar = null },
            icon = { Icon(Icons.Default.PersonOff, null,
                tint = MaterialTheme.colorScheme.error) },
            title = { Text("Desactivar usuario") },
            text = {
                Text("¿Desactivar a \"${u.nombre_completo}\"? No podrá iniciar sesión hasta que lo vuelvas a activar.")
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.desactivar(u, usuarioActualId)
                    usuarioADesactivar = null
                }) { Text("DESACTIVAR", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { usuarioADesactivar = null }) { Text("CANCELAR") }
            }
        )
    }
}

// Tarjeta de usuario
@Composable
fun CardUsuario(
    usuario: Usuario,
    roles: List<com.example.gestionalmacenpda.domain.model.Rol>,
    esMiCuenta: Boolean,
    onActivar: () -> Unit,
    onDesactivar: () -> Unit
) {
    val esActivo = usuario.activo == 1
    val rolNombre = roles.find { it.id == usuario.rol_id }?.nombre ?: "Rol ${usuario.rol_id}"

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (esActivo) MaterialTheme.colorScheme.surfaceVariant
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (esActivo) Icons.Default.Person else Icons.Default.PersonOff,
                contentDescription = null,
                tint = if (esActivo) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(usuario.nombre_completo,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (esActivo) MaterialTheme.colorScheme.onSurface
                        else MaterialTheme.colorScheme.outline)
                    if (esMiCuenta) {
                        Spacer(Modifier.width(6.dp))
                        Surface(shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.primaryContainer) {
                            Text("TÚ",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Text("@${usuario.username}  ·  $rolNombre",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (!esActivo) {
                    Text("INACTIVO",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold)
                }
            }
            Switch(
                checked = esActivo,
                onCheckedChange = { nuevo ->
                    if (!esMiCuenta) { if (nuevo) onActivar() else onDesactivar() }
                },
                enabled = !esMiCuenta,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    }
}