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

data class LugarNuevo(
    val nombre: String,
    val tipo: TipoLugar,
    val vehiculos: List<TipoVehiculo>,
    val direccion: String,
    val horario: String,
    val telefono: String,
    val latitud: Double,
    val longitud: Double,
    val rutaReferencia: String,
    val recomendadoPor: String,
    val notas: String,
    val fotoUri: String? = null,
    val publico: Boolean = true
)

data class DesvareUiState(
    val lugares: List<Lugar> = emptyList(),
    val filtroTipoLugar: TipoLugar? = null,
    val filtroVehiculo: TipoVehiculo? = null,
    val soloVerificados: Boolean = false,
    val filtroVisibilidad: Boolean? = null, // null = Todos, true = Públicos, false = Privados
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

    fun alternarFiltroVisibilidad() {
        _uiState.update { state ->
            val siguiente = when (state.filtroVisibilidad) {
                null -> true   // Solo públicos
                true -> false  // Solo privados
                false -> null  // Todos
            }
            state.copy(filtroVisibilidad = siguiente)
        }
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
            .filter { state.filtroVisibilidad == null || it.publico == state.filtroVisibilidad }
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
        lugarNuevo: LugarNuevo,
        onResultado: (Result<Boolean>) -> Unit = {}
    ) {
        try {
            val nuevoId = (_uiState.value.lugares.maxOfOrNull { it.id } ?: 0) + 1
            val nuevoLugar = Lugar(
                id = nuevoId,
                nombre = lugarNuevo.nombre,
                tipo = lugarNuevo.tipo,
                vehiculosAtendidos = lugarNuevo.vehiculos,
                direccion = lugarNuevo.direccion,
                horario = lugarNuevo.horario.ifBlank { "Horario por confirmar" },
                telefono = lugarNuevo.telefono,
                latitud = lugarNuevo.latitud,
                longitud = lugarNuevo.longitud,
                rutaReferencia = lugarNuevo.rutaReferencia.ifBlank { "Cerca de UCompensar" },
                verificado = false,
                recomendadoPor = lugarNuevo.recomendadoPor.ifBlank { "Estudiante UCompensar" },
                notas = lugarNuevo.notas,
                fotoUri = lugarNuevo.fotoUri,
                publico = lugarNuevo.publico
            )

            val listaActualizada = _uiState.value.lugares + nuevoLugar
            repositorio.guardarLugares(listaActualizada)
            _uiState.update { it.copy(lugares = listaActualizada) }
            onResultado(Result.success(true))
        } catch (e: Exception) {
            onResultado(Result.failure(e))
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
        fotoUri: String?,
        publico: Boolean = true
    ) {
        agregarLugarRecomendado(
            LugarNuevo(
                nombre = nombre,
                tipo = tipo,
                vehiculos = vehiculos,
                direccion = direccion,
                horario = horario,
                telefono = telefono,
                latitud = latitud,
                longitud = longitud,
                rutaReferencia = rutaReferencia,
                recomendadoPor = recomendadoPor,
                notas = notas,
                fotoUri = fotoUri,
                publico = publico
            )
        )
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