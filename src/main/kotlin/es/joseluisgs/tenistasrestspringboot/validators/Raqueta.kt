package es.joseluisgs.tenistasrestspringboot.validators

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import es.joseluisgs.tenistasrestspringboot.dto.RaquetaCreateDto
import es.joseluisgs.tenistasrestspringboot.errors.RaquetaError


fun RaquetaCreateDto.validate(): Result<RaquetaCreateDto, RaquetaError> {
    if (this.marca.isBlank())
        return Err(RaquetaError.BadRequest("La marca no puede estar vacía"))
    if (this.precio < 0.0)
        return Err(RaquetaError.BadRequest("El precio no puede ser negativo"))
    return Ok(this)
}