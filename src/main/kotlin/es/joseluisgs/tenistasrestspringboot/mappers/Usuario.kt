package es.joseluisgs.tenistasrestspringboot.mappers

import es.joseluisgs.tenistasrestspringboot.dto.UsuarioCreateDto
import es.joseluisgs.tenistasrestspringboot.dto.UsuarioDto
import es.joseluisgs.tenistasrestspringboot.models.Usuario

fun Usuario.toDto(): UsuarioDto {
    return UsuarioDto(
        id = this.uuid,
        nombre = this.nombre,
        email = this.email,
        username = this.username,
        avatar = this.avatar,
        rol = this.rol.name,
        metadata = UsuarioDto.MetaData(
            createdAt = this.createdAt,
            updatedAt = this.updatedAt,
            deleted = this.deleted
        )
    )
}

fun UsuarioCreateDto.toModel(): Usuario {
    return Usuario(
        nombre = this.nombre,
        email = this.email,
        username = this.username,
        password = this.password,
        avatar = this.avatar ?: "https://upload.wikimedia.org/wikipedia/commons/f/f4/User_Avatar_2.png",
        rol = Usuario.Rol.valueOf(this.rol ?: "USER")
    )
}