
package com.ucompensar.eldesvare.ui.theme.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.rememberCameraPositionState
import com.ucompensar.eldesvare.data.DireccionesHelper
import com.ucompensar.eldesvare.ui.theme.AzulDesvare
import com.ucompensar.eldesvare.ui.theme.NaranjaDesvare
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
@Composable
fun rememberPedirUbicacionActual(
    onUbicacion: (LatLng) -> Unit,
    onSinUbicacion: () -> Unit
): () -> Unit {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val obtener = {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { loc ->
                if (loc != null) onUbicacion(LatLng(loc.latitude, loc.longitude))
                else onSinUbicacion()
            }
            .addOnFailureListener { onSinUbicacion() }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permisos ->
        val concedido = permisos[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permisos[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (concedido) obtener() else onSinUbicacion()
    }

    return {
        val tienePermiso = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (tienePermiso) obtener()
        else launcher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
}

@Composable
fun SelectorUbicacionDialog(
    puntoInicial: LatLng,
    onConfirmar: (LatLng, String?) -> Unit,
    onCerrar: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val camara = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(puntoInicial, 16f)
    }
    var direccionPreview by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(camara.isMoving) {
        if (!camara.isMoving) {
            val destino = camara.position.target
            direccionPreview = DireccionesHelper.direccionDe(context, destino)
        }
    }

    Dialog(
        onDismissRequest = onCerrar,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize()) {
                val tienePermiso = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = camara,
                    properties = MapProperties(isMyLocationEnabled = tienePermiso),
                    onMapClick = { latLng ->
                        camara.position = CameraPosition.fromLatLngZoom(latLng, camara.position.zoom)
                    }
                )

                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Ubicación seleccionada",
                    tint = NaranjaDesvare,
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.Center)
                        .offset(y = (-24).dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(12.dp)
                        .background(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onCerrar) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                    Text(
                        text = "Mueve el mapa o toca un punto",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = direccionPreview ?: "Ubicación seleccionada en el mapa",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Lat: ${"%.5f".format(camara.position.target.latitude)}, Lng: ${"%.5f".format(camara.position.target.longitude)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        BotonPrincipal(
                            texto = "Confirmar esta ubicación",
                            icono = Icons.Default.Check,
                            onClick = {
                                val puntoFinal = camara.position.target
                                scope.launch {
                                    val dir = direccionPreview ?: DireccionesHelper.direccionDe(context, puntoFinal)
                                    onConfirmar(puntoFinal, dir)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BotonPrincipal(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cargando: Boolean = false,
    icono: ImageVector? = null
) {
    Button(
        onClick = onClick,
        enabled = !cargando,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = NaranjaDesvare,
            contentColor = AzulDesvare
        )
    ) {
        if (cargando) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                strokeWidth = 2.5.dp,
                color = AzulDesvare
            )
        } else {
            if (icono != null) {
                Icon(icono, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(text = texto, fontWeight = FontWeight.Bold)
        }
    }
}