package es.joseluisgs.tenistasrestspringboot.validators

import es.joseluisgs.tenistasrestspringboot.dto.RepresentanteRequestDto
import es.joseluisgs.tenistasrestspringboot.exceptions.RepresentanteBadRequestException

/**
 * Validador de Representantes
 * @see RepresentanteRequestDto
 */
fun RepresentanteRequestDto.validate(): RepresentanteRequestDto {
    if (this.nombre.isBlank())
        throw RepresentanteBadRequestException("El nombre no puede estar vacío")
    if (this.email.isBlank() || !this.email.matches(Regex("^[A-Za-z0-9+_.-]+@(.+)\$")))
        throw RepresentanteBadRequestException("El email no puede estar vacío o no tiene el formato correcto")

    return this
}