package com.ucompensar.eldesvare.ui.theme.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ucompensar.eldesvare.ui.theme.screens.DetalleLugarScreen
import com.ucompensar.eldesvare.ui.theme.screens.LoginScreen
import com.ucompensar.eldesvare.ui.theme.screens.MapaScreen
import com.ucompensar.eldesvare.ui.theme.screens.OpcionesScreen
import com.ucompensar.eldesvare.ui.theme.screens.RecomendarLugarScreen
import com.ucompensar.eldesvare.ui.theme.screens.RecuperarContrasenaScreen
import com.ucompensar.eldesvare.ui.theme.screens.RegistroScreen
import com.ucompensar.eldesvare.viewmodel.AuthViewModel
import com.ucompensar.eldesvare.viewmodel.DesvareViewModel

// Destino al que se va después de iniciar sesión cuando el login se abrió desde "Recomendar"
private const val DESTINO_RECOMENDAR = "recomendar"

@Composable
fun AppNavigation(
    desvareViewModel: DesvareViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val navController = rememberNavController()
    val authState by authViewModel.uiState.collectAsState()

    // Si ya hay una sesión guardada se entra directo al mapa
    val pantallaInicial = remember {
        if (authViewModel.uiState.value.usuario != null) "mapa" else "login?destino={destino}"
    }

    // Después de entrar se limpia el historial para que "atrás" no regrese al login
    val entrarALaApp = {
        navController.navigate("mapa") {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }
    val volverAlLogin = {
        navController.navigate("login?destino=mapa") {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }
    val continuarHacia = { destino: String ->
        if (destino == DESTINO_RECOMENDAR) {
            navController.navigate("recomendar") { popUpTo("mapa") }
        } else {
            entrarALaApp()
        }
    }

    NavHost(
        navController = navController,
        startDestination = pantallaInicial
    ) {
        composable(
            route = "login?destino={destino}",
            arguments = listOf(navArgument("destino") { type = NavType.StringType; defaultValue = "mapa" })
        ) { backStackEntry ->
            val destino = backStackEntry.arguments?.getString("destino") ?: "mapa"
            LoginScreen(
                viewModel = authViewModel,
                motivo = if (destino == DESTINO_RECOMENDAR) "Inicia sesión para recomendar un lugar" else null,
                onIngresar = { continuarHacia(destino) },
                onContinuarSinCuenta = {
                    if (destino == DESTINO_RECOMENDAR) navController.popBackStack("mapa", inclusive = false) else entrarALaApp()
                },
                onIrARegistro = { navController.navigate("registro?destino=$destino") },
                onIrARecuperar = { navController.navigate("recuperar") }
            )
        }

        composable(
            route = "registro?destino={destino}",
            arguments = listOf(navArgument("destino") { type = NavType.StringType; defaultValue = "mapa" })
        ) { backStackEntry ->
            val destino = backStackEntry.arguments?.getString("destino") ?: "mapa"
            RegistroScreen(
                viewModel = authViewModel,
                onRegistrado = { continuarHacia(destino) },
                onVolver = { navController.popBackStack() }
            )
        }

        composable("recuperar") {
            RecuperarContrasenaScreen(
                viewModel = authViewModel,
                onVolver = { navController.popBackStack() }
            )
        }

        composable("mapa") {
            MapaScreen(
                viewModel = desvareViewModel,
                onIrADetalle = { lugarId ->
                    navController.navigate("detalle/$lugarId")
                },
                onIrARecomendar = {
                    // Para recomendar se necesita cuenta; los invitados pasan primero por el login
                    if (authState.usuario != null) {
                        navController.navigate("recomendar")
                    } else {
                        navController.navigate("login?destino=$DESTINO_RECOMENDAR")
                    }
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
                nombreUsuario = authState.usuario?.nombre.orEmpty(),
                onVolver = { navController.popBackStack() }
            )
        }

        composable("opciones") {
            OpcionesScreen(
                viewModel = desvareViewModel,
                usuario = authState.usuario,
                onCerrarSesion = {
                    authViewModel.cerrarSesion()
                    volverAlLogin()
                },
                onIniciarSesion = volverAlLogin,
                onVolver = { navController.popBackStack() }
            )
        }
    }
}
