package com.ucompensar.eldesvare.ui.theme.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ucompensar.eldesvare.R
import com.ucompensar.eldesvare.ui.theme.AzulDesvareClaro
import com.ucompensar.eldesvare.ui.theme.NaranjaDesvare
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

// ─────────────────────────────────────────────────────────────────────────────
// 1. SPLASH: fondo negro, un cuadrado blanco redondeado entra girando en espiral
//    y el logo aparece "de repente" con un rebote encima de él.
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun SplashAnimado(onTerminar: () -> Unit) {
    val alTerminar by rememberUpdatedState(onTerminar)

    // 0 → 1: recorrido de la espiral del cuadrado blanco
    val espiral = remember { Animatable(0f) }
    // 0 → 1: tamaño del logo (con rebote)
    val escalaLogo = remember { Animatable(0f) }
    // 1 → 0: desvanecido final de toda la pantalla
    val opacidad = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        coroutineScope {
            launch {
                espiral.animateTo(1f, tween(durationMillis = 1100, easing = FastOutSlowInEasing))
            }
            launch {
                delay(650) // el logo salta cuando el cuadrado ya casi llegó al centro
                escalaLogo.animateTo(
                    1f,
                    spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
                )
            }
        }
        delay(450)
        opacidad.animateTo(0f, tween(durationMillis = 300))
        alTerminar()
    }

    val densidad = LocalDensity.current
    val radioInicialPx = with(densidad) { 170.dp.toPx() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .graphicsLayer { alpha = opacidad.value },
        contentAlignment = Alignment.Center
    ) {
        // Cuadrado blanco: gira 2 vueltas mientras se acerca al centro en espiral
        Box(
            modifier = Modifier
                .size(160.dp)
                .graphicsLayer {
                    val p = espiral.value
                    val restante = 1f - p
                    val angulo = Math.toRadians((restante * 720f).toDouble())
                    val radio = radioInicialPx * restante
                    translationX = (radio * cos(angulo)).toFloat()
                    translationY = (radio * sin(angulo)).toFloat()
                    rotationZ = -restante * 720f
                    scaleX = 0.2f + 0.8f * p
                    scaleY = 0.2f + 0.8f * p
                    alpha = p.coerceIn(0f, 1f)
                }
                .clip(RoundedCornerShape(36.dp))
                .background(Color.White)
        )

        // Logo: aparece de golpe con rebote
        Image(
            painter = painterResource(R.drawable.el_desvare),
            contentDescription = "El Desvare",
            modifier = Modifier
                .size(120.dp)
                .graphicsLayer {
                    scaleX = escalaLogo.value
                    scaleY = escalaLogo.value
                    rotationZ = (1f - escalaLogo.value) * -30f
                }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 2. ÍCONO DE TAB QUE SALTA al quedar seleccionado
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun IconoTabSaltarin(icono: ImageVector, seleccionado: Boolean) {
    val salto = remember { Animatable(0f) }      // desplazamiento vertical en dp
    val escala = remember { Animatable(1f) }

    LaunchedEffect(seleccionado) {
        if (seleccionado) {
            coroutineScope {
                launch {
                    salto.animateTo(-10f, tween(durationMillis = 140, easing = FastOutSlowInEasing))
                    salto.animateTo(
                        0f,
                        spring(dampingRatio = 0.35f, stiffness = Spring.StiffnessMediumLow)
                    )
                }
                launch {
                    escala.animateTo(1.25f, tween(durationMillis = 140))
                    escala.animateTo(1f, spring(dampingRatio = 0.4f, stiffness = Spring.StiffnessMediumLow))
                }
            }
        }
    }

    Icon(
        imageVector = icono,
        contentDescription = null,
        modifier = Modifier.graphicsLayer {
            translationY = salto.value * density
            scaleX = escala.value
            scaleY = escala.value
        }
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// 3. CHIP DE FILTRO CON GRADIENTE
//    El gradiente aparece al tocarlo (o al pasar el mouse en el emulador)
//    y queda fijo y en movimiento mientras está seleccionado.
// ─────────────────────────────────────────────────────────────────────────────

private val ColoresGradiente = listOf(NaranjaDesvare, Color(0xFFFF9A3D), AzulDesvareClaro, NaranjaDesvare)

@Composable
fun ChipGradiente(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interaccion = remember { MutableInteractionSource() }
    val presionado by interaccion.collectIsPressedAsState()
    val mouseEncima by interaccion.collectIsHoveredAsState()
    val activo = seleccionado || presionado || mouseEncima

    // Qué tan visible está el gradiente (0 = nada, 1 = completo)
    val intensidad by animateFloatAsState(
        targetValue = when {
            seleccionado -> 1f
            presionado || mouseEncima -> 0.85f
            else -> 0f
        },
        animationSpec = tween(durationMillis = 250),
        label = "intensidadGradiente"
    )

    // Desplazamiento continuo del gradiente para que se vea "vivo"
    val transicion = rememberInfiniteTransition(label = "gradienteChip")
    val desplazamiento by transicion.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(durationMillis = 2200, easing = LinearEasing), RepeatMode.Restart),
        label = "desplazamientoGradiente"
    )

    val colorTexto by animateColorAsState(
        targetValue = if (activo) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(durationMillis = 250),
        label = "colorTextoChip"
    )
    val escala by animateFloatAsState(
        targetValue = if (presionado) 0.94f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "escalaChip"
    )

    val forma = RoundedCornerShape(10.dp)
    val colorBorde = MaterialTheme.colorScheme.outline

    Box(
        modifier = modifier
            .height(36.dp)
            .graphicsLayer {
                scaleX = escala
                scaleY = escala
            }
            .clip(forma)
            .border(1.dp, if (activo) Color.Transparent else colorBorde, forma)
            .drawBehind {
                if (intensidad > 0f) {
                    val ancho = size.width * 2f
                    val inicio = -ancho * desplazamiento
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = ColoresGradiente,
                            start = Offset(inicio, 0f),
                            end = Offset(inicio + ancho, size.height),
                            tileMode = TileMode.Mirror
                        ),
                        alpha = intensidad
                    )
                }
            }
            .hoverable(interaccion)
            .clickable(
                interactionSource = interaccion,
                indication = ripple(color = Color.White),
                role = Role.Checkbox,
                onClick = onClick
            )
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            color = colorTexto,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun SplashAnimadoPreview() {
    SplashAnimado(onTerminar = {})
}
