package com.ucompensar.eldesvare.data

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ucompensar.eldesvare.model.DatosIniciales
import com.ucompensar.eldesvare.model.Lugar
import com.ucompensar.eldesvare.model.TipoLugar
import com.ucompensar.eldesvare.model.TipoVehiculo
import com.ucompensar.eldesvare.ui.theme.ElDesvareTheme
import com.ucompensar.eldesvare.ui.theme.screens.TarjetaLugarItem
import org.json.JSONArray
import org.json.JSONObject

class PreferenciasLocalesRepository(context: Context) {

    private val prefs = context.getSharedPreferences("el_desvare_offline_db", Context.MODE_PRIVATE)

    fun obtenerPreferenciaTema(): Int {
        return prefs.getInt("preferencia_tema", 0)
    }

    fun guardarPreferenciaTema(tema: Int) {
        prefs.edit().putInt("preferencia_tema", tema).apply()
    }

    fun obtenerLugares(): List<Lugar> {
        val jsonString = prefs.getString("lugares_guardados", null)
        if (jsonString.isNullOrEmpty()) {
            guardarLugares(DatosIniciales.lugaresPrecargados)
            return DatosIniciales.lugaresPrecargados
        }

        return try {
            val jsonArray = JSONArray(jsonString)
            val lista = mutableListOf<Lugar>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val vehiculosArray = obj.getJSONArray("vehiculos")
                val vehiculos = mutableListOf<TipoVehiculo>()
                for (j in 0 until vehiculosArray.length()) {
                    vehiculos.add(TipoVehiculo.valueOf(vehiculosArray.getString(j)))
                }

                lista.add(
                    Lugar(
                        id = obj.getInt("id"),
                        nombre = obj.getString("nombre"),
                        tipo = TipoLugar.valueOf(obj.getString("tipo")),
                        vehiculosAtendidos = vehiculos,
                        direccion = obj.getString("direccion"),
                        horario = obj.getString("horario"),
                        telefono = obj.getString("telefono"),
                        latitud = obj.getDouble("latitud"),
                        longitud = obj.getDouble("longitud"),
                        rutaReferencia = obj.getString("rutaReferencia"),
                        verificado = obj.getBoolean("verificado"),
                        recomendadoPor = obj.getString("recomendadoPor"),
                        notas = obj.getString("notas"),
                        fotoUri = obj.optString("fotoUri", "").takeIf { it.isNotBlank() },
                        reportesFalsos = obj.optInt("reportesFalsos", 0)
                    )
                )
            }
            lista
        } catch (e: Exception) {
            DatosIniciales.lugaresPrecargados
        }
    }

    fun guardarLugares(lugares: List<Lugar>) {
        val jsonArray = JSONArray()
        lugares.forEach { lugar ->
            val obj = JSONObject().apply {
                put("id", lugar.id)
                put("nombre", lugar.nombre)
                put("tipo", lugar.tipo.name)
                put("vehiculos", JSONArray(lugar.vehiculosAtendidos.map { it.name }))
                put("direccion", lugar.direccion)
                put("horario", lugar.horario)
                put("telefono", lugar.telefono)
                put("latitud", lugar.latitud)
                put("longitud", lugar.longitud)
                put("rutaReferencia", lugar.rutaReferencia)
                put("verificado", lugar.verificado)
                put("recomendadoPor", lugar.recomendadoPor)
                put("notas", lugar.notas)
                put("fotoUri", lugar.fotoUri ?: "")
                put("reportesFalsos", lugar.reportesFalsos)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString("lugares_guardados", jsonArray.toString()).apply()
    }
}

@Preview(showBackground = true, name = "Repositorio - Lugares Guardados")
@Composable
fun PreferenciasLocalesRepositoryPreview() {
    ElDesvareTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Datos del Repositorio Offline (${DatosIniciales.lugaresPrecargados.size} lugares)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            DatosIniciales.lugaresPrecargados.take(2).forEach { lugar ->
                TarjetaLugarItem(
                    lugar = lugar,
                    distanciaTexto = "250 m",
                    onClick = {},
                    onUsarBrujula = {}
                )
            }
        }
    }
}