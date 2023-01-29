package es.joseluisgs.tenistasrestspringboot.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

sealed class TenistaException(message: String) : RuntimeException(message)

@ResponseStatus(HttpStatus.NOT_FOUND)
class TenistaNotFoundException(message: String) : TenistaException(message)

@ResponseStatus(HttpStatus.BAD_REQUEST)
class TenistaBadRequestException(message: String) : TenistaException(message)