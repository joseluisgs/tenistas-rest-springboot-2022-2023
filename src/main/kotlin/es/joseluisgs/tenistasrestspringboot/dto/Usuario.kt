package es.joseluisgs.tenistasrestspringboot.dto

import es.joseluisgs.tenistasrestspringboot.models.Usuario
import java.time.LocalDateTime
import java.util.*


data class UsuarioCreateDto(
    val nombre: String,
    val email: String,
    val username: String,
    val avatar: String = "https://upload.wikimedia.org/wikipedia/commons/f/f4/User_Avatar_2.png",
    val role: Usuario.Rol = Usuario.Rol.USER,
    val password: String,
    val password2: String,
)

data class UsuarioLoginDto(
    val username: String,
    val password: String
)

data class UsuarioDto(
    val id: UUID? = null,
    val nombre: String,
    val username: String,
    val email: String,
    val avatar: String,
    val roles: Set<String>
) {

    val token: String? = null

    data class MetaData(
        val createdAt: LocalDateTime? = LocalDateTime.now(),
        val updatedAt: LocalDateTime? = LocalDateTime.now(),
        val deleted: Boolean = false
    )
}



