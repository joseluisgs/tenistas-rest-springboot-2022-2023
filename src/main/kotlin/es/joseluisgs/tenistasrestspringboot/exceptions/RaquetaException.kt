package es.joseluisgs.tenistasrestspringboot.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

sealed class RaquetaException(message: String) : RuntimeException(message)

// También podemos usar la anotación @ResponseStatus para indicar el código de error
@ResponseStatus(HttpStatus.NOT_FOUND)
class RaquetaNotFoundException(message: String) : RaquetaException(message)

@ResponseStatus(HttpStatus.BAD_REQUEST)
class RaquetaBadRequestException(message: String) : RaquetaException(message)

@ResponseStatus(HttpStatus.BAD_REQUEST)
class RaquetaConflictIntegrityException(message: String) : RaquetaException(message)