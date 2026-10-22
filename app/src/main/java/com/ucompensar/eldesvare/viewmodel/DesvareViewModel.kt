package com.ucompensar.eldesvare.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.ucompensar.eldesvare.data.PreferenciasLocalesRepository
import com.ucompensar.eldesvare.model.DatosIniciales
import com.ucompensar.eldesvare.model.Lugar
import com.ucompensar.eldesvare.model.TipoLugar
import com.ucompensar.eldesvare.model.TipoVehiculo
import com.ucompensar.eldesvare.sensors.BrujulaYDistanciaHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class DesvareUiState(
    val lugares: List<Lugar> = emptyList(),
    val filtroTipoLugar: TipoLugar? = null,
    val filtroVehiculo: TipoVehiculo? = null,
    val soloVerificados: Boolean = false,
    val latitudUsuario: Double = DatosIniciales.LAT_UCOMPENSAR,
    val longitudUsuario: Double = DatosIniciales.LNG_UCOMPENSAR,
    val lugarGuiaBrujula: Lugar? = null
)

class DesvareViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = PreferenciasLocalesRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(DesvareUiState())
    val uiState: StateFlow<DesvareUiState> = _uiState.asStateFlow()

    private val _temaPreferencia = MutableStateFlow(repositorio.obtenerPreferenciaTema())
    val temaPreferencia: StateFlow<Int> = _temaPreferencia.asStateFlow()

    init {
        cargarLugares()
    }

    fun cambiarTema(nuevoTema: Int) {
        repositorio.guardarPreferenciaTema(nuevoTema)
        _temaPreferencia.value = nuevoTema
    }

    private fun cargarLugares() {
        val guardados = repositorio.obtenerLugares()
        _uiState.update { state ->
            state.copy(
                lugares = guardados,
                lugarGuiaBrujula = guardados.firstOrNull()
            )
        }
    }

    fun seleccionarFiltroTipo(tipo: TipoLugar?) {
        _uiState.update { it.copy(filtroTipoLugar = tipo) }
    }

    fun seleccionarFiltroVehiculo(vehiculo: TipoVehiculo?) {
        _uiState.update { it.copy(filtroVehiculo = vehiculo) }
    }

    fun alternarSoloVerificados() {
        _uiState.update { it.copy(soloVerificados = !it.soloVerificados) }
    }

    fun actualizarUbicacionUsuario(latitud: Double, longitud: Double) {
        _uiState.update { it.copy(latitudUsuario = latitud, longitudUsuario = longitud) }
    }

    fun seleccionarLugarParaBrujula(lugar: Lugar) {
        _uiState.update { it.copy(lugarGuiaBrujula = lugar) }
    }

    fun obtenerLugaresFiltradosYOrdenados(state: DesvareUiState): List<Lugar> {
        return state.lugares
            .filter { it.reportesFalsos < 2 }
            .filter { state.filtroTipoLugar == null || it.tipo == state.filtroTipoLugar }
            .filter { state.filtroVehiculo == null || it.vehiculosAtendidos.contains(state.filtroVehiculo) }
            .filter { !state.soloVerificados || it.verificado }
            .sortedBy { lugar ->
                BrujulaYDistanciaHelper.calcularDistanciaMetros(
                    state.latitudUsuario,
                    state.longitudUsuario,
                    lugar.latitud,
                    lugar.longitud
                )
            }
    }

    fun agregarLugarRecomendado(
        nombre: String,
        tipo: TipoLugar,
        vehiculos: List<TipoVehiculo>,
        direccion: String,
        horario: String,
        telefono: String,
        latitud: Double,
        longitud: Double,
        rutaReferencia: String,
        recomendadoPor: String,
        notas: String,
        fotoUri: String?
    ) {
        val nuevoId = (_uiState.value.lugares.maxOfOrNull { it.id } ?: 0) + 1
        val nuevoLugar = Lugar(
            id = nuevoId,
            nombre = nombre,
            tipo = tipo,
            vehiculosAtendidos = vehiculos,
            direccion = direccion,
            horario = horario.ifBlank { "Horario por confirmar" },
            telefono = telefono,
            latitud = latitud,
            longitud = longitud,
            rutaReferencia = rutaReferencia.ifBlank { "Cerca de UCompensar" },
            verificado = false,
            recomendadoPor = recomendadoPor.ifBlank { "Estudiante UCompensar" },
            notas = notas,
            fotoUri = fotoUri
        )

        val listaActualizada = _uiState.value.lugares + nuevoLugar
        repositorio.guardarLugares(listaActualizada)
        _uiState.update { it.copy(lugares = listaActualizada) }
    }

    fun reportarLugarFalso(lugarId: Int) {
        val listaActualizada = _uiState.value.lugares.map { lugar ->
            if (lugar.id == lugarId) {
                lugar.copy(reportesFalsos = lugar.reportesFalsos + 2)
            } else {
                lugar
            }
        }
        repositorio.guardarLugares(listaActualizada)
        _uiState.update { it.copy(lugares = listaActualizada) }
    }
}