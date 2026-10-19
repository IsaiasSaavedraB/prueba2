package com.example.agencia.ui.registro

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun RegistroScreen(
    modifier: Modifier = Modifier,
    viewModel: RegistroViewModel = viewModel()
) {
    // variable para mantener la sincronizacion de los estados
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Crear cuenta",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = uiState.nombre,
            onValueChange = {
                viewModel.cambiarNombre(it)
            },
            label = {
                Text("Nombre")
            },
            isError = uiState.errorNombre,
            supportingText = {
                if (uiState.errorNombre) {
                    Text("El nombre es obligatorio")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = uiState.apellido,
            onValueChange = {
                viewModel.cambiarApellido(it)
            },
            label = {
                Text("Apellido")
            },
            isError = uiState.errorApellido,
            supportingText = {
                if (uiState.errorApellido) {
                    Text("El apellido es obligatorio")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = uiState.correo,
            onValueChange = {
                viewModel.cambiarCorreo(it)
            },
            label = {
                Text("Correo")
            },
            isError = uiState.errorCorreo,
            supportingText = {
                if (uiState.errorCorreo) {
                    Text("Ingrese un correo válido terminado en .cl")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = uiState.password,
            onValueChange = {
                viewModel.cambiarPassword(it)
            },
            label = {
                Text("Contraseña")
            },
            visualTransformation = PasswordVisualTransformation(),
            isError = uiState.errorPassword,
            supportingText = {
                if (uiState.errorPassword) {
                    Text("Debe contener al menos 8 caracteres")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = uiState.repetirPassword,
            onValueChange = {
                viewModel.cambiarRepetirPassword(it)
            },
            label = {
                Text("Repetir contraseña")
            },
            visualTransformation = PasswordVisualTransformation(),
            isError = uiState.errorRepetirPassword,
            supportingText = {
                if (uiState.errorRepetirPassword) {
                    Text("Las contraseñas no coinciden")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Checkbox(
                checked = uiState.aceptaTerminos,
                onCheckedChange = {
                    viewModel.cambiarTerminos(it)
                }
            )

            Text("Acepto los términos y condiciones")
        }

        if (uiState.errorTerminos) {
            Text(
                text = "Debe aceptar los términos y condiciones",
                color = MaterialTheme.colorScheme.error
            )
        }

        Button(
            onClick = {
                viewModel.registrar()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrarse")
        }
    }
}