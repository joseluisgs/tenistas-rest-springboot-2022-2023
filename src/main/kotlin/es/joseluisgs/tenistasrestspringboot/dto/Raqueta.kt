package es.joseluisgs.tenistasrestspringboot.dto

import java.time.LocalDateTime
import java.util.*

data class RaquetasPageDto(
    val page: Int,
    val perPage: Int,
    val data: List<RaquetaDto>,
    val createdAt: String = LocalDateTime.now().toString()
)

data class RaquetaCreateDto(
    val marca: String,
    val precio: Double,
    val representanteId: UUID,
)

data class RaquetaDto(
    val id: UUID? = null,
    val marca: String,
    val precio: Double,
    val represetante: RepresentanteDto,
    val metadata: MetaData? = null,
) {

    data class MetaData(
        val createdAt: String? = LocalDateTime.now().toString(),
        val updatedAt: String? = LocalDateTime.now().toString(),
        val deleted: Boolean = false
    )
}

data class RaquetaTenistaDto(
    val id: UUID? = null,
    val marca: String,
    val precio: Double,
    val represetanteId: UUID? = null,
    val metadata: MetaData? = null,
) {
    data class MetaData(
        val createdAt: String? = LocalDateTime.now().toString(),
        val updatedAt: String? = LocalDateTime.now().toString(),
        val deleted: Boolean = false
    )
}
