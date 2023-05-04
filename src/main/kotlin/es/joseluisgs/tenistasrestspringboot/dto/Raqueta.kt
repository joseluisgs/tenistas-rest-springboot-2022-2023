package es.joseluisgs.tenistasrestspringboot.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotEmpty
import java.time.LocalDateTime
import java.util.*

data class RaquetasPageDto(
    val content: List<RaquetaDto>,
    val currentPage: Int,
    val pageSize: Int,
    val totalPages: Long,
    val totalElements: Long,
    val sort: String,
    val createdAt: String = LocalDateTime.now().toString()
)

data class RaquetaCreateDto(
    @NotEmpty(message = "La marca no puede estar vacía")
    val marca: String,
    @Min(value = 0, message = "El precio no puede ser negativo")
    val precio: Double,
    val representanteId: UUID,
)

data class RaquetaDto(
    val id: UUID,
    val marca: String,
    val precio: Double,
    val representante: RepresentanteDto,
    val metadata: MetaData? = null,
) {

    data class MetaData(
        val createdAt: String? = LocalDateTime.now().toString(),
        val updatedAt: String? = LocalDateTime.now().toString(),
        val deleted: Boolean = false
    )
}

data class RaquetaTenistaDto(
    val id: UUID,
    val marca: String,
    val precio: Double,
    val representanteId: UUID? = null,
    val metadata: MetaData? = null,
) {
    data class MetaData(
        val createdAt: String? = LocalDateTime.now().toString(),
        val updatedAt: String? = LocalDateTime.now().toString(),
        val deleted: Boolean = false
    )
}
