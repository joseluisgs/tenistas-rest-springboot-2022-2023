package es.joseluisgs.tenistasrestspringboot.dto

import es.joseluisgs.tenistasrestspringboot.models.Tenista
import java.time.LocalDateTime
import java.util.*

data class TenistasPageDto(
    val content: List<TenistaDto>,
    val currentPage: Int,
    val pageSize: Int,
    val totalPages: Long,
    val totalElements: Long,
    val sort: String,
    val createdAt: String = LocalDateTime.now().toString()
)

data class TenistaCreateDto(
    val nombre: String,
    val ranking: Int,
    val fechaNacimiento: String,
    val añoProfesional: Int,
    val altura: Int,
    val peso: Int,
    val manoDominante: Tenista.ManoDominante? = Tenista.ManoDominante.DERECHA,
    val tipoReves: Tenista.TipoReves? = Tenista.TipoReves.DOS_MANOS,
    val puntos: Int,
    val pais: String,
    val raquetaId: UUID? = null,
)

data class TenistaDto(
    val id: UUID? = null,
    val nombre: String,
    val ranking: Int,
    val fechaNacimiento: String = LocalDateTime.now().toString(),
    val añoProfesional: Int,
    val altura: Int,
    val peso: Int,
    val manoDominante: Tenista.ManoDominante,
    val tipoReves: Tenista.TipoReves,
    val puntos: Int,
    val pais: String,
    val raqueta: RaquetaTenistaDto?,
    val metadata: MetaData? = null,
) {
    data class MetaData(
        val createdAt: String = LocalDateTime.now().toString(),
        val updatedAt: String = LocalDateTime.now().toString(),
        val deleted: Boolean = false
    )
}
