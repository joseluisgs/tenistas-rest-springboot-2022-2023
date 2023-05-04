package es.joseluisgs.tenistasrestspringboot.dto

import es.joseluisgs.tenistasrestspringboot.models.Usuario
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import java.time.LocalDateTime
import java.util.*


data class UsuarioCreateDto(
    @NotEmpty(message = "El nombre no puede estar vacío")
    val nombre: String,
    @Email(message = "El email debe ser válido")
    val email: String,
    @NotEmpty(message = "El username no puede estar vacío")
    val username: String,
    val avatar: String? = null,
    val rol: Set<String> = setOf(Usuario.Rol.USER.name),
    @NotEmpty(message = "El password no puede estar vacío")
    val password: String
)

data class UsuarioLoginDto(
    @NotEmpty(message = "El username no puede estar vacío")
    val username: String,
    @NotEmpty(message = "El password no puede estar vacío")
    val password: String
)

data class UsuarioDto(
    val id: UUID? = null,
    val nombre: String,
    val username: String,
    val email: String,
    val avatar: String,
    val rol: Set<String> = setOf(Usuario.Rol.USER.name),
    val metadata: MetaData? = null,
) {
    data class MetaData(
        val createdAt: LocalDateTime? = LocalDateTime.now(),
        val updatedAt: LocalDateTime? = LocalDateTime.now(),
        val deleted: Boolean = false
    )
}

data class UsuarioUpdateDto(
    @NotEmpty(message = "El nombre no puede estar vacío")
    val nombre: String,
    @Email(message = "El email debe ser válido")
    val email: String,
    @NotEmpty(message = "El username no puede estar vacío")
    val username: String,
)

data class UserWithTokenDto(
    val user: UsuarioDto,
    val token: String
)



