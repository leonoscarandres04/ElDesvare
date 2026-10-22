package com.ucompensar.eldesvare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ucompensar.eldesvare.sensors.SensorLuzHelper
import com.ucompensar.eldesvare.ui.theme.ElDesvareTheme
import com.ucompensar.eldesvare.ui.theme.navigation.AppNavigation
import com.ucompensar.eldesvare.viewmodel.DesvareViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val desvareViewModel: DesvareViewModel = viewModel()

            val temaPreferencia by desvareViewModel.temaPreferencia.collectAsState()

            val modoOscuroPorSensor by SensorLuzHelper.observarModoOscuro(this)
                .collectAsState(initial = isSystemInDarkTheme())

            val usarModoOscuro = when (temaPreferencia) {
                1 -> false
                2 -> true
                else -> modoOscuroPorSensor
            }

            ElDesvareTheme(darkTheme = usarModoOscuro) {
                Surface {
                    AppNavigation(desvareViewModel = desvareViewModel)
                }
            }
        }
    }
}