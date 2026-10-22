package com.ucompensar.eldesvare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ucompensar.eldesvare.sensors.SensorLuzHelper
import com.ucompensar.eldesvare.ui.navigation.AppNavigation
import com.ucompensar.eldesvare.ui.theme.ElDesvareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // 1. Leemos el sensor de luz en tiempo real (si no hay sensor, usa el modo del sistema)
            val modoOscuroPorSensor by SensorLuzHelper.observarModoOscuro(this)
                .collectAsState(initial = isSystemInDarkTheme())

            // 2. Le pasamos el estado al tema principal de la app
            ElDesvareTheme(darkTheme = modoOscuroPorSensor) {
                Surface {
                    AppNavigation()
                }
            }
        }
    }
}