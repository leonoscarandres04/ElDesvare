package com.ucompensar.eldesvare.ui.theme.screens

import android.app.Activity
import android.util.Patterns
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.ucompensar.eldesvare.R
import com.ucompensar.eldesvare.ui.theme.AzulDesvare
import com.ucompensar.eldesvare.ui.theme.AzulDesvareClaro
import com.ucompensar.eldesvare.ui.theme.ElDesvareTheme
import com.ucompensar.eldesvare.ui.theme.NaranjaDesvare

/** Validaciones de los formularios de cuenta. Devuelven el mensaje de error, o null si está bien. */
object ValidacionAuth {

    fun errorNombre(nombre: String): String? =
        if (nombre.isBlank()) "Escribe tu nombre" else null

    fun errorCorreo(correo: String): String? = when {
        correo.isBlank() -> "Escribe tu correo"
        !Patterns.EMAIL_ADDRESS.matcher(correo.trim()).matches() -> "Ese correo no es válido"
        else -> null
    }

    fun errorContrasenaLogin(contrasena: String): String? =
        if (contrasena.isEmpty()) "Escribe tu contraseña" else null

    // Firebase Auth pide mínimo 6 caracteres, así que se exige lo mismo desde ya
    fun errorContrasenaNueva(contrasena: String): String? = when {
        contrasena.isEmpty() -> "Escribe una contraseña"
        contrasena.length < 6 -> "Debe tener al menos 6 caracteres"
        else -> null
    }

    fun errorConfirmacion(contrasena: String, confirmacion: String): String? =
        if (confirmacion != contrasena) "Las contraseñas no coinciden" else null
}

/** Cabecera azul con el logo que comparten las pantallas de cuenta. */
@Composable
fun EncabezadoMarca(
    titulo: String,
    subtitulo: String,
    tamanoLogo: Dp = 88.dp,
    onVolver: (() -> Unit)? = null
) {
    // El logo sube y baja suavemente para darle vida a la pantalla
    val flotar = rememberInfiniteTransition(label = "logo")
    val desplazamiento by flotar.animateFloat(
        initialValue = 0f,
        targetValue = -6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flotarLogo"
    )

    IconosBarraEstadoBlancos()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp))
            .background(Brush.verticalGradient(listOf(AzulDesvare, AzulDesvareClaro)))
    ) {
        // Círculos decorativos del fondo
        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(x = (-70).dp, y = (-60).dp)
                .background(Color.White.copy(alpha = 0.05f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 50.dp, y = 40.dp)
                .background(NaranjaDesvare.copy(alpha = 0.22f), CircleShape)
        )

        if (onVolver != null) {
            IconButton(
                onClick = onVolver,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(4.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(start = 24.dp, end = 24.dp, top = 28.dp, bottom = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .graphicsLayer { translationY = desplazamiento.dp.toPx() }
                    .background(Color.White.copy(alpha = 0.10f), RoundedCornerShape(32.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(32.dp))
                    .padding(10.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.el_desvare),
                    contentDescription = "Logo de El Desvare",
                    modifier = Modifier.size(tamanoLogo)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = titulo,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitulo,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * La cabecera es azul oscuro, así que mientras se ve se ponen blancos la hora y la batería
 * de la barra de estado. Al salir de la pantalla se dejan como estaban.
 */
@Composable
private fun IconosBarraEstadoBlancos() {
    val view = LocalView.current
    if (view.isInEditMode) return

    DisposableEffect(view) {
        val window = (view.context as? Activity)?.window
        val controlador = window?.let { WindowCompat.getInsetsController(it, view) }
        val estabanOscuros = controlador?.isAppearanceLightStatusBars
        controlador?.isAppearanceLightStatusBars = false
        onDispose {
            if (estabanOscuros != null) controlador.isAppearanceLightStatusBars = estabanOscuros
        }
    }
}

/** Tarjeta blanca que se monta sobre la cabecera azul y aparece con una animación. */
@Composable
fun TarjetaFormulario(content: @Composable ColumnScope.() -> Unit) {
    // En la vista previa de Android Studio se muestra de una vez, sin animación
    val enPreview = LocalInspectionMode.current
    val visible = remember { MutableTransitionState(enPreview).apply { targetState = true } }

    AnimatedVisibility(
        visibleState = visible,
        enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { it / 5 }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .offset(y = (-36).dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 22.dp, vertical = 26.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                content = content
            )
        }
    }
}

@Composable
fun CampoAuth(
    valor: String,
    onValorChange: (String) -> Unit,
    etiqueta: String,
    icono: ImageVector,
    error: String?,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    accionTeclado: ImeAction = ImeAction.Next,
    onAccionTeclado: () -> Unit = {},
    esContrasena: Boolean = false,
    capitalizarPalabras: Boolean = false
) {
    var mostrarContrasena by rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(
        value = valor,
        onValueChange = onValorChange,
        label = { Text(etiqueta) },
        leadingIcon = { Icon(icono, contentDescription = null) },
        trailingIcon = if (esContrasena) {
            {
                IconButton(onClick = { mostrarContrasena = !mostrarContrasena }) {
                    Icon(
                        imageVector = if (mostrarContrasena) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (mostrarContrasena) "Ocultar contraseña" else "Mostrar contraseña"
                    )
                }
            }
        } else {
            null
        },
        visualTransformation = if (esContrasena && !mostrarContrasena) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        isError = error != null,
        supportingText = error?.let { { Text(it) } },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = KeyboardOptions(
            capitalization = if (capitalizarPalabras) KeyboardCapitalization.Words else KeyboardCapitalization.None,
            autoCorrectEnabled = false,
            keyboardType = tipoTeclado,
            imeAction = accionTeclado
        ),
        keyboardActions = KeyboardActions(onAny = { onAccionTeclado() }),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NaranjaDesvare,
            focusedLabelColor = NaranjaDesvare,
            focusedLeadingIconColor = NaranjaDesvare,
            cursorColor = NaranjaDesvare
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun BotonPrincipal(
    texto: String,
    cargando: Boolean,
    onClick: () -> Unit,
    icono: ImageVector? = null
) {
    Button(
        onClick = onClick,
        enabled = !cargando,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = NaranjaDesvare,
            contentColor = Color.White,
            disabledContainerColor = NaranjaDesvare.copy(alpha = 0.7f),
            disabledContentColor = Color.White
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 1.dp)
    ) {
        AnimatedContent(targetState = cargando, label = "botonCargando") { estaCargando ->
            if (estaCargando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.5.dp
                )
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (icono != null) {
                        Icon(icono, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(texto, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

/** Aviso que aparece y desaparece con animación: rojo para errores, azul para información. */
@Composable
fun BannerMensaje(texto: String?, esError: Boolean) {
    AnimatedVisibility(
        visible = texto != null,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        val fondo = if (esError) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondaryContainer
        val contenido = if (esError) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSecondaryContainer
        Surface(color = fondo, contentColor = contenido, shape = RoundedCornerShape(14.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (esError) Icons.Default.ErrorOutline else Icons.Default.CheckCircle,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = texto.orEmpty(), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun SeparadorO() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        HorizontalDivider(modifier = Modifier.weight(1f))
        Text(
            text = "o",
            modifier = Modifier.padding(horizontal = 12.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        HorizontalDivider(modifier = Modifier.weight(1f))
    }
}

/** Barra que muestra qué tan segura es la contraseña mientras se escribe. */
@Composable
fun IndicadorFuerzaContrasena(contrasena: String) {
    if (contrasena.isEmpty()) return

    val puntos = listOf(
        contrasena.length >= 8,
        contrasena.any { it.isDigit() },
        contrasena.any { it.isUpperCase() },
        contrasena.any { !it.isLetterOrDigit() }
    ).count { it }

    val (texto, color, progreso) = when {
        contrasena.length < 6 -> Triple("Muy corta", Color(0xFFE53935), 0.15f)
        puntos <= 1 -> Triple("Débil", Color(0xFFE53935), 0.35f)
        puntos <= 2 -> Triple("Media", Color(0xFFFFA000), 0.65f)
        else -> Triple("Fuerte", Color(0xFF43A047), 1f)
    }
    val progresoAnimado by animateFloatAsState(targetValue = progreso, label = "fuerza")

    Column(modifier = Modifier.offset(y = (-8).dp)) {
        LinearProgressIndicator(
            progress = { progresoAnimado },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            strokeCap = StrokeCap.Round,
            drawStopIndicator = {}
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Seguridad: $texto",
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@Preview(showBackground = true, name = "Componente - Encabezado de cuenta")
@Composable
fun EncabezadoMarcaPreview() {
    ElDesvareTheme {
        EncabezadoMarca(titulo = "El Desvare", subtitulo = "Tu ayuda en la vía, cerca a UCompensar")
    }
}

@Preview(showBackground = true, name = "Componente - Avisos")
@Composable
fun BannerMensajePreview() {
    ElDesvareTheme {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            BannerMensaje(texto = "La contraseña no es correcta", esError = true)
            BannerMensaje(texto = "Te enviamos un correo para crear una nueva contraseña", esError = false)
            IndicadorFuerzaContrasena(contrasena = "Desvare2026!")
        }
    }
}
