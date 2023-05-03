package es.joseluisgs.tenistasrestspringboot.validators

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import es.joseluisgs.tenistasrestspringboot.dto.RepresentanteRequestDto
import es.joseluisgs.tenistasrestspringboot.errors.RepresentanteError

/**
 * Validador de Representantes
 * @see RepresentanteRequestDto
 */
fun RepresentanteRequestDto.validate(): Result<RepresentanteRequestDto, RepresentanteError> {
    if (this.nombre.isBlank())
        return Err(RepresentanteError.BadRequest("El nombre no puede estar vacío"))
    if (this.email.isBlank() || !this.email.matches(Regex("^[A-Za-z0-9+_.-]+@(.+)\$")))
        return Err(RepresentanteError.BadRequest("El email no puede estar vacío y debe ser válido"))

    return Ok(this)
}