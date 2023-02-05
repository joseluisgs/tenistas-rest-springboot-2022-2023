package es.joseluisgs.tenistasrestspringboot.validators

import es.joseluisgs.tenistasrestspringboot.dto.UsuarioCreateDto
import es.joseluisgs.tenistasrestspringboot.dto.UsuarioUpdateDto
import es.joseluisgs.tenistasrestspringboot.exceptions.UsuariosBadRequestException

fun UsuarioCreateDto.validate(): UsuarioCreateDto {
    if (this.nombre.isBlank()) {
        throw UsuariosBadRequestException("El nombre no puede estar vacío")
    } else if (this.email.isBlank() || !this.email.matches(Regex("^[A-Za-z0-9+_.-]+@(.+)\$")))
        throw UsuariosBadRequestException("El email no puede estar vacío o no tiene el formato correcto")
    else if (this.username.isBlank())
        throw UsuariosBadRequestException("El username no puede estar vacío")
    else if (this.password.isBlank() || this.password.length < 5)
        throw UsuariosBadRequestException("El password no puede estar vacío o ser menor de 5 caracteres")

    return this
}

fun UsuarioUpdateDto.validate(): UsuarioUpdateDto {
    if (this.nombre.isBlank()) {
        throw UsuariosBadRequestException("El nombre no puede estar vacío")
    } else if (this.email.isBlank() || !this.email.matches(Regex("^[A-Za-z0-9+_.-]+@(.+)\$")))
        throw UsuariosBadRequestException("El email no puede estar vacío o no tiene el formato correcto")
    else if (this.username.isBlank())
        throw UsuariosBadRequestException("El username no puede estar vacío")

    return this
}