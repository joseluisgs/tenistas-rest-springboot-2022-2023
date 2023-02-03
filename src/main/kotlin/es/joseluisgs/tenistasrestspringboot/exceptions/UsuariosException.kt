package es.joseluisgs.tenistasrestspringboot.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

sealed class UsuariosException(message: String) : RuntimeException(message)

@ResponseStatus(HttpStatus.NOT_FOUND)
class UsuariosNotFoundException(message: String) : RuntimeException(message)

@ResponseStatus(HttpStatus.BAD_REQUEST)
class UsuariosBadRequestException(message: String) : RuntimeException(message)