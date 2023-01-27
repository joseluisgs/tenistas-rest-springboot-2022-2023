package es.joseluisgs.tenistasrestspringboot.mappers

import es.joseluisgs.tenistasrestspringboot.dto.RepresentanteDto
import es.joseluisgs.tenistasrestspringboot.models.Representante

/**
 * Transformamos un Representante en un RepresentanteDto
 */
fun Representante.toDto() = RepresentanteDto(
    uuid = this.uuid, // cambio el id por el uuid
    nombre = this.nombre,
    email = this.email,
    metadata = RepresentanteDto.MetaData(
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        deleted = this.deleted // Solo se verá en el Json si es true
    )
)

/**
 * Transformamos un RepresentanteDto en un Representante
 */
fun RepresentanteDto.toModel() = Representante(
    nombre = this.nombre,
    email = this.email
)
