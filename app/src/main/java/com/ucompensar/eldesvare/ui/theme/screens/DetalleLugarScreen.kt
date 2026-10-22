package com.ucompensar.eldesvare.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ucompensar.eldesvare.viewmodel.DesvareViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleLugarScreen(
    lugarId: Int,
    viewModel: DesvareViewModel,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val lugar = uiState.lugares.find { it.id == lugarId }

    if (lugar == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Lugar no encontrado")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Lugar") },
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
            if (!lugar.fotoUri.isNullOrBlank()) {
                AsyncImage(
                    model = Uri.parse(lugar.fotoUri),
                    contentDescription = "Fachada de ${lugar.nombre}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = if (lugar.tipo.name == "TALLER") Icons.Default.Build else Icons.Default.LocalParking,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Fachada de ${lugar.tipo.etiqueta}")
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = lugar.nombre,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                BadgeVerificacion(verificado = lugar.verificado)
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Dirección: ${lugar.direccion}", fontWeight = FontWeight.Medium)
                    Text("Ruta de referencia: ${lugar.rutaReferencia}")
                    Text("Horario: ${lugar.horario}")
                    Text("Teléfono: ${lugar.telefono}")
                    Text("Vehículos que atiende: ${lugar.vehiculosAtendidos.joinToString { it.etiqueta }}")
                    Text("Recomendado por: ${lugar.recomendadoPor}")
                    HorizontalDivider()
                    Text(
                        text = "Notas y servicios:",
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = lugar.notas)
                }
            }

            Text(
                text = "Acciones de emergencia inmediata",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:${lugar.telefono}")
                        }
                        context.startActivity(intent)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Call, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Llamar")
                }

                Button(
                    onClick = {
                        val uriRuta = Uri.parse("google.navigation:q=${lugar.latitud},${lugar.longitud}")
                        val mapIntent = Intent(Intent.ACTION_VIEW, uriRuta).apply {
                            setPackage("com.google.android.apps.maps")
                        }
                        if (mapIntent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(mapIntent)
                        } else {
                            val browserIntent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${lugar.latitud},${lugar.longitud}")
                            )
                            context.startActivity(browserIntent)
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Directions, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Ir (Ruta)")
                }
            }

            OutlinedButton(
                onClick = {
                    val textoCompartir = """
                        Te comparto este punto desde la app El Desvare (UCompensar):
                        ${lugar.nombre} (${lugar.tipo.etiqueta})
                        Dirección: ${lugar.direccion}
                        Teléfono: ${lugar.telefono}
                        Mapa: https://maps.google.com/?q=${lugar.latitud},${lugar.longitud}
                    """.trimIndent()

                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, textoCompartir)
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(sendIntent, "Compartir ubicación"))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Share, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Compartir ubicación y contacto")
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = {
                    viewModel.reportarLugarFalso(lugar.id)
                    Toast.makeText(
                        context,
                        "Lugar reportado. Se ha ocultado del mapa por seguridad.",
                        Toast.LENGTH_LONG
                    ).show()
                    onVolver()
                },
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(Icons.Default.ReportProblem, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Reportar lugar falso o cerrado")
            }
        }
    }
}