package es.joseluisgs.tenistasrestspringboot.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

// Vamos a tipificar las excepciones y a crear una jerarquía de excepciones
/**
 * RepresentanteException
 * @param message: String Mensaje de la excepción
 */
sealed class RepresentanteException(message: String) : RuntimeException(message)

@ResponseStatus(HttpStatus.NOT_FOUND)
class RepresentanteNotFoundException(message: String) : RepresentanteException(message)

@ResponseStatus(HttpStatus.BAD_REQUEST)
class RepresentanteBadRequestException(message: String) : RepresentanteException(message)

@ResponseStatus(HttpStatus.BAD_REQUEST)
class RepresentanteConflictIntegrityException(message: String) : RepresentanteException(message)