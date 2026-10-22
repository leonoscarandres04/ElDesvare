package com.ucompensar.eldesvare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import com.ucompensar.eldesvare.ui.navigation.AppNavigation
import com.ucompensar.eldesvare.ui.theme.ElDesvareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ElDesvareTheme {
                Surface {
                    AppNavigation()
                }
            }
        }
    }
}