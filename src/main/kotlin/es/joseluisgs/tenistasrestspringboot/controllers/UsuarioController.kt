package es.joseluisgs.tenistasrestspringboot.controllers

import es.joseluisgs.tenistasrestspringboot.config.APIConfig
import es.joseluisgs.tenistasrestspringboot.config.security.jwt.JwtTokenUtils
import es.joseluisgs.tenistasrestspringboot.dto.UsuarioLoginDto
import es.joseluisgs.tenistasrestspringboot.models.Usuario
import es.joseluisgs.tenistasrestspringboot.services.usuarios.UsuariosService
import jakarta.validation.Valid
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
    fun login(@Valid @RequestBody logingDto: UsuarioLoginDto) {
        logger.info { "Login de usuario: ${logingDto.username}" }

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

        // La respuesta que queremos
        ResponseEntity.ok(jwtToken)
    }
}