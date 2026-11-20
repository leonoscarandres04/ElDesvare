package com.ucompensar.eldesvare.data

import android.content.Context
import android.location.Geocoder
import android.os.Build
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

object DireccionesHelper {
    suspend fun direccionDe(context: Context, punto: LatLng): String? = withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context, Locale("es", "CO"))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                suspendCancellableCoroutine { cont ->
                    geocoder.getFromLocation(punto.latitude, punto.longitude, 1) { direcciones ->
                        val texto = direcciones.firstOrNull()?.getAddressLine(0)
                            ?.substringBefore(",")
                            ?.trim()
                        cont.resume(texto)
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                val direcciones = geocoder.getFromLocation(punto.latitude, punto.longitude, 1)
                direcciones?.firstOrNull()?.getAddressLine(0)
                    ?.substringBefore(",")
                    ?.trim()
            }
        } catch (e: Exception) {
            null
        }
    }
}