package es.joseluisgs.tenistasrestspringboot.services.usuarios

import es.joseluisgs.tenistasrestspringboot.exceptions.UsuariosBadRequestException
import es.joseluisgs.tenistasrestspringboot.exceptions.UsuariosNotFoundException
import es.joseluisgs.tenistasrestspringboot.models.Usuario
import es.joseluisgs.tenistasrestspringboot.repositories.usuarios.UsuariosRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

private val logger = KotlinLogging.logger {}

// Este no lo vamos a cachear pero si quisieramos solo es poner la cache como antes

@Service
class UsuariosService
@Autowired constructor(
    private val repository: UsuariosRepository, // Inyectamos el repositorio de Usuarios
    private val passwordEncoder: PasswordEncoder // Inyectamos el PasswordEncoder BCrypt en Bean de Config
) : UserDetailsService {
    // No podemos suspenderla porque es una interfaz de Spring Security
    override fun loadUserByUsername(username: String): UserDetails = runBlocking {

        // Create a method in your repo to find a user by its username
        return@runBlocking repository.findByUsername(username).firstOrNull()
            ?: throw UsuariosNotFoundException("Usuario no encontrado con username: $username")
    }

    suspend fun findAll() = withContext(Dispatchers.IO) {
        return@withContext repository.findAll()
    }

    @Cacheable("usuarios")
    suspend fun loadUserById(userId: Long) = withContext(Dispatchers.IO) {
        return@withContext repository.findById(userId)
    }

    @Cacheable("usuarios")
    suspend fun loadUserByUuid(uuid: UUID) = withContext(Dispatchers.IO) {
        return@withContext repository.findByUuid(uuid).firstOrNull()
    }

    suspend fun save(user: Usuario): Usuario = withContext(Dispatchers.IO) {
        logger.info { "Guardando usuario: $user" }

        // existe el username o el email
        checkRestricciones(user)

        logger.info { "El usuario no existe, lo guardamos" }
        // Encriptamos la contraseña
        val newUser = user.copy(
            uuid = UUID.randomUUID(), password = passwordEncoder.encode(user.password),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        try {
            return@withContext repository.save(newUser)
        } catch (e: Exception) {
            throw UsuariosBadRequestException("Error al crear el usuario: Nombre de usuario o email ya existen")
        }
    }

    private suspend fun checkRestricciones(user: Usuario) {
        if (repository.findByUsername(user.username)
                .firstOrNull() != null
        ) {
            logger.info { "El usuario ya existe" }
            throw UsuariosBadRequestException("El username ya existe")
        }
        if (repository.findByEmail(user.email)
                .firstOrNull() != null
        ) {
            logger.info { "El email ya existe" }
            throw UsuariosBadRequestException("El email ya existe")
        }
    }

    suspend fun update(user: Usuario) = withContext(Dispatchers.IO) {
        logger.info { "Actualizando usuario: $user" }

        // existe el username o el email
        checkRestricciones(user)

        logger.info { "El usuario no existe, lo actualizamos" }

        val updtatedUser = user.copy(
            updatedAt = LocalDateTime.now()
        )

        try {
            return@withContext repository.save(updtatedUser)
        } catch (e: Exception) {
            throw UsuariosBadRequestException("Error al actualizar el usuario: Nombre de usuario o email ya existen")
        }
        

    }

}