package com.ucompensar.eldesvare.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

object SensorLuzHelper {

    /**
     * Escucha el sensor de luz ambiental.
     * Retorna 'true' (Modo oscuro) si la luz es menor a 20 lux, 'false' (Modo claro) si es mayor.
     */
    fun observarModoOscuro(context: Context): Flow<Boolean> = callbackFlow {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensorLuz = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_LIGHT) {
                    val nivelLuzLux = event.values[0]
                    // Si el nivel de luz es menor a 20 lux (ambiente oscuro), activamos modo oscuro
                    trySend(nivelLuzLux < 20f)
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (sensorLuz != null) {
            sensorManager.registerListener(listener, sensorLuz, SensorManager.SENSOR_DELAY_NORMAL)
        } else {
            // Si el dispositivo no tiene sensor de luz, enviamos false por defecto
            trySend(false)
        }

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }
}