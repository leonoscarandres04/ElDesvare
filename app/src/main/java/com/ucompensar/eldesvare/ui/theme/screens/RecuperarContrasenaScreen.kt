package com.ucompensar.eldesvare.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.MaterialTheme
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
fun RecuperarContrasenaScreen(
    viewModel: AuthViewModel,
    onVolver: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.limpiarMensajes() }

    RecuperarContrasenaContent(
        cargando = uiState.cargando,
        error = uiState.error,
        mensaje = uiState.mensaje,
        onEnviar = viewModel::recuperarContrasena,
        onVolver = {
            viewModel.limpiarMensajes()
            onVolver()
        },
        onEditar = viewModel::limpiarMensajes
    )
}

@Composable
fun RecuperarContrasenaContent(
    cargando: Boolean = false,
    error: String? = null,
    mensaje: String? = null,
    onEnviar: (correo: String) -> Unit = {},
    onVolver: () -> Unit = {},
    onEditar: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current

    var correo by rememberSaveable { mutableStateOf("") }
    var intentoEnviar by rememberSaveable { mutableStateOf(false) }
    val errorCorreo = if (intentoEnviar) ValidacionAuth.errorCorreo(correo) else null

    val enviar = {
        intentoEnviar = true
        if (ValidacionAuth.errorCorreo(correo) == null) {
            focusManager.clearFocus()
            onEnviar(correo)
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
            titulo = "¿Olvidaste tu contraseña?",
            subtitulo = "Tranquilo, le pasa a cualquiera",
            tamanoLogo = 64.dp,
            onVolver = onVolver
        )

        TarjetaFormulario {
            Text(
                text = "Recupera tu cuenta",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Escribe el correo con el que te registraste y te enviaremos un enlace para crear una nueva contraseña.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

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
                accionTeclado = ImeAction.Send,
                onAccionTeclado = enviar
            )

            BannerMensaje(texto = error, esError = true)
            BannerMensaje(texto = mensaje, esError = false)

            BotonPrincipal(
                texto = "Enviar enlace",
                cargando = cargando,
                onClick = enviar,
                icono = Icons.AutoMirrored.Filled.Send
            )

            TextButton(
                onClick = onVolver,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Volver a iniciar sesión", color = NaranjaDesvare, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 800, name = "Recuperar contraseña - Modo Claro")
@Composable
fun RecuperarContrasenaLightPreview() {
    ElDesvareTheme(darkTheme = false) {
        RecuperarContrasenaContent()
    }
}

@Preview(showBackground = true, heightDp = 860, name = "Recuperar contraseña - Enviado")
@Composable
fun RecuperarContrasenaEnviadoPreview() {
    ElDesvareTheme(darkTheme = true) {
        RecuperarContrasenaContent(mensaje = "Te enviamos un correo para crear una nueva contraseña.")
    }
}
