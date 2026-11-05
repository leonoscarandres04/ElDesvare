package com.ucompensar.eldesvare.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
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
fun RegistroScreen(
    viewModel: AuthViewModel,
    onRegistrado: () -> Unit,
    onVolver: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.limpiarMensajes() }

    RegistroContent(
        cargando = uiState.cargando,
        error = uiState.error,
        onRegistrar = { nombre, correo, contrasena ->
            viewModel.registrar(nombre, correo, contrasena, onExito = onRegistrado)
        },
        onVolver = {
            viewModel.limpiarMensajes()
            onVolver()
        },
        onEditar = viewModel::limpiarMensajes
    )
}

@Composable
fun RegistroContent(
    cargando: Boolean = false,
    error: String? = null,
    onRegistrar: (nombre: String, correo: String, contrasena: String) -> Unit = { _, _, _ -> },
    onVolver: () -> Unit = {},
    onEditar: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current

    var nombre by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var contrasena by rememberSaveable { mutableStateOf("") }
    var confirmacion by rememberSaveable { mutableStateOf("") }
    var intentoEnviar by rememberSaveable { mutableStateOf(false) }

    val errorNombre = if (intentoEnviar) ValidacionAuth.errorNombre(nombre) else null
    val errorCorreo = if (intentoEnviar) ValidacionAuth.errorCorreo(correo) else null
    val errorContrasena = if (intentoEnviar) ValidacionAuth.errorContrasenaNueva(contrasena) else null
    val errorConfirmacion = if (intentoEnviar) ValidacionAuth.errorConfirmacion(contrasena, confirmacion) else null

    val enviar = {
        intentoEnviar = true
        val todoBien = listOf(
            ValidacionAuth.errorNombre(nombre),
            ValidacionAuth.errorCorreo(correo),
            ValidacionAuth.errorContrasenaNueva(contrasena),
            ValidacionAuth.errorConfirmacion(contrasena, confirmacion)
        ).all { it == null }
        if (todoBien) {
            focusManager.clearFocus()
            onRegistrar(nombre, correo, contrasena)
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
            titulo = "Crea tu cuenta",
            subtitulo = "Únete y ayuda a otros conductores a salir del desvare",
            tamanoLogo = 64.dp,
            onVolver = onVolver
        )

        TarjetaFormulario {
            Text(
                text = "Tus datos",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Tu nombre aparecerá en los lugares que recomiendes.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            CampoAuth(
                valor = nombre,
                onValorChange = {
                    nombre = it
                    onEditar()
                },
                etiqueta = "Nombre",
                icono = Icons.Default.Person,
                error = errorNombre,
                onAccionTeclado = { focusManager.moveFocus(FocusDirection.Down) },
                capitalizarPalabras = true
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
                onAccionTeclado = { focusManager.moveFocus(FocusDirection.Down) },
                esContrasena = true
            )

            IndicadorFuerzaContrasena(contrasena = contrasena)

            CampoAuth(
                valor = confirmacion,
                onValorChange = {
                    confirmacion = it
                    onEditar()
                },
                etiqueta = "Confirmar contraseña",
                icono = Icons.Default.LockReset,
                error = errorConfirmacion,
                tipoTeclado = KeyboardType.Password,
                accionTeclado = ImeAction.Done,
                onAccionTeclado = enviar,
                esContrasena = true
            )

            BannerMensaje(texto = error, esError = true)

            BotonPrincipal(
                texto = "Crear cuenta",
                cargando = cargando,
                onClick = enviar,
                icono = Icons.Default.PersonAdd
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-24).dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "¿Ya tienes cuenta?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(onClick = onVolver, enabled = !cargando) {
                Text("Inicia sesión", color = NaranjaDesvare, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 1000, name = "Registro - Modo Claro")
@Composable
fun RegistroLightPreview() {
    ElDesvareTheme(darkTheme = false) {
        RegistroContent()
    }
}

@Preview(showBackground = true, heightDp = 1000, name = "Registro - Modo Oscuro")
@Composable
fun RegistroDarkPreview() {
    ElDesvareTheme(darkTheme = true) {
        RegistroContent(error = "Ya existe una cuenta con ese correo")
    }
}
