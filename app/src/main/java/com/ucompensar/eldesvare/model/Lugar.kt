package com.ucompensar.eldesvare.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.ucompensar.eldesvare.ui.theme.ElDesvareTheme
import com.ucompensar.eldesvare.ui.theme.screens.TarjetaLugarItem

enum class TipoLugar(val etiqueta: String) {
    TALLER("Taller"),
    PARQUEADERO("Parqueadero")
}

enum class TipoVehiculo(val etiqueta: String) {
    CARRO("Carro"),
    MOTO("Moto"),
    BICICLETA("Bicicleta")
}

data class Lugar(
    val id: Int,
    val nombre: String,
    val tipo: TipoLugar,
    val vehiculosAtendidos: List<TipoVehiculo>,
    val direccion: String,
    val horario: String,
    val telefono: String,
    val latitud: Double,
    val longitud: Double,
    val rutaReferencia: String,
    val verificado: Boolean,
    val recomendadoPor: String,
    val notas: String,
    val fotoUri: String? = null,
    val reportesFalsos: Int = 0
)

object DatosIniciales {
    // Coordenadas base de referencia: Fundación Universitaria Compensar (Sede Av. 68)
    const val LAT_UCOMPENSAR = 4.6635
    const val LNG_UCOMPENSAR = -74.0985

    val lugaresPrecargados = listOf(
        // 1 y 2: Precargados por Oscar León
        Lugar(
            id = 1,
            nombre = "Montallantas y Mecánica Rápida La 68",
            tipo = TipoLugar.TALLER,
            vehiculosAtendidos = listOf(TipoVehiculo.CARRO, TipoVehiculo.MOTO),
            direccion = "Av. Carrera 68 # 66-18",
            horario = "Lunes a Sábado: 6:30 AM - 8:00 PM",
            telefono = "3104567890",
            latitud = 4.6612,
            longitud = -74.0978,
            rutaReferencia = "Ruta Av. 68 hacia UCompensar",
            verificado = true,
            recomendadoPor = "Oscar León (Equipo)",
            notas = "Despinche rápido para moto y carro, servicio de inicio de batería con cables."
        ),
        Lugar(
            id = 2,
            nombre = "Parqueadero El Estudiante Seguro",
            tipo = TipoLugar.PARQUEADERO,
            vehiculosAtendidos = listOf(TipoVehiculo.CARRO, TipoVehiculo.MOTO, TipoVehiculo.BICICLETA),
            direccion = "Calle 67B # 65-22",
            horario = "Lunes a Sábado: 6:00 AM - 10:15 PM",
            telefono = "3159876543",
            latitud = 4.6648,
            longitud = -74.0965,
            rutaReferencia = "Ruta Av. 68 / Calle 68",
            verificado = true,
            recomendadoPor = "Oscar León (Equipo)",
            notas = "Tarifa económica con carnet estudiantil, guardan cascos y tienen bicicletero cubierto."
        ),
        // 3 y 4: Precargados por Andres Morales
        Lugar(
            id = 3,
            nombre = "Abellantas Las Ferias",
            tipo = TipoLugar.TALLER,
            vehiculosAtendidos = listOf(TipoVehiculo.CARRO, TipoVehiculo.MOTO),
            direccion = "Carrera 68C # 73A-07",
            horario = "Horario por confirmar",
            telefono = "",
            latitud = 4.6808763,
            longitud = -74.085731,
            rutaReferencia = "Ruta Av. 68 / Calle 74 (detrás de Alkosto de la 68)",
            verificado = true,
            recomendadoPor = "Andres Morales (Equipo)",
            notas = "Montallantas con montaje automático, vulcanización de llantas estalladas y compra y venta de llantas nuevas y de segunda. Reciben tarjeta."
        ),
        Lugar(
            id = 4,
            nombre = "Autolavado y Parqueadero Detailing",
            tipo = TipoLugar.PARQUEADERO,
            vehiculosAtendidos = listOf(TipoVehiculo.CARRO, TipoVehiculo.MOTO),
            direccion = "Carrera 68C # 74A-02",
            horario = "Horario por confirmar",
            telefono = "3006542494",
            latitud = 4.6810236,
            longitud = -74.0854725,
            rutaReferencia = "Ruta Av. 68 / Calle 74 (cerca a Alkosto Av. 68)",
            verificado = true,
            recomendadoPor = "Andres Morales (Equipo)",
            notas = "Autolavado con servicio de parqueo mensual para motos, carros y camiones. También atienden por WhatsApp al 321 202 7782."
        ),
        // 5 y 6: Precargados por Stiven Cortés
        Lugar(
            id = 5,
            nombre = "Taller Mecánico y Grúa Exprés La 63",
            tipo = TipoLugar.TALLER,
            vehiculosAtendidos = listOf(TipoVehiculo.CARRO),
            direccion = "Av. Calle 63 # 60-12",
            horario = "24 Horas",
            telefono = "3005566778",
            latitud = 4.6580,
            longitud = -74.0930,
            rutaReferencia = "Ruta Calle 63 hacia Av. 68",
            verificado = true,
            recomendadoPor = "Stiven Cortés (Equipo)",
            notas = "Auxilio por recalentamiento de motor, sincronización y paso de corriente."
        ),
        Lugar(
            id = 6,
            nombre = "Bici-Parqueadero y Taller El Pedal",
            tipo = TipoLugar.PARQUEADERO,
            vehiculosAtendidos = listOf(TipoVehiculo.BICICLETA, TipoVehiculo.MOTO),
            direccion = "Carrera 66 # 63-80",
            horario = "Lunes a Sábado: 5:30 AM - 9:30 PM",
            telefono = "3189988776",
            latitud = 4.6601,
            longitud = -74.0952,
            rutaReferencia = "Ciclorruta Calle 63 / Av. 68",
            verificado = true,
            recomendadoPor = "Stiven Cortés (Equipo)",
            notas = "Cuenta con bomba de aire gratuita, parches y candados de alta seguridad."
        )
    )
}

@Preview(showBackground = true, name = "Componente - Tarjeta Lugar (Modo Claro)")
@Composable
fun LugarTarjetaPreview() {
    ElDesvareTheme {
        TarjetaLugarItem(
            lugar = DatosIniciales.lugaresPrecargados.first(),
            distanciaTexto = "250 m",
            onClick = {},
            onUsarBrujula = {}
        )
    }
}

@Preview(showBackground = true, name = "Componente - Tarjeta Lugar (Modo Oscuro)")
@Composable
fun LugarTarjetaDarkModePreview() {
    ElDesvareTheme(darkTheme = true) {
        TarjetaLugarItem(
            lugar = DatosIniciales.lugaresPrecargados.first(),
            distanciaTexto = "250 m",
            onClick = {},
            onUsarBrujula = {}
        )
    }
}