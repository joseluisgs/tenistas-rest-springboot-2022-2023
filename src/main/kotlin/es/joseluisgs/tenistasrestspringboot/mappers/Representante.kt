package es.joseluisgs.tenistasrestspringboot.mappers

import es.joseluisgs.tenistasrestspringboot.dto.RepresentanteDto
import es.joseluisgs.tenistasrestspringboot.dto.RepresentanteRequestDto
import es.joseluisgs.tenistasrestspringboot.models.Representante

/**
 * Transformamos un Representante en un RepresentanteDto
 * @receiver Representante
 * @return RepresentanteDto
 */
fun Representante.toDto() = RepresentanteDto(
    id = this.uuid, // cambio el id por el uuid, pero para el dto es id
    nombre = this.nombre,
    email = this.email,
    metadata = RepresentanteDto.MetaData(
        createdAt = this.createdAt.toString(),
        updatedAt = this.updatedAt.toString(),
        deleted = this.deleted // Solo se verá en el Json si es true
    )
)

/**
 * Transformamos un RepresentanteDto en un Representante
 * @receiver RepresentanteDto
 * @return Representante
 */
fun RepresentanteRequestDto.toModel() = Representante(
    nombre = this.nombre,
    email = this.email
)
