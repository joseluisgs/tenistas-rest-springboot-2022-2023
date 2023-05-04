package es.joseluisgs.tenistasrestspringboot.controllers

import es.joseluisgs.tenistasrestspringboot.config.APIConfig
import es.joseluisgs.tenistasrestspringboot.config.security.jwt.JwtTokenUtils
import es.joseluisgs.tenistasrestspringboot.dto.*
import es.joseluisgs.tenistasrestspringboot.mappers.toDto
import es.joseluisgs.tenistasrestspringboot.mappers.toModel
import es.joseluisgs.tenistasrestspringboot.models.Usuario
import es.joseluisgs.tenistasrestspringboot.services.storage.StorageService
import es.joseluisgs.tenistasrestspringboot.services.usuarios.UsuariosService
import es.joseluisgs.tenistasrestspringboot.validators.validate
import jakarta.validation.Valid
import kotlinx.coroutines.flow.toList
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile


private val logger = KotlinLogging.logger {}

@RestController

// Cuidado que se necesia la barra al final porque la estamos poniendo en los verbos
@RequestMapping(APIConfig.API_PATH + "/users") // Sigue escucnado en el directorio API
class UsuarioController @Autowired constructor(
    private val usuariosService: UsuariosService,
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenUtil: JwtTokenUtils,
    private val storageService: StorageService
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
        val user = usuarioDto.validate().toModel()
        // Lo guardamos
        user.rol.forEach { println(it) }
        val userSaved = usuariosService.save(user)
        // Generamos el token
        val jwtToken: String = jwtTokenUtil.generateToken(userSaved)
        logger.info { "Token de usuario: ${jwtToken}" }
        return ResponseEntity.ok(UserWithTokenDto(userSaved.toDto(), jwtToken))
    }

    // Solo los usuarios pueden acceder a esta información
    // en el fondo no es necesario porque ya lo hace el AuthenticationManager y es el rol minimo
    @PreAuthorize("hasRole('USER')") // hasAnyRole('USER', 'ADMIN')
    @GetMapping("/me")
    fun meInfo(@AuthenticationPrincipal user: Usuario): ResponseEntity<UsuarioDto> {
        // No hay que buscar porque el usuario ya está autenticado y lo tenemos en el contexto
        logger.info { "Obteniendo usuario: ${user.username}" }

        return ResponseEntity.ok(user.toDto())
    }

    @PreAuthorize("hasRole('ADMIN')") // Solo los administradores pueden acceder a esta información
    @GetMapping("/list")
    suspend fun list(@AuthenticationPrincipal user: Usuario): ResponseEntity<List<UsuarioDto>> {
        // Estamos aqui es que somos administradores!!! por el contexto!!
        logger.info { "Obteniendo lista de usuarios" }

        // No hay que buscar porque el usuario ya está autenticado y lo tenemos en el contexto
        // Si lo hacemos pues estamos yendo de mas  a la base de datos pero es para mostrar como se hace
        /*
        if (!user?.rol?.contains(Usuario.Rol.ADMIN)!!)
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "No tienes permisos para acceder a esta información")
        */

        val res = usuariosService.findAll().toList().map { it.toDto() }
        return ResponseEntity.ok(res)
    }

    // No voy a poner el @PreAuthorize porque ya se da por hecho que es usuario
    // que es el rol minimo, si no poner para los roles que son
    @PutMapping("/me")
    suspend fun updateMe(
        @AuthenticationPrincipal user: Usuario,
        @Valid @RequestBody usuarioDto: UsuarioUpdateDto
    ): ResponseEntity<UsuarioDto> {
        // No hay que buscar porque el usuario ya está autenticado y lo tenemos en el contexto
        logger.info { "Actualizando usuario: ${user.username}" }

        usuarioDto.validate()

        var userUpdated = user.copy(
            nombre = usuarioDto.nombre,
            username = usuarioDto.username,
            email = usuarioDto.email,
        )

        // Actualizamos el usuario
        userUpdated = usuariosService.update(userUpdated)
        return ResponseEntity.ok(userUpdated.toDto())
    }

    @PatchMapping(
        value = ["/me"],
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    suspend fun updateAvatar(
        @AuthenticationPrincipal user: Usuario,
        @RequestPart("file") file: MultipartFile
    ): ResponseEntity<UsuarioDto> {
        // No hay que buscar porque el usuario ya está autenticado y lo tenemos en el contexto
        logger.info { "Actualizando avatar de usuario: ${user.username}" }

        var urlImagen = user.avatar

        // subimos el fichero
        if (!file.isEmpty) {
            // Podemos pasarle el nombre del fichero con un id del usuario
            val imagen: String = storageService.store(file, user.uuid.toString())
            urlImagen = storageService.getUrl(imagen)
        }

        val userAvatar = user.copy(
            avatar = urlImagen
        )

        // Actualizamos el usuario
        val userUpdated = usuariosService.update(userAvatar)
        return ResponseEntity.ok(userUpdated.toDto())
    }

    // Para capturar los errores de validación
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(
        ex: MethodArgumentNotValidException
    ): Map<String, String>? {
        val errors: MutableMap<String, String> = HashMap()
        ex.bindingResult?.allErrors?.forEach { error ->
            val fieldName = (error as FieldError).field
            val errorMessage: String? = error.getDefaultMessage()
            errors[fieldName] = errorMessage ?: ""
        }
        return errors
    }

}