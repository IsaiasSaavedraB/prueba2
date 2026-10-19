package com.example.agencia.ui.usuarios

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.*
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun UsuariosScreen(
    modifier: Modifier = Modifier,
    viewModel: UsuariosViewModel = viewModel()
) {
    val usuarios by viewModel.usuarios.collectAsStateWithLifecycle()
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Usuarios registrados",
            style = MaterialTheme.typography.headlineMedium
        )
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            items(usuarios) { usuario ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "${usuario.nombre} ${usuario.apellido}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = usuario.correo
                        )
                    }
                }

            }
        }
    }
}
