package com.example.gestionalmacenpda.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gestionalmacenpda.domain.model.Usuario

@Composable
fun PantallaLogin(viewModel: LoginViewModel = hiltViewModel(), onLoginSuccess: (Usuario) -> Unit)
{
    var usuario by remember { mutableStateOf("") }
    var password by remember{ mutableStateOf("") }
    val estadoPantalla by viewModel.estadoPantalla.collectAsState()

    LaunchedEffect(estadoPantalla)
    {
        if (estadoPantalla is EstadoLogin.Success)
        { onLoginSuccess((estadoPantalla as EstadoLogin.Success).usuario)}
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Inicio de Sesión",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(30.dp))
        OutlinedTextField(
            value = usuario,
            onValueChange = { usuario = it },
            label = { Text("Usuario") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(20.dp))



        // Gestion de los estados visuales de la pantalla
        when(estadoPantalla) {
            is EstadoLogin.Cargando -> CircularProgressIndicator()
            else -> {
                Button (
                    onClick = { viewModel.realizarLogin(usuario, password) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = MaterialTheme.shapes.medium
                ) {Text("Entrar")}
            }
        }

        if (estadoPantalla is EstadoLogin.Error)
        {
            Text(
                text = (estadoPantalla as EstadoLogin.Error).mensaje,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 10.dp)
            )
        }
    }
}