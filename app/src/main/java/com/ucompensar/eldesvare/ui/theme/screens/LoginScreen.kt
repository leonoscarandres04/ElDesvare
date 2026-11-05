package com.ucompensar.eldesvare.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ucompensar.eldesvare.ui.theme.ElDesvareTheme
import com.ucompensar.eldesvare.ui.theme.NaranjaDesvare
import com.ucompensar.eldesvare.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    motivo: String? = null,
    onIngresar: () -> Unit,
    onContinuarSinCuenta: () -> Unit,
    onIrARegistro: () -> Unit,
    onIrARecuperar: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Al llegar (o volver con el botón atrás) no deben quedar avisos de otra pantalla
    LaunchedEffect(Unit) { viewModel.limpiarMensajes() }

    LoginContent(
        cargando = uiState.cargando,
        error = uiState.error,
        motivo = motivo,
        onIniciarSesion = { correo, contrasena ->
            viewModel.iniciarSesion(correo, contrasena, onExito = onIngresar)
        },
        onContinuarSinCuenta = {
            viewModel.continuarComoInvitado()
            onContinuarSinCuenta()
        },
        onIrARegistro = {
            viewModel.limpiarMensajes()
            onIrARegistro()
        },
        onIrARecuperar = {
            viewModel.limpiarMensajes()
            onIrARecuperar()
        },
        onEditar = viewModel::limpiarMensajes
    )
}

@Composable
fun LoginContent(
    cargando: Boolean = false,
    error: String? = null,
    motivo: String? = null,
    onIniciarSesion: (correo: String, contrasena: String) -> Unit = { _, _ -> },
    onContinuarSinCuenta: () -> Unit = {},
    onIrARegistro: () -> Unit = {},
    onIrARecuperar: () -> Unit = {},
    onEditar: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current

    var correo by rememberSaveable { mutableStateOf("") }
    var contrasena by rememberSaveable { mutableStateOf("") }
    // Los errores de los campos solo se muestran después del primer intento
    var intentoEnviar by rememberSaveable { mutableStateOf(false) }

    val errorCorreo = if (intentoEnviar) ValidacionAuth.errorCorreo(correo) else null
    val errorContrasena = if (intentoEnviar) ValidacionAuth.errorContrasenaLogin(contrasena) else null

    val enviar = {
        intentoEnviar = true
        if (ValidacionAuth.errorCorreo(correo) == null && ValidacionAuth.errorContrasenaLogin(contrasena) == null) {
            focusManager.clearFocus()
            onIniciarSesion(correo, contrasena)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .imePadding()
            .navigationBarsPadding()
    ) {
        EncabezadoMarca(
            titulo = "El Desvare",
            subtitulo = "Tu ayuda en la vía, cerca a UCompensar"
        )

        TarjetaFormulario {
            Text(
                text = "¡Hola de nuevo! 👋",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Inicia sesión para recomendar talleres y parqueaderos a otros conductores.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (motivo != null) {
                Surface(
                    color = NaranjaDesvare.copy(alpha = 0.12f),
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = NaranjaDesvare)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = motivo, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            CampoAuth(
                valor = correo,
                onValorChange = {
                    correo = it
                    onEditar()
                },
                etiqueta = "Correo electrónico",
                icono = Icons.Default.Email,
                error = errorCorreo,
                tipoTeclado = KeyboardType.Email,
                onAccionTeclado = { focusManager.moveFocus(FocusDirection.Down) }
            )

            CampoAuth(
                valor = contrasena,
                onValorChange = {
                    contrasena = it
                    onEditar()
                },
                etiqueta = "Contraseña",
                icono = Icons.Default.Lock,
                error = errorContrasena,
                tipoTeclado = KeyboardType.Password,
                accionTeclado = ImeAction.Done,
                onAccionTeclado = enviar,
                esContrasena = true
            )

            TextButton(
                onClick = onIrARecuperar,
                modifier = Modifier
                    .align(Alignment.End)
                    .offset(y = (-10).dp)
            ) {
                Text("¿Olvidaste tu contraseña?", color = NaranjaDesvare, fontWeight = FontWeight.SemiBold)
            }

            BannerMensaje(texto = error, esError = true)

            BotonPrincipal(
                texto = "Iniciar sesión",
                cargando = cargando,
                onClick = enviar,
                icono = Icons.AutoMirrored.Filled.Login
            )

            SeparadorO()

            OutlinedButton(
                onClick = onContinuarSinCuenta,
                enabled = !cargando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.DirectionsCar, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (motivo != null) "Ahora no" else "Continuar sin cuenta")
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-24).dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "¿No tienes cuenta?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(onClick = onIrARegistro, enabled = !cargando) {
                Text("Regístrate", color = NaranjaDesvare, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 860, name = "Login - Modo Claro")
@Composable
fun LoginLightPreview() {
    ElDesvareTheme(darkTheme = false) {
        LoginContent()
    }
}

@Preview(showBackground = true, heightDp = 860, name = "Login - Modo Oscuro")
@Composable
fun LoginDarkPreview() {
    ElDesvareTheme(darkTheme = true) {
        LoginContent()
    }
}

@Preview(showBackground = true, heightDp = 960, name = "Login - Con error")
@Composable
fun LoginErrorPreview() {
    ElDesvareTheme(darkTheme = false) {
        LoginContent(error = "La contraseña no es correcta")
    }
}

@Preview(showBackground = true, heightDp = 960, name = "Login - Para recomendar un lugar")
@Composable
fun LoginRecomendarPreview() {
    ElDesvareTheme(darkTheme = false) {
        LoginContent(motivo = "Inicia sesión para recomendar un lugar")
    }
}
