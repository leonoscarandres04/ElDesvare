package com.ucompensar.eldesvare.data.auth

import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.ucompensar.eldesvare.model.Usuario
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Login con Firebase Authentication (correo y contraseña).
 * Además guarda los datos del usuario en Firestore, en la colección "usuarios/{uid}".
 */
class AuthFirebaseRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : AuthRepository {

    init {
        // Los correos de recuperación de contraseña llegan en español
        auth.setLanguageCode("es")
    }

    override fun usuarioActual(): Usuario? = auth.currentUser?.aUsuario()

    override suspend fun iniciarSesion(correo: String, contrasena: String): Result<Usuario> =
        runCatching {
            val resultado = auth.signInWithEmailAndPassword(correo.trim(), contrasena).esperar()
            resultado.user?.aUsuario() ?: error("No se pudo iniciar sesión")
        }.traducirError()

    override suspend fun registrar(nombre: String, correo: String, contrasena: String): Result<Usuario> =
        runCatching {
            val resultado = auth.createUserWithEmailAndPassword(correo.trim(), contrasena).esperar()
            val usuario = resultado.user ?: error("No se pudo crear la cuenta")

            // Guarda el nombre en el perfil de Firebase Auth
            usuario.updateProfile(userProfileChangeRequest { displayName = nombre.trim() }).esperar()

            // Guarda el usuario en Firestore. Si esto falla, la cuenta igual queda creada.
            runCatching {
                db.collection("usuarios").document(usuario.uid).set(
                    mapOf(
                        "nombre" to nombre.trim(),
                        "correo" to usuario.email,
                        "creadoEn" to FieldValue.serverTimestamp()
                    )
                ).esperar()
            }

            Usuario(nombre = nombre.trim(), correo = usuario.email.orEmpty())
        }.traducirError()

    override suspend fun recuperarContrasena(correo: String): Result<String> =
        runCatching {
            auth.sendPasswordResetEmail(correo.trim()).esperar()
            // Firebase no revela si el correo existe (protección contra enumeración de correos)
            "Si hay una cuenta con ese correo, te llegará un enlace para crear una nueva contraseña. " +
                    "Revisa también la carpeta de spam."
        }.traducirError()

    override fun cerrarSesion() {
        auth.signOut()
    }

    private fun FirebaseUser.aUsuario() = Usuario(
        nombre = displayName.orEmpty(),
        correo = email.orEmpty()
    )

    /** Convierte los errores de Firebase en mensajes claros para el usuario. */
    private fun <T> Result<T>.traducirError(): Result<T> = recoverCatching { e ->
        throw Exception(
            when (e) {
                is FirebaseAuthWeakPasswordException -> "La contraseña es muy débil, usa al menos 6 caracteres"
                is FirebaseAuthUserCollisionException -> "Ya existe una cuenta con ese correo"
                is FirebaseAuthInvalidUserException -> "No hay ninguna cuenta con ese correo"
                is FirebaseAuthInvalidCredentialsException -> "El correo o la contraseña no son correctos"
                is FirebaseNetworkException -> "No hay conexión a internet, intenta de nuevo"
                is FirebaseTooManyRequestsException -> "Demasiados intentos, espera un momento"
                else -> e.message ?: "Algo salió mal, intenta de nuevo"
            },
            e
        )
    }
}

/** Espera a que termine una tarea de Firebase sin bloquear la app. */
private suspend fun <T> Task<T>.esperar(): T = suspendCancellableCoroutine { cont ->
    addOnCompleteListener { tarea ->
        val error = tarea.exception
        when {
            error != null -> cont.resumeWithException(error)
            tarea.isCanceled -> cont.resumeWithException(IllegalStateException("La tarea fue cancelada"))
            else -> cont.resume(tarea.result)
        }
    }
}