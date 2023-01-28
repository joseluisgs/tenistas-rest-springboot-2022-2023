package es.joseluisgs.tenistasrestspringboot.mappers

import es.joseluisgs.tenistasrestspringboot.dto.TenistaCreateDto
import es.joseluisgs.tenistasrestspringboot.dto.TenistaDto
import es.joseluisgs.tenistasrestspringboot.models.Raqueta
import es.joseluisgs.tenistasrestspringboot.models.Tenista
import java.time.LocalDate

fun Tenista.toDto(raqueta: Raqueta?) = TenistaDto(
    id = this.uuid,
    nombre = this.nombre,
    ranking = this.ranking,
    fechaNacimiento = this.fechaNacimiento.toString(),
    añoProfesional = this.añoProfesional,
    altura = this.altura,
    peso = this.peso,
    manoDominante = this.manoDominante,
    tipoReves = this.tipoReves,
    puntos = this.puntos,
    pais = this.pais,
    raqueta = raqueta?.toTenistaDto(),
    metadata = TenistaDto.MetaData(
        createdAt = this.createdAt.toString(),
        updatedAt = this.updatedAt.toString(),
        deleted = this.deleted // Solo se verá en el Json si es true
    )
)

fun TenistaCreateDto.toModel() = Tenista(
    nombre = this.nombre,
    ranking = this.ranking,
    fechaNacimiento = LocalDate.parse(this.fechaNacimiento),
    añoProfesional = this.añoProfesional,
    altura = this.altura,
    peso = this.peso,
    manoDominante = this.manoDominante ?: Tenista.ManoDominante.DERECHA,
    tipoReves = this.tipoReves ?: Tenista.TipoReves.DOS_MANOS,
    puntos = this.puntos,
    pais = this.pais,
    raquetaId = this.raquetaId,
)
