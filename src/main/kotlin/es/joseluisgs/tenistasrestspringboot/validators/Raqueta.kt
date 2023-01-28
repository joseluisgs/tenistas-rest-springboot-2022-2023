package es.joseluisgs.tenistasrestspringboot.validators

import es.joseluisgs.tenistasrestspringboot.dto.RaquetaCreateDto
import es.joseluisgs.tenistasrestspringboot.exceptions.RaquetaBadRequestException


fun RaquetaCreateDto.validate(): RaquetaCreateDto {
    if (this.marca.isBlank())
        throw RaquetaBadRequestException("La marca no puede estar vacía")
    if (this.precio < 0.0)
        throw RaquetaBadRequestException("El precio no puede ser negativo")
    return this
}