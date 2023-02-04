package es.joseluisgs.tenistasrestspringboot.config.security.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import es.joseluisgs.tenistasrestspringboot.exceptions.TokenInvalidException
import es.joseluisgs.tenistasrestspringboot.models.Usuario
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

private val logger = KotlinLogging.logger {}

@Component
class JwtTokenUtils {
    // Cargamos el secreto del token desde el fichero de propiedades o valor por defecto
    @Value("\${jwt.secret:MeGustanLosPepinosDeLeganes}")
    private val jwtSecreto: String? =
        null // Secreto, lo cargamos de properties y si no le asignamos un valor por defecto

    @Value("\${jwt.token-expiration:3600}")
    private val jwtDuracionTokenEnSegundos = 0 // Tiempo de expiración, idem a secreto

    // Genera el Token
    fun generateToken(user: Usuario): String {
        logger.info { "Generando token para el usuario: ${user.username}" }

        // Obtenemos el usuario
        // val user: Usuario = authentication.principal

        // Creamos el timepo de vida del token, fecha en milisegunods (*1000) Fecha del sistema
        // Mas duración del token
        val tokenExpirationDate = Date(System.currentTimeMillis() + jwtDuracionTokenEnSegundos * 1000)

        // Construimos el token con sus datos y payload
        return JWT.create()
            .withSubject(user.uuid.toString()) // Como Subject el ID del usuario
            .withHeader(mapOf("typ" to TOKEN_TYPE)) // Tipo de token
            .withIssuedAt(Date()) // Fecha actual
            .withExpiresAt(tokenExpirationDate) // Fecha de expiración
            .withClaim("username", user.username)
            .withClaim("nombre", user.nombre)
            .withClaim("rol", user.rol.name)
            //.withClaim("authorities", user.authorities.map { it.authority }.toList())
            // Le añadimos los roles o lo que queramos como payload: claims
            .sign(Algorithm.HMAC512(jwtSecreto)) // Lo firmamos con nuestro secreto HS512
    }

    // A partir de un token obetner el ID de usuario
    fun getUserIdFromJwt(token: String?): String {
        logger.info {
            "Obteniendo el ID del usuario: ${token}"
        }
        return validateToken(token!!)!!.subject
    }

    // Nos idica como validar el Token
    fun validateToken(authToken: String): DecodedJWT? {
        logger.info { "Validando el token: ${authToken}" }

        try {
            return JWT.require(Algorithm.HMAC512(jwtSecreto)).build().verify(authToken)
        } catch (e: Exception) {
            throw TokenInvalidException("Token no válido o expirado")
        }
    }

    private fun getClaimsFromJwt(token: String) =
        validateToken(token)?.claims

    fun getUsernameFromJwt(token: String): String {
        logger.info { "Obteniendo el nombre de usuario del token: ${token}" }

        val claims = getClaimsFromJwt(token)
        return claims!!["username"]!!.asString()
    }

    fun isTokenValid(token: String): Boolean {
        logger.info { "Comprobando si el token es válido: ${token}" }

        val claims = getClaimsFromJwt(token)!!
        val expirationDate = claims["exp"]!!.asDate()
        val now = Date(System.currentTimeMillis())
        return now.before(expirationDate)
    }

    companion object {
        // Naturaleza del Token!!!
        const val TOKEN_HEADER = "Authorization" // Encabezado
        const val TOKEN_PREFIX = "Bearer " // Prefijo, importante este espacio
        const val TOKEN_TYPE = "JWT" // Tipo de Token
    }
}