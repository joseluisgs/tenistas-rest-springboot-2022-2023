package es.joseluisgs.tenistasrestspringboot.validators

import es.joseluisgs.tenistasrestspringboot.dto.TenistaCreateDto
import es.joseluisgs.tenistasrestspringboot.exceptions.TenistaBadRequestException
import java.time.LocalDate


fun TenistaCreateDto.validate(): TenistaCreateDto {
    if (this.nombre.isBlank()) {
        throw TenistaBadRequestException("El nombre no puede estar vacío")
    } else if (this.ranking <= 0) {
        throw TenistaBadRequestException("El ranking debe ser mayor que 0")
    } else if (LocalDate.parse(this.fechaNacimiento).isAfter(LocalDate.now())) {
        throw TenistaBadRequestException("La fecha de nacimiento no puede ser mayor que la actual")
    } else if (this.añoProfesional <= 0) {
        throw TenistaBadRequestException("El año profesional debe ser mayor que 0")
    } else if (this.altura <= 0) {
        throw TenistaBadRequestException("La altura debe ser mayor que 0")
    } else if (this.peso <= 0) {
        throw TenistaBadRequestException("El peso debe ser mayor que 0")
    } else if (this.puntos <= 0) {
        throw TenistaBadRequestException("Los puntos deben ser mayor que 0")
    } else if (this.pais.isBlank()) {
        throw TenistaBadRequestException("El país no puede estar vacío")
    }
    return this
}