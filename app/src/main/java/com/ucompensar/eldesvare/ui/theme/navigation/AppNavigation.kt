package com.ucompensar.eldesvare.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ucompensar.eldesvare.ui.screens.DetalleLugarScreen
import com.ucompensar.eldesvare.ui.screens.MapaScreen
import com.ucompensar.eldesvare.ui.screens.RecomendarLugarScreen
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
    }
}