package com.ucompensar.eldesvare.ui.theme.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.EditLocationAlt
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.ucompensar.eldesvare.data.DireccionesHelper
import com.ucompensar.eldesvare.model.DatosIniciales
import com.ucompensar.eldesvare.model.TipoLugar
import com.ucompensar.eldesvare.model.TipoVehiculo
import com.ucompensar.eldesvare.ui.theme.ElDesvareTheme
import com.ucompensar.eldesvare.ui.theme.NaranjaDesvare
import com.ucompensar.eldesvare.viewmodel.DesvareViewModel
import com.ucompensar.eldesvare.viewmodel.LugarNuevo
import kotlinx.coroutines.launch

/** Horas cada media hora, de "12:00 AM" a "11:30 PM". */
private val HORAS: List<String> = (0 until 48).map { i ->
    val hora = i / 2
    val minutos = if (i % 2 == 0) "00" else "30"
    val hora12 = if (hora % 12 == 0) 12 else hora % 12
    "$hora12:$minutos${if (hora < 12) "AM" else "PM"}"
}

private val DIAS = listOf("Lunes a Viernes", "Lunes a Sábado", "Todos los días", "Fines de semana")

private const val DIGITOS_TELEFONO = 10

@Composable
fun RecomendarLugarScreen(
    viewModel: DesvareViewModel,
    nombreUsuario: String = "",
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var guardando by remember { mutableStateOf(false) }

    RecomendarLugarContent(
        latitudUsuario = uiState.latitudUsuario,
        longitudUsuario = uiState.longitudUsuario,
        nombreUsuario = nombreUsuario,
        guardando = guardando,
        onVolver = onVolver,
        onGuardarLugar = { lugarNuevo ->
            guardando = true
            viewModel.agregarLugarRecomendado(lugarNuevo) { resultado ->
                guardando = false
                resultado
                    .onSuccess { quedoEnLaNube ->
                        val mensaje = when {
                            !quedoEnLaNube -> "Sin internet: el lugar se subirá solo cuando vuelva la conexión"
                            lugarNuevo.publico -> "¡Listo! El lugar quedó publicado como 'Sin verificar'"
                            else -> "¡Listo! El lugar quedó guardado solo para ti"
                        }
                        Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
                        onVolver()
                    }
                    .onFailure { error ->
                        Toast.makeText(
                            context,
                            error.message ?: "No se pudo guardar el lugar, intenta de nuevo",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RecomendarLugarContent(
    latitudUsuario: Double = DatosIniciales.LAT_UCOMPENSAR,
    longitudUsuario: Double = DatosIniciales.LNG_UCOMPENSAR,
    nombreUsuario: String = "",
    guardando: Boolean = false,
    onVolver: () -> Unit = {},
    onGuardarLugar: (LugarNuevo) -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var publico by rememberSaveable { mutableStateOf(true) }
    var nombre by rememberSaveable { mutableStateOf("") }
    var tipoSeleccionado by rememberSaveable { mutableStateOf(TipoLugar.TALLER) }
    var carrosSeleccionado by rememberSaveable { mutableStateOf(true) }
    var motosSeleccionado by rememberSaveable { mutableStateOf(true) }
    var bicisSeleccionado by rememberSaveable { mutableStateOf(false) }

    var latitud by rememberSaveable { mutableStateOf(latitudUsuario) }
    var longitud by rememberSaveable { mutableStateOf(longitudUsuario) }
    var direccion by rememberSaveable { mutableStateOf("") }
    var direccionAutomatica by rememberSaveable { mutableStateOf("") }
    var mostrarSelector by rememberSaveable { mutableStateOf(false) }

    var telefono by rememberSaveable { mutableStateOf("") }
    var abierto24h by rememberSaveable { mutableStateOf(false) }
    var dias by rememberSaveable { mutableStateOf("Lunes a Sábado") }
    var horaAbre by rememberSaveable { mutableStateOf("7:00 AM") }
    var horaCierra by rememberSaveable { mutableStateOf("6:00 PM") }

    var rutaReferencia by rememberSaveable { mutableStateOf("Ruta Av. 68 hacia UCompensar") }
    var recomendadoPor by rememberSaveable { mutableStateOf(nombreUsuario) }
    var notas by rememberSaveable { mutableStateOf("") }
    var fotoUri by rememberSaveable { mutableStateOf<String?>(null) }
    var intentoEnviar by rememberSaveable { mutableStateOf(false) }

    val punto = LatLng(latitud, longitud)

    val usarPunto = { nuevoPunto: LatLng, direccionEncontrada: String? ->
        latitud = nuevoPunto.latitude
        longitud = nuevoPunto.longitude
        if (direccionEncontrada != null && (direccion.isBlank() || direccion == direccionAutomatica)) {
            direccion = direccionEncontrada
            direccionAutomatica = direccionEncontrada
        }
    }

    val usarMiUbicacion = rememberPedirUbicacionActual(
        onUbicacion = { nuevoPunto ->
            scope.launch { usarPunto(nuevoPunto, DireccionesHelper.direccionDe(context, nuevoPunto)) }
        },
        onSinUbicacion = {
            Toast.makeText(context, "No pudimos obtener tu ubicación. Revisa que el GPS esté prendido.", Toast.LENGTH_LONG).show()
        }
    )

    val selectorImagenLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) fotoUri = uri.toString()
    }

    val horario = if (abierto24h) "Abierto 24 horas, todos los días" else "$dias: $horaAbre -$horaCierra"

    val errorNombre = if (intentoEnviar && nombre.isBlank()) "Escribe el nombre del lugar" else null
    val errorDireccion = if (intentoEnviar && direccion.isBlank()) "Escribe la dirección o elígela en el mapa" else null
    val errorTelefono = when {
        intentoEnviar && telefono.isEmpty() -> "Escribe el teléfono"
        intentoEnviar && telefono.length != DIGITOS_TELEFONO -> "El número debe tener $DIGITOS_TELEFONO dígitos"
        else -> null
    }
    val errorVehiculos = intentoEnviar && !carrosSeleccionado && !motosSeleccionado && !bicisSeleccionado
    val errorHorario = !abierto24h && horaAbre == horaCierra

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recomendar un lugar", fontWeight = FontWeight.Bold) },
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Comparte un taller o parqueadero de barrio que no esté en Maps.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            SeccionFormulario(titulo = "¿Quién lo puede ver?") {
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = publico,
                        onClick = { publico = true },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                        colors = coloresSegmento(),
                        icon = { Icon(Icons.Default.Public, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    ) { Text("Público") }
                    SegmentedButton(
                        selected = !publico,
                        onClick = { publico = false },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                        colors = coloresSegmento(),
                        icon = { Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    ) { Text("Privado") }
                }
                Text(
                    text = if (publico) {
                        "Todos los conductores lo verán en el mapa como \"Sin verificar\"."
                    } else {
                        "Solo tú lo verás en tu mapa. Nadie más puede verlo."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            SeccionFormulario(titulo = "El lugar") {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del taller o parqueadero *") },
                    leadingIcon = { Icon(Icons.Default.Storefront, contentDescription = null) },
                    isError = errorNombre != null,
                    supportingText = errorNombre?.let { { Text(it) } },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = coloresCampo(),
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Tipo de lugar", style = MaterialTheme.typography.labelLarge)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TipoLugar.entries.forEach { tipo ->
                        ChipFiltro(
                            texto = tipo.etiqueta,
                            icono = iconoTipoLugar(tipo),
                            seleccionado = tipoSeleccionado == tipo,
                            onClick = { tipoSeleccionado = tipo }
                        )
                    }
                }

                Text("Vehículos que atiende *", style = MaterialTheme.typography.labelLarge)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ChipFiltro("Carro", iconoVehiculo(TipoVehiculo.CARRO), carrosSeleccionado) { carrosSeleccionado = !carrosSeleccionado }
                    ChipFiltro("Moto", iconoVehiculo(TipoVehiculo.MOTO), motosSeleccionado) { motosSeleccionado = !motosSeleccionado }
                    ChipFiltro("Bicicleta", iconoVehiculo(TipoVehiculo.BICICLETA), bicisSeleccionado) { bicisSeleccionado = !bicisSeleccionado }
                }
                if (errorVehiculos) {
                    Text(
                        text = "Elige al menos un vehículo",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            SeccionFormulario(titulo = "Ubicación") {
                MiniMapaUbicacion(punto = punto, onClick = { mostrarSelector = true })

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = usarMiUbicacion,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Mi ubicación")
                    }
                    FilledTonalButton(
                        onClick = { mostrarSelector = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.EditLocationAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Elegir en el mapa")
                    }
                }

                OutlinedTextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    label = { Text("Dirección o punto de referencia *") },
                    leadingIcon = { Icon(Icons.Default.Place, contentDescription = null) },
                    isError = errorDireccion != null,
                    supportingText = errorDireccion?.let { { Text(it) } },
                    shape = RoundedCornerShape(14.dp),
                    colors = coloresCampo(),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = rutaReferencia,
                    onValueChange = { rutaReferencia = it },
                    label = { Text("Ruta hacia la universidad (Ej. Av. 68, Calle 32)") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = coloresCampo(),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            SeccionFormulario(titulo = "Contacto y horario") {
                OutlinedTextField(
                    value = telefono,
                    onValueChange = { nuevo -> telefono = nuevo.filter { it.isDigit() }.take(DIGITOS_TELEFONO) },
                    label = { Text("Teléfono / Celular *") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    isError = errorTelefono != null,
                    supportingText = { Text(errorTelefono ?: "${telefono.length}/$DIGITOS_TELEFONO dígitos") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = coloresCampo(),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Abierto 24/7", fontWeight = FontWeight.SemiBold)
                        Text(
                            text = "Atiende las 24 horas, todos los días",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = abierto24h,
                        onCheckedChange = { abierto24h = it },
                        colors = SwitchDefaults.colors(checkedTrackColor = NaranjaDesvare)
                    )
                }

                AnimatedVisibility(visible = !abierto24h) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        CampoDesplegable(
                            etiqueta = "Días de atención",
                            valor = dias,
                            opciones = DIAS,
                            icono = Icons.Default.CalendarMonth,
                            onSeleccion = { dias = it }
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(modifier = Modifier.weight(1f)) {
                                CampoDesplegable(
                                    etiqueta = "Abre",
                                    valor = horaAbre,
                                    opciones = HORAS,
                                    icono = null,
                                    onSeleccion = { horaAbre = it }
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                CampoDesplegable(
                                    etiqueta = "Cierra",
                                    valor = horaCierra,
                                    opciones = HORAS,
                                    icono = null,
                                    onSeleccion = { horaCierra = it }
                                )
                            }
                        }
                        if (errorHorario) {
                            Text(
                                text = "La hora de abrir y la de cerrar no pueden ser la misma",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            SeccionFormulario(titulo = "Más detalles") {
                OutlinedButton(
                    onClick = { selectorImagenLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    if (fotoUri != null) {
                        AsyncImage(
                            model = Uri.parse(fotoUri),
                            contentDescription = "Foto de la fachada",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    } else {
                        Icon(Icons.Default.CameraAlt, contentDescription = null)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(if (fotoUri != null) "Foto adjuntada · Cambiar" else "Adjuntar foto de la fachada")
                }

                OutlinedTextField(
                    value = recomendadoPor,
                    onValueChange = { recomendadoPor = it },
                    label = { Text("Tu nombre (quien recomienda)") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = coloresCampo(),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notas,
                    onValueChange = { notas = it },
                    label = { Text("Notas (precios, qué arreglan, seguridad, etc.)") },
                    shape = RoundedCornerShape(14.dp),
                    colors = coloresCampo(),
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }

            BotonPrincipal(
                texto = if (publico) "Publicar en el mapa" else "Guardar solo para mí",
                cargando = guardando,
                icono = Icons.Default.Save,
                onClick = {
                    intentoEnviar = true
                    val vehiculos = buildList {
                        if (carrosSeleccionado) add(TipoVehiculo.CARRO)
                        if (motosSeleccionado) add(TipoVehiculo.MOTO)
                        if (bicisSeleccionado) add(TipoVehiculo.BICICLETA)
                    }
                    val todoBien = nombre.isNotBlank() && direccion.isNotBlank() &&
                            telefono.length == DIGITOS_TELEFONO && vehiculos.isNotEmpty() && !errorHorario

                    if (!todoBien) {
                        Toast.makeText(context, "Revisa los campos marcados en rojo", Toast.LENGTH_SHORT).show()
                    } else {
                        onGuardarLugar(
                            LugarNuevo(
                                nombre = nombre,
                                tipo = tipoSeleccionado,
                                vehiculos = vehiculos,
                                direccion = direccion,
                                horario = horario,
                                telefono = telefono,
                                latitud = latitud,
                                longitud = longitud,
                                rutaReferencia = rutaReferencia,
                                recomendadoPor = recomendadoPor,
                                notas = notas.ifBlank { "Recomendado por la comunidad universitaria." },
                                fotoUri = fotoUri,
                                publico = publico
                            )
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    if (mostrarSelector) {
        SelectorUbicacionDialog(
            puntoInicial = punto,
            onConfirmar = { nuevoPunto, direccionEncontrada ->
                usarPunto(nuevoPunto, direccionEncontrada)
                mostrarSelector = false
            },
            onCerrar = { mostrarSelector = false }
        )
    }
}

@Composable
private fun SeccionFormulario(titulo: String, contenido: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = NaranjaDesvare
            )
            contenido()
        }
    }
}

@Composable
private fun MiniMapaUbicacion(punto: LatLng, onClick: () -> Unit) {
    val camara = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(punto, 16f)
    }
    LaunchedEffect(punto) {
        camara.position = CameraPosition.fromLatLngZoom(punto, 16f)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = camara,
            uiSettings = MapUiSettings(
                compassEnabled = false,
                mapToolbarEnabled = false,
                myLocationButtonEnabled = false,
                rotationGesturesEnabled = false,
                scrollGesturesEnabled = false,
                tiltGesturesEnabled = false,
                zoomControlsEnabled = false,
                zoomGesturesEnabled = false
            )
        ) {
            Marker(state = remember(punto) { MarkerState(position = punto) })
        }
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(onClick = onClick)
        )
        Text(
            text = "Toca para mover el punto",
            style = MaterialTheme.typography.labelMedium,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp)
                .background(Color.Black.copy(alpha = 0.55f), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun CampoDesplegable(
    etiqueta: String,
    valor: String,
    opciones: List<String>,
    icono: ImageVector?,
    onSeleccion: (String) -> Unit
) {
    var abierto by remember { mutableStateOf(false) }

    Box {
        OutlinedTextField(
            value = valor,
            onValueChange = {},
            readOnly = true,
            label = { Text(etiqueta) },
            // Sin ícono a la izquierda se gana espacio para que la hora (7:00 AM) se lea completa
            leadingIcon = if (icono != null) {
                { Icon(icono, contentDescription = null) }
            } else null,
            trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = coloresCampo(),
            modifier = Modifier.fillMaxWidth()
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { abierto = true }
        )
        DropdownMenu(
            expanded = abierto,
            onDismissRequest = { abierto = false },
            modifier = Modifier.heightIn(max = 320.dp)
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = {
                        Text(opcion, fontWeight = if (opcion == valor) FontWeight.Bold else FontWeight.Normal)
                    },
                    onClick = {
                        onSeleccion(opcion)
                        abierto = false
                    }
                )
            }
        }
    }
}

@Composable
private fun coloresCampo() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = NaranjaDesvare,
    focusedLabelColor = NaranjaDesvare,
    focusedLeadingIconColor = NaranjaDesvare,
    cursorColor = NaranjaDesvare
)

@Composable
private fun coloresSegmento() = SegmentedButtonDefaults.colors(
    activeContainerColor = NaranjaDesvare.copy(alpha = 0.18f),
    activeContentColor = NaranjaDesvare,
    activeBorderColor = NaranjaDesvare
)

@Preview(showBackground = true, heightDp = 1600, name = "Recomendar Lugar - Modo Claro")
@Composable
fun RecomendarLugarScreenLightPreview() {
    ElDesvareTheme {
        RecomendarLugarContent(nombreUsuario = "Andres Morales")
    }
}

@Preview(showBackground = true, heightDp = 1600, name = "Recomendar Lugar - Modo Oscuro")
@Composable
fun RecomendarLugarScreenDarkPreview() {
    ElDesvareTheme(darkTheme = true) {
        RecomendarLugarContent()
    }
}