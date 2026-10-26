package com.ucompensar.eldesvare.ui.theme.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ucompensar.eldesvare.ui.theme.ElDesvareTheme
import com.ucompensar.eldesvare.viewmodel.DesvareViewModel

@Composable
fun OpcionesScreen(
    viewModel: DesvareViewModel,
    onVolver: () -> Unit
) {
    val temaActual by viewModel.temaPreferencia.collectAsState()

    OpcionesContent(
        temaActual = temaActual,
        onCambiarTema = { nuevoTema -> viewModel.cambiarTema(nuevoTema) },
        onVolver = onVolver
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpcionesContent(
    temaActual: Int,
    onCambiarTema: (Int) -> Unit = {},
    onVolver: () -> Unit = {}
) {
    var notificacionesActivas by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Apariencia",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.selectableGroup()) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ColorLens, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Tema de la aplicación", fontWeight = FontWeight.SemiBold)
                    }
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                    TemaOpcionItem(
                        texto = "Automático (Sensor de luz)",
                        icono = Icons.Default.BrightnessAuto,
                        seleccionado = temaActual == 0,
                        onClick = { onCambiarTema(0) }
                    )
                    TemaOpcionItem(
                        texto = "Modo Claro",
                        icono = Icons.Default.LightMode,
                        seleccionado = temaActual == 1,
                        onClick = { onCambiarTema(1) }
                    )
                    TemaOpcionItem(
                        texto = "Modo Oscuro",
                        icono = Icons.Default.DarkMode,
                        seleccionado = temaActual == 2,
                        onClick = { onCambiarTema(2) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Preferencias",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Alertas de talleres", fontWeight = FontWeight.SemiBold)
                            Text("Notificar lugares cercanos", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    Switch(
                        checked = notificacionesActivas,
                        onCheckedChange = { notificacionesActivas = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("El Desvare v1.0", fontWeight = FontWeight.SemiBold)
                        Text("Proyecto UCompensar - Oscar, Andres, Stiven", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
fun TemaOpcionItem(
    texto: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = seleccionado,
                onClick = onClick,
                role = Role.RadioButton
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icono, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = texto, modifier = Modifier.weight(1f))
        RadioButton(
            selected = seleccionado,
            onClick = null
        )
    }
}

@Preview(showBackground = true, name = "Opciones / Configuración - Modo Claro")
@Composable
fun OpcionesScreenLightPreview() {
    ElDesvareTheme {
        OpcionesContent(temaActual = 0)
    }
}

@Preview(showBackground = true, name = "Opciones / Configuración - Modo Oscuro")
@Composable
fun OpcionesScreenDarkPreview() {
    ElDesvareTheme(darkTheme = true) {
        OpcionesContent(temaActual = 2)
    }
}

@Preview(showBackground = true, name = "Componente - Tema Opción Seleccionado")
@Composable
fun TemaOpcionItemSeleccionadoPreview() {
    ElDesvareTheme {
        TemaOpcionItem(
            texto = "Automático (Sensor de luz)",
            icono = Icons.Default.BrightnessAuto,
            seleccionado = true,
            onClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Componente - Tema Opción No Seleccionado")
@Composable
fun TemaOpcionItemNoSeleccionadoPreview() {
    ElDesvareTheme {
        TemaOpcionItem(
            texto = "Modo Oscuro",
            icono = Icons.Default.DarkMode,
            seleccionado = false,
            onClick = {}
        )
    }
}