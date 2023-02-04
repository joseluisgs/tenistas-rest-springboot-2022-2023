package es.joseluisgs.tenistasrestspringboot.controllers

import es.joseluisgs.tenistasrestspringboot.config.APIConfig
import es.joseluisgs.tenistasrestspringboot.config.security.jwt.JwtTokenUtils
import es.joseluisgs.tenistasrestspringboot.dto.UserWithTokenDto
import es.joseluisgs.tenistasrestspringboot.dto.UsuarioCreateDto
import es.joseluisgs.tenistasrestspringboot.dto.UsuarioDto
import es.joseluisgs.tenistasrestspringboot.dto.UsuarioLoginDto
import es.joseluisgs.tenistasrestspringboot.exceptions.UsuariosBadRequestException
import es.joseluisgs.tenistasrestspringboot.mappers.toDto
import es.joseluisgs.tenistasrestspringboot.mappers.toModel
import es.joseluisgs.tenistasrestspringboot.models.Usuario
import es.joseluisgs.tenistasrestspringboot.services.usuarios.UsuariosService
import es.joseluisgs.tenistasrestspringboot.validators.validate
import jakarta.validation.Valid
import kotlinx.coroutines.flow.toList
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException


private val logger = KotlinLogging.logger {}

@RestController

// Cuidado que se necesia la barra al final porque la estamos poniendo en los verbos
@RequestMapping(APIConfig.API_PATH + "/users") // Sigue escucnado en el directorio API
class UsuarioController @Autowired constructor(
    private val usuariosService: UsuariosService,
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenUtil: JwtTokenUtils,
) {

    @PostMapping("/login")
    fun login(@Valid @RequestBody logingDto: UsuarioLoginDto): ResponseEntity<UserWithTokenDto> {
        logger.info { "Login de usuario: ${logingDto.username}" }

        // podríamos hacerlo preguntándole al servicio si existe el usuario
        // pero mejor lo hacemos con el AuthenticationManager que es el que se encarga de ello
        // y nos devuelve el usuario autenticado o null

        val authentication: Authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                logingDto.username,
                logingDto.password
            )
        )
        // Autenticamos al usuario, si lo es nos lo devuelve
        SecurityContextHolder.getContext().authentication = authentication

        // Devolvemos al usuario autenticado
        val user = authentication.principal as Usuario

        // Generamos el token
        val jwtToken: String = jwtTokenUtil.generateToken(user)
        logger.info { "Token de usuario: ${jwtToken}" }

        // Devolvemos el usuario con el token
        val userWithToken = UserWithTokenDto(user.toDto(), jwtToken)

        // La respuesta que queremos
        return ResponseEntity.ok(userWithToken)
    }

    @PostMapping("/register")
    suspend fun register(@Valid @RequestBody usuarioDto: UsuarioCreateDto): ResponseEntity<UserWithTokenDto> {
        logger.info { "Registro de usuario: ${usuarioDto.username}" }

        // Creamos el usuario
        try {
            val user = usuarioDto.validate().toModel()
            // Lo guardamos
            val userSaved = usuariosService.save(user)
            // Generamos el token
            val jwtToken: String = jwtTokenUtil.generateToken(userSaved)
            logger.info { "Token de usuario: ${jwtToken}" }
            return ResponseEntity.ok(UserWithTokenDto(userSaved.toDto(), jwtToken))
        } catch (e: UsuariosBadRequestException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }

    @GetMapping("/me")
    fun meInfo(@AuthenticationPrincipal user: Usuario?): ResponseEntity<UsuarioDto> {
        // No hay que buscar porque el usuario ya está autenticado y lo tenemos en el contexto
        logger.info { "Obteniendo usuario: ${user?.username}" }

        return ResponseEntity.ok(user?.toDto())
    }

    @GetMapping("/list")
    suspend fun list(@AuthenticationPrincipal user: Usuario?): ResponseEntity<List<UsuarioDto>> {
        // Estamos aqui es que somos administradores!!! por el contexto!!
        logger.info { "Obteniendo lista de usuarios" }

        if (user?.rol != Usuario.Rol.ADMIN)
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "No tienes permisos para acceder a esta información")

        val res = usuariosService.findAll().toList().map { it.toDto() }
        return ResponseEntity.ok(res)
    }
}