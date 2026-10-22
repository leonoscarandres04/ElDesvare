package com.ucompensar.eldesvare.ui.theme.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ucompensar.eldesvare.model.TipoLugar
import com.ucompensar.eldesvare.model.TipoVehiculo
import com.ucompensar.eldesvare.viewmodel.DesvareViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecomendarLugarScreen(
    viewModel: DesvareViewModel,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var nombre by remember { mutableStateOf("") }
    var tipoSeleccionado by remember { mutableStateOf(TipoLugar.TALLER) }
    var carrosSeleccionado by remember { mutableStateOf(true) }
    var motosSeleccionado by remember { mutableStateOf(true) }
    var bicisSeleccionado by remember { mutableStateOf(false) }

    var direccion by remember { mutableStateOf("") }
    var horario by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var rutaReferencia by remember { mutableStateOf("Ruta Av. 68 hacia UCompensar") }
    var recomendadoPor by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }
    var latitudTexto by remember { mutableStateOf(uiState.latitudUsuario.toString()) }
    var longitudTexto by remember { mutableStateOf(uiState.longitudUsuario.toString()) }
    var fotoUriSeleccionada by remember { mutableStateOf<Uri?>(null) }

    val selectorImagenLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        fotoUriSeleccionada = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recomendar un Lugar") },
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Comparte un taller o parqueadero de barrio que no esté en Maps. Se publicará con estado 'Sin verificar' hasta que el equipo lo valide.",
                style = MaterialTheme.typography.bodySmall
            )

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del taller o parqueadero *") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Tipo de lugar:", fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TipoLugar.entries.forEach { tipo ->
                    FilterChip(
                        selected = tipoSeleccionado == tipo,
                        onClick = { tipoSeleccionado = tipo },
                        label = { Text(tipo.etiqueta) }
                    )
                }
            }

            Text("Vehículos que atiende:", fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = carrosSeleccionado,
                    onClick = { carrosSeleccionado = !carrosSeleccionado },
                    label = { Text("Carro") }
                )
                FilterChip(
                    selected = motosSeleccionado,
                    onClick = { motosSeleccionado = !motosSeleccionado },
                    label = { Text("Moto") }
                )
                FilterChip(
                    selected = bicisSeleccionado,
                    onClick = { bicisSeleccionado = !bicisSeleccionado },
                    label = { Text("Bicicleta") }
                )
            }

            OutlinedTextField(
                value = direccion,
                onValueChange = { direccion = it },
                label = { Text("Dirección o punto de referencia *") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    label = { Text("Teléfono / Celular *") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = horario,
                    onValueChange = { horario = it },
                    label = { Text("Horario") },
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = rutaReferencia,
                onValueChange = { rutaReferencia = it },
                label = { Text("Ruta hacia la universidad (Ej. Av. 68, Calle 32)") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = latitudTexto,
                    onValueChange = { latitudTexto = it },
                    label = { Text("Latitud GPS") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = longitudTexto,
                    onValueChange = { longitudTexto = it },
                    label = { Text("Longitud GPS") },
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedButton(
                onClick = {
                    latitudTexto = uiState.latitudUsuario.toString()
                    longitudTexto = uiState.longitudUsuario.toString()
                    Toast.makeText(context, "Ubicación GPS actual cargada", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Usar mi ubicación GPS actual")
            }

            OutlinedButton(
                onClick = { selectorImagenLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    if (fotoUriSeleccionada != null) "Foto de fachada adjuntada ✓"
                    else "Adjuntar foto de la fachada (Cámara / Galería)"
                )
            }

            OutlinedTextField(
                value = recomendadoPor,
                onValueChange = { recomendadoPor = it },
                label = { Text("Tu nombre (quien recomienda)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = notas,
                onValueChange = { notas = it },
                label = { Text("Notas (precios, qué arreglan, seguridad, etc.) *") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Button(
                onClick = {
                    val vehiculos = mutableListOf<TipoVehiculo>().apply {
                        if (carrosSeleccionado) add(TipoVehiculo.CARRO)
                        if (motosSeleccionado) add(TipoVehiculo.MOTO)
                        if (bicisSeleccionado) add(TipoVehiculo.BICICLETA)
                    }

                    if (nombre.isBlank() || direccion.isBlank() || telefono.isBlank() || vehiculos.isEmpty()) {
                        Toast.makeText(
                            context,
                            "Por favor completa nombre, dirección, teléfono y al menos un vehículo",
                            Toast.LENGTH_LONG
                        ).show()
                        return@Button
                    }

                    viewModel.agregarLugarRecomendado(
                        nombre = nombre,
                        tipo = tipoSeleccionado,
                        vehiculos = vehiculos,
                        direccion = direccion,
                        horario = horario,
                        telefono = telefono,
                        latitud = latitudTexto.toDoubleOrNull() ?: uiState.latitudUsuario,
                        longitud = longitudTexto.toDoubleOrNull() ?: uiState.longitudUsuario,
                        rutaReferencia = rutaReferencia,
                        recomendadoPor = recomendadoPor,
                        notas = notas.ifBlank { "Recomendado por la comunidad universitaria." },
                        fotoUri = fotoUriSeleccionada?.toString()
                    )

                    Toast.makeText(
                        context,
                        "¡Lugar guardado como 'Sin verificar' y compartido en el mapa!",
                        Toast.LENGTH_LONG
                    ).show()
                    onVolver()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Publicar recomendación en el mapa")
            }
        }
    }
}