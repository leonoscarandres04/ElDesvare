package com.ucompensar.eldesvare.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.ucompensar.eldesvare.model.Lugar
import com.ucompensar.eldesvare.model.TipoLugar
import com.ucompensar.eldesvare.model.TipoVehiculo
import com.ucompensar.eldesvare.sensors.BrujulaYDistanciaHelper
import com.ucompensar.eldesvare.viewmodel.DesvareViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun MapaScreen(
    viewModel: DesvareViewModel,
    onIrADetalle: (Int) -> Unit,
    onIrARecomendar: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val lugaresFiltrados = remember(uiState) {
        viewModel.obtenerLugaresFiltradosYOrdenados(uiState)
    }

    var modoVista by remember { mutableIntStateOf(0) }

    val azimutDispositivo by BrujulaYDistanciaHelper.observarAzimutBrujula(context)
        .collectAsState(initial = 0f)

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val launcherPermisoGps = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permisos ->
        val concedido = permisos[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permisos[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (concedido) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    viewModel.actualizarUbicacionUsuario(it.latitude, it.longitude)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        val tienePermiso = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (tienePermiso) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    viewModel.actualizarUbicacionUsuario(it.latitude, it.longitude)
                }
            }
        } else {
            launcherPermisoGps.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("El Desvare - UCompensar", fontWeight = FontWeight.Bold)
                        Text(
                            "Talleres y parqueaderos en tu ruta",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onIrARecomendar,
                icon = { Icon(Icons.Default.AddLocationAlt, contentDescription = null) },
                text = { Text("Recomendar lugar") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(selectedTabIndex = modoVista) {
                Tab(
                    selected = modoVista == 0,
                    onClick = { modoVista = 0 },
                    text = { Text("Rutas y Lista") },
                    icon = { Icon(Icons.Default.List, contentDescription = null) }
                )
                Tab(
                    selected = modoVista == 1,
                    onClick = { modoVista = 1 },
                    text = { Text("Mapa") },
                    icon = { Icon(Icons.Default.Map, contentDescription = null) }
                )
                Tab(
                    selected = modoVista == 2,
                    onClick = { modoVista = 2 },
                    text = { Text("Brújula Offline") },
                    icon = { Icon(Icons.Default.Explore, contentDescription = null) }
                )
            }

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = uiState.filtroTipoLugar == null,
                        onClick = { viewModel.seleccionarFiltroTipo(null) },
                        label = { Text("Todos") }
                    )
                }
                items(TipoLugar.entries) { tipo ->
                    FilterChip(
                        selected = uiState.filtroTipoLugar == tipo,
                        onClick = {
                            viewModel.seleccionarFiltroTipo(
                                if (uiState.filtroTipoLugar == tipo) null else tipo
                            )
                        },
                        label = { Text(tipo.etiqueta) }
                    )
                }
                items(TipoVehiculo.entries) { vehiculo ->
                    FilterChip(
                        selected = uiState.filtroVehiculo == vehiculo,
                        onClick = {
                            viewModel.seleccionarFiltroVehiculo(
                                if (uiState.filtroVehiculo == vehiculo) null else vehiculo
                            )
                        },
                        label = { Text(vehiculo.etiqueta) }
                    )
                }
                item {
                    FilterChip(
                        selected = uiState.soloVerificados,
                        onClick = { viewModel.alternarSoloVerificados() },
                        label = { Text("Solo Verificados") }
                    )
                }
            }

            lugaresFiltrados.firstOrNull()?.let { masCercano ->
                val distanciaMasCercano = BrujulaYDistanciaHelper.calcularDistanciaMetros(
                    uiState.latitudUsuario,
                    uiState.longitudUsuario,
                    masCercano.latitud,
                    masCercano.longitud
                )
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                        .clickable { onIrADetalle(masCercano.id) },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "MÁS CERCANO EN TU RUTA (${BrujulaYDistanciaHelper.formatearDistancia(distanciaMasCercano)})",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = masCercano.nombre,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${masCercano.direccion} • ${masCercano.rutaReferencia}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        BadgeVerificacion(verificado = masCercano.verificado)
                    }
                }
            }

            when (modoVista) {
                0 -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(lugaresFiltrados) { lugar ->
                            val distancia = BrujulaYDistanciaHelper.calcularDistanciaMetros(
                                uiState.latitudUsuario,
                                uiState.longitudUsuario,
                                lugar.latitud,
                                lugar.longitud
                            )
                            TarjetaLugarItem(
                                lugar = lugar,
                                distanciaTexto = BrujulaYDistanciaHelper.formatearDistancia(distancia),
                                onClick = { onIrADetalle(lugar.id) },
                                onUsarBrujula = {
                                    viewModel.seleccionarLugarParaBrujula(lugar)
                                    modoVista = 2
                                }
                            )
                        }
                    }
                }

                1 -> {
                    val cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(
                            LatLng(uiState.latitudUsuario, uiState.longitudUsuario),
                            14f
                        )
                    }
                    Box(modifier = Modifier.fillMaxSize()) {
                        GoogleMap(
                            modifier = Modifier.fillMaxSize(),
                            cameraPositionState = cameraPositionState
                        ) {
                            lugaresFiltrados.forEach { lugar ->
                                Marker(
                                    state = MarkerState(
                                        position = LatLng(lugar.latitud, lugar.longitud)
                                    ),
                                    title = lugar.nombre,
                                    snippet = "${lugar.tipo.etiqueta} - ${if (lugar.verificado) "Verificado" else "Sin verificar"}",
                                    onInfoWindowClick = { onIrADetalle(lugar.id) }
                                )
                            }
                        }
                    }
                }

                2 -> {
                    val destino = uiState.lugarGuiaBrujula ?: lugaresFiltrados.firstOrNull()
                    if (destino != null) {
                        val distancia = BrujulaYDistanciaHelper.calcularDistanciaMetros(
                            uiState.latitudUsuario,
                            uiState.longitudUsuario,
                            destino.latitud,
                            destino.longitud
                        )
                        val rumboDestino = BrujulaYDistanciaHelper.calcularRumboHaciaDestino(
                            uiState.latitudUsuario,
                            uiState.longitudUsuario,
                            destino.latitud,
                            destino.longitud
                        )
                        val rotacionFlecha = (rumboDestino - azimutDispositivo + 360f) % 360f

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "GUÍA SIN CONEXIÓN (BRÚJULA)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = destino.nombre,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = destino.direccion,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(28.dp))

                            Box(
                                modifier = Modifier
                                    .size(180.dp)
                                    .background(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Navigation,
                                    contentDescription = "Flecha de brújula",
                                    modifier = Modifier
                                        .size(110.dp)
                                        .rotate(rotacionFlecha),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = BrujulaYDistanciaHelper.formatearDistancia(distancia),
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Funciona sin internet usando puntos guardados y el magnetómetro.",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { onIrADetalle(destino.id) }) {
                                Text("Ver teléfono y detalles del lugar")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaLugarItem(
    lugar: Lugar,
    distanciaTexto: String,
    onClick: () -> Unit,
    onUsarBrujula: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${lugar.tipo.etiqueta.uppercase()} • $distanciaTexto",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                BadgeVerificacion(verificado = lugar.verificado)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = lugar.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${lugar.direccion} (${lugar.rutaReferencia})",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Atiende: ${lugar.vehiculosAtendidos.joinToString { it.etiqueta }}",
                    style = MaterialTheme.typography.bodySmall
                )
                TextButton(onClick = onUsarBrujula) {
                    Icon(
                        Icons.Default.Explore,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Guiar con brújula")
                }
            }
        }
    }
}

@Composable
fun BadgeVerificacion(verificado: Boolean) {
    Surface(
        color = if (verificado) Color(0xFFDFF6DD) else Color(0xFFFFF4CE),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (verificado) Icons.Default.Verified else Icons.Default.Info,
                contentDescription = null,
                tint = if (verificado) Color(0xFF107C10) else Color(0xFF797775),
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = if (verificado) "Verificado" else "Sin verificar",
                style = MaterialTheme.typography.labelSmall,
                color = if (verificado) Color(0xFF107C10) else Color(0xFF797775),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}