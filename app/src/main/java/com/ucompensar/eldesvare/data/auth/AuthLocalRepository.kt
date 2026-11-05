package com.ucompensar.eldesvare.data.auth

import android.content.Context
import com.ucompensar.eldesvare.model.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.security.MessageDigest
import java.util.UUID

/**
 * Login guardado solo en este celular, mientras se conecta Firebase.
 * Las contraseñas no se guardan tal cual: se guarda un hash SHA-256 con una sal por cuenta.
 */
class AuthLocalRepository(context: Context) : AuthRepository {

    private val prefs = context.getSharedPreferences("el_desvare_auth", Context.MODE_PRIVATE)

    override fun usuarioActual(): Usuario? {
        val correo = prefs.getString(CLAVE_SESION, null) ?: return null
        val cuenta = leerCuentas().optJSONObject(correo) ?: return null
        return Usuario(nombre = cuenta.getString("nombre"), correo = correo)
    }

    override suspend fun iniciarSesion(correo: String, contrasena: String): Result<Usuario> =
        withContext(Dispatchers.IO) {
            val correoLimpio = limpiarCorreo(correo)
            val cuenta = leerCuentas().optJSONObject(correoLimpio)
                ?: return@withContext Result.failure(Exception("No hay ninguna cuenta con ese correo"))

            if (cuenta.getString("hash") != hashear(contrasena, cuenta.getString("sal"))) {
                return@withContext Result.failure(Exception("La contraseña no es correcta"))
            }

            prefs.edit().putString(CLAVE_SESION, correoLimpio).apply()
            Result.success(Usuario(nombre = cuenta.getString("nombre"), correo = correoLimpio))
        }

    override suspend fun registrar(nombre: String, correo: String, contrasena: String): Result<Usuario> =
        withContext(Dispatchers.IO) {
            val correoLimpio = limpiarCorreo(correo)
            val cuentas = leerCuentas()
            if (cuentas.has(correoLimpio)) {
                return@withContext Result.failure(Exception("Ya existe una cuenta con ese correo"))
            }

            val sal = UUID.randomUUID().toString()
            cuentas.put(
                correoLimpio,
                JSONObject()
                    .put("nombre", nombre.trim())
                    .put("sal", sal)
                    .put("hash", hashear(contrasena, sal))
            )
            // Al registrarse queda con la sesión iniciada
            prefs.edit()
                .putString(CLAVE_CUENTAS, cuentas.toString())
                .putString(CLAVE_SESION, correoLimpio)
                .apply()
            Result.success(Usuario(nombre = nombre.trim(), correo = correoLimpio))
        }

    override suspend fun recuperarContrasena(correo: String): Result<String> =
        withContext(Dispatchers.IO) {
            if (leerCuentas().has(limpiarCorreo(correo))) {
                // Sin Firebase no hay cómo enviar correos todavía
                Result.success(
                    "Encontramos tu cuenta. Cuando conectemos Firebase te llegará un correo " +
                        "para crear una nueva contraseña; por ahora las cuentas solo se guardan en este celular."
                )
            } else {
                Result.failure(Exception("No hay ninguna cuenta con ese correo"))
            }
        }

    override fun cerrarSesion() {
        prefs.edit().remove(CLAVE_SESION).apply()
    }

    private fun leerCuentas(): JSONObject {
        val json = prefs.getString(CLAVE_CUENTAS, null) ?: return JSONObject()
        return try {
            JSONObject(json)
        } catch (e: Exception) {
            JSONObject()
        }
    }

    private fun limpiarCorreo(correo: String) = correo.trim().lowercase()

    private fun hashear(contrasena: String, sal: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest((sal + contrasena).toByteArray())
            .joinToString("") { "%02x".format(it) }

    private companion object {
        const val CLAVE_CUENTAS = "cuentas"
        const val CLAVE_SESION = "correo_sesion"
    }
}
