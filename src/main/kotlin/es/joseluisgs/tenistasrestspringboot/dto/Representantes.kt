package es.joseluisgs.tenistasrestspringboot.dto

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
    val createdAt: LocalDateTime? = LocalDateTime.now()
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
        val createdAt: LocalDateTime? = LocalDateTime.now(),
        val updatedAt: LocalDateTime? = LocalDateTime.now(),
        val deleted: Boolean = false
    )
}

data class RepresentanteRequestDto(
    val nombre: String,
    val email: String,
)