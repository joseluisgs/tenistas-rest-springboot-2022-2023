package es.joseluisgs.tenistasrestspringboot.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import java.time.LocalDateTime
import java.util.*

/**
 * Representante DTO para paginas de datos
 */
data class RepresentantesPageDto(
    val content: List<RepresentanteDto>,
    val currentPage: Int,
    val pageSize: Int,
    val totalPages: Long,
    val totalElements: Long,
    val sort: String,
    val createdAt: String = LocalDateTime.now().toString()
)

/**
 * Representante DTO
 */
data class RepresentanteDto(
    val id: UUID,
    val nombre: String,
    val email: String,
    val metadata: MetaData? = null,
) {
    data class MetaData(
        val createdAt: String = LocalDateTime.now().toString(),
        val updatedAt: String = LocalDateTime.now().toString(),
        val deleted: Boolean = false
    )
}

/**
 * Representante DTO para crear
 */
data class RepresentanteRequestDto(
    @NotEmpty(message = "El nombre no puede estar vacío")
    val nombre: String,
    @Email(message = "El email debe ser válido")
    val email: String,
)