package com.ucompensar.eldesvare.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ucompensar.eldesvare.data.auth.ProveedorAuth
import com.ucompensar.eldesvare.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val usuario: Usuario? = null,
    val esInvitado: Boolean = false,
    val cargando: Boolean = false,
    val error: String? = null,
    val mensaje: String? = null
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = ProveedorAuth.crear(application.applicationContext)

    // Si quedó una sesión guardada, el usuario arranca ya conectado
    private val _uiState = MutableStateFlow(AuthUiState(usuario = repositorio.usuarioActual()))
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun iniciarSesion(correo: String, contrasena: String, onExito: () -> Unit) {
        ejecutar(onExito) { repositorio.iniciarSesion(correo, contrasena) }
    }

    fun registrar(nombre: String, correo: String, contrasena: String, onExito: () -> Unit) {
        ejecutar(onExito) { repositorio.registrar(nombre, correo, contrasena) }
    }

    fun recuperarContrasena(correo: String) {
        if (_uiState.value.cargando) return
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null, mensaje = null) }
            repositorio.recuperarContrasena(correo)
                .onSuccess { mensaje -> _uiState.update { it.copy(cargando = false, mensaje = mensaje) } }
                .onFailure { e -> _uiState.update { it.copy(cargando = false, error = mensajeDeError(e)) } }
        }
    }

    fun continuarComoInvitado() {
        _uiState.update { it.copy(esInvitado = true, error = null, mensaje = null) }
    }

    fun cerrarSesion() {
        repositorio.cerrarSesion()
        _uiState.update { AuthUiState() }
    }

    fun limpiarMensajes() {
        _uiState.update { it.copy(error = null, mensaje = null) }
    }

    private fun ejecutar(onExito: () -> Unit, accion: suspend () -> Result<Usuario>) {
        // Evita que un doble toque en el botón mande dos peticiones
        if (_uiState.value.cargando) return
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null, mensaje = null) }
            accion()
                .onSuccess { usuario ->
                    _uiState.update { it.copy(usuario = usuario, esInvitado = false, cargando = false) }
                    onExito()
                }
                .onFailure { e -> _uiState.update { it.copy(cargando = false, error = mensajeDeError(e)) } }
        }
    }

    private fun mensajeDeError(e: Throwable) = e.message ?: "Algo salió mal, intenta de nuevo"
}
