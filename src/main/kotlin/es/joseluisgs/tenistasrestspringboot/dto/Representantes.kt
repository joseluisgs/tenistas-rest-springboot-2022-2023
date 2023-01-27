package es.joseluisgs.tenistasrestspringboot.dto

import java.time.LocalDateTime
import java.util.*

/**
 * Representante DTO para paginas de datos
 */
data class RepresentantesPageDto(
    val page: Int,
    val perPage: Int,
    val data: List<RepresentanteDto>,
    val createdAt: LocalDateTime? = LocalDateTime.now()
)

/**
 * Representante DTO
 */
data class RepresentanteDto(
    val uuid: UUID? = null,
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