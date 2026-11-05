package com.ucompensar.eldesvare.data.auth

import android.content.Context
import com.ucompensar.eldesvare.model.Usuario

/**
 * Todo lo que la app necesita del login. Las pantallas y el ViewModel solo usan esta
 * interfaz, así que para pasar a Firebase Auth basta con crear otra implementación
 * (por ejemplo AuthFirebaseRepository) y devolverla en [ProveedorAuth].
 */
interface AuthRepository {

    /** Usuario con la sesión guardada, o null si nadie ha iniciado sesión. */
    fun usuarioActual(): Usuario?

    suspend fun iniciarSesion(correo: String, contrasena: String): Result<Usuario>

    suspend fun registrar(nombre: String, correo: String, contrasena: String): Result<Usuario>

    /** Devuelve el mensaje que se le muestra al usuario cuando todo sale bien. */
    suspend fun recuperarContrasena(correo: String): Result<String>

    fun cerrarSesion()
}

object ProveedorAuth {
    // Cuando Firebase esté conectado, aquí se cambia AuthLocalRepository por la versión de Firebase
    fun crear(context: Context): AuthRepository = AuthLocalRepository(context)
}
