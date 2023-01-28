package es.joseluisgs.tenistasrestspringboot.models

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotEmpty
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

/**
 * Representante Model
 */
@Table(name = "TENISTAS")
data class Tenista(
    // Identificador, si usamos Spring Data Reactive con H2, los uuid fallan, por eso usamos Long
    @Id
    val id: Long? = null,

    val uuid: UUID = UUID.randomUUID(),

    // Datos
    @NotEmpty(message = "El nombre no puede estar vacío")
    var nombre: String,
    @Min(value = 1, message = "El número de la línea debe ser mayor que 0")
    var ranking: Int,
    @Column("fecha_nacimiento")
    var fechaNacimiento: LocalDate,
    @Min(value = 1, message = "El año debe ser mayor mayor que 0")
    @Column("año_profesional")
    var añoProfesional: Int,
    @Min(value = 1, message = "La altura debe ser mayor que 0")
    var altura: Int,
    @Min(value = 1, message = "El peso debe ser mayor que 0")
    var peso: Int,
    @Column("mano_dominante")
    var manoDominante: ManoDominante,
    @Column("tipo_reves")
    var tipoReves: TipoReves,
    @Min(value = 1, message = "Los puntos deben ser mayor que 0")
    var puntos: Int,
    @NotEmpty(message = "El país no puede estar vacío")
    var pais: String,
    var raquetaId: UUID? = null, // No tiene por que tener raqueta

    // Historicos y metadata
    @Column("created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @Column("updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val deleted: Boolean = false // Para el borrado lógico si es necesario
) {

    // ENUMS de la propia clase
    enum class ManoDominante {
        DERECHA, IZQUIERDA
    }

    enum class TipoReves {
        UNA_MANO, DOS_MANOS
    }


}