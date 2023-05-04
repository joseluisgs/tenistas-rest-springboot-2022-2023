package es.joseluisgs.tenistasrestspringboot.validators

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import es.joseluisgs.tenistasrestspringboot.dto.TenistaCreateDto
import es.joseluisgs.tenistasrestspringboot.errors.TenistaError
import java.time.LocalDate


fun TenistaCreateDto.validate(): Result<TenistaCreateDto, TenistaError> {
    if (this.nombre.isBlank()) {
        return Err(TenistaError.BadRequest("El nombre no puede estar vacío"))
    } else if (this.ranking <= 0) {
        return Err(TenistaError.BadRequest("El ranking debe ser mayor que 0"))
    } else if (LocalDate.parse(this.fechaNacimiento).isAfter(LocalDate.now())) {
        return Err(TenistaError.BadRequest("La fecha de nacimiento no puede ser posterior a la actual"))
    } else if (this.añoProfesional <= 0) {
        return Err(TenistaError.BadRequest("El año profesional debe ser mayor que 0"))
    } else if (this.altura <= 0) {
        return Err(TenistaError.BadRequest("La altura debe ser mayor que 0"))
    } else if (this.peso <= 0) {
        return Err(TenistaError.BadRequest("El peso debe ser mayor que 0"))
    } else if (this.puntos <= 0) {
        return Err(TenistaError.BadRequest("Los puntos deben ser mayor que 0"))
    } else if (this.pais.isBlank()) {
        return Err(TenistaError.BadRequest("El país no puede estar vacío"))
    }
    return Ok(this)
}