package es.joseluisgs.tenistasrestspringboot.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

sealed class RaquetaException(message: String) : RuntimeException(message)

@ResponseStatus(HttpStatus.BAD_REQUEST)
class RaquetaConflictIntegrityException(message: String) : RaquetaException(message)
