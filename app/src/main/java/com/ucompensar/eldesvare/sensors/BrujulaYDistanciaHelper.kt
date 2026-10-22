package com.ucompensar.eldesvare.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

object BrujulaYDistanciaHelper {

    fun calcularDistanciaMetros(
        latUsuario: Double,
        lngUsuario: Double,
        latDestino: Double,
        lngDestino: Double
    ): Float {
        val resultados = FloatArray(1)
        Location.distanceBetween(latUsuario, lngUsuario, latDestino, lngDestino, resultados)
        return resultados[0]
    }

    fun formatearDistancia(metros: Float): String {
        return if (metros < 1000f) {
            "${metros.toInt()} m"
        } else {
            String.format(Locale.US, "%.1f km", metros / 1000f)
        }
    }

    fun calcularRumboHaciaDestino(
        latOrigen: Double,
        lngOrigen: Double,
        latDestino: Double,
        lngDestino: Double
    ): Float {
        val lat1 = Math.toRadians(latOrigen)
        val lon1 = Math.toRadians(lngOrigen)
        val lat2 = Math.toRadians(latDestino)
        val lon2 = Math.toRadians(lngDestino)

        val dLon = lon2 - lon1
        val y = sin(dLon) * cos(lat2)
        val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLon)
        val brng = Math.toDegrees(atan2(y, x)).toFloat()
        return (brng + 360f) % 360f
    }

    fun observarAzimutBrujula(context: Context): Flow<Float> = callbackFlow {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        val gravity = FloatArray(3)
        val geomagnetic = FloatArray(3)
        var tieneGravedad = false
        var tieneMagnetismo = false

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                    System.arraycopy(event.values, 0, gravity, 0, event.values.size)
                    tieneGravedad = true
                } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                    System.arraycopy(event.values, 0, geomagnetic, 0, event.values.size)
                    tieneMagnetismo = true
                }

                if (tieneGravedad && tieneMagnetismo) {
                    val r = FloatArray(9)
                    val i = FloatArray(9)
                    if (SensorManager.getRotationMatrix(r, i, gravity, geomagnetic)) {
                        val orientation = FloatArray(3)
                        SensorManager.getOrientation(r, orientation)
                        val azimutGrados =
                            ((Math.toDegrees(orientation[0].toDouble()).toFloat()) + 360f) % 360f
                        trySend(azimutGrados)
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        accelerometer?.let {
            sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI)
        }
        magnetometer?.let {
            sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI)
        }

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }
}