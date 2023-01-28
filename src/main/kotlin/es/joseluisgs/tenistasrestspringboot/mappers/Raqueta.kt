package es.joseluisgs.tenistasrestspringboot.mappers

import es.joseluisgs.tenistasrestspringboot.dto.RaquetaCreateDto
import es.joseluisgs.tenistasrestspringboot.dto.RaquetaDto
import es.joseluisgs.tenistasrestspringboot.dto.RaquetaTenistaDto
import es.joseluisgs.tenistasrestspringboot.models.Raqueta
import es.joseluisgs.tenistasrestspringboot.models.Representante

fun Raqueta.toDto(representante: Representante) = RaquetaDto(
    id = this.uuid,
    marca = this.marca,
    precio = this.precio,
    represetante = representante.toDto(),
    metadata = RaquetaDto.MetaData(
        createdAt = this.createdAt.toString(),
        updatedAt = this.updatedAt.toString(),
        deleted = this.deleted // Solo se verá en el Json si es true
    )
)

fun Raqueta.toTenistaDto() = RaquetaTenistaDto(
    id = this.uuid,
    marca = this.marca,
    precio = this.precio,
    represetanteId = this.represetanteId,
    metadata = RaquetaTenistaDto.MetaData(
        createdAt = this.createdAt.toString(),
        updatedAt = this.updatedAt.toString(),
        deleted = this.deleted // Solo se verá en el Json si es true
    )
)

fun RaquetaCreateDto.toModel() = Raqueta(
    marca = this.marca,
    precio = this.precio,
    represetanteId = this.representanteId,
)
