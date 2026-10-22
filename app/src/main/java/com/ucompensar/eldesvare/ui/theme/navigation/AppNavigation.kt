package com.ucompensar.eldesvare.ui.theme.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ucompensar.eldesvare.ui.theme.screens.DetalleLugarScreen
import com.ucompensar.eldesvare.ui.theme.screens.MapaScreen
import com.ucompensar.eldesvare.ui.theme.screens.OpcionesScreen
import com.ucompensar.eldesvare.ui.theme.screens.RecomendarLugarScreen
import com.ucompensar.eldesvare.viewmodel.DesvareViewModel

@Composable
fun AppNavigation(desvareViewModel: DesvareViewModel = viewModel()) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "mapa"
    ) {
        composable("mapa") {
            MapaScreen(
                viewModel = desvareViewModel,
                onIrADetalle = { lugarId ->
                    navController.navigate("detalle/$lugarId")
                },
                onIrARecomendar = {
                    navController.navigate("recomendar")
                },
                onIrAOpciones = {
                    navController.navigate("opciones")
                }
            )
        }

        composable(
            route = "detalle/{lugarId}",
            arguments = listOf(navArgument("lugarId") { type = NavType.IntType })
        ) { backStackEntry ->
            val lugarId = backStackEntry.arguments?.getInt("lugarId") ?: 1
            DetalleLugarScreen(
                lugarId = lugarId,
                viewModel = desvareViewModel,
                onVolver = { navController.popBackStack() }
            )
        }

        composable("recomendar") {
            RecomendarLugarScreen(
                viewModel = desvareViewModel,
                onVolver = { navController.popBackStack() }
            )
        }

        composable("opciones") {
            OpcionesScreen(
                viewModel = desvareViewModel,
                onVolver = { navController.popBackStack() }
            )
        }
    }
}