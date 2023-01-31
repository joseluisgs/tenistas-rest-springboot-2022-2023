package es.joseluisgs.tenistasrestspringboot.models

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.*

@Table(name = "RAQUETAS")
data class Raqueta(
    // Identificador, si usamos Spring Data Reactive con H2, los uuid fallan, por eso usamos Long
    @Id
    val id: Long? = null,

    val uuid: UUID = UUID.randomUUID(),

    // Datos
    @NotEmpty(message = "La marca no puede estar vacía")
    val marca: String,
    // No negative
    @Min(value = 0, message = "El precio no puede ser negativo")
    val precio: Double,

    // Relaciones
    @NotNull(message = "El representante no puede ser nulo")
    @Column("representante_id")
    val representanteId: UUID, // UUID del representante

    // Historicos y metadata
    @Column("created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @Column("updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val deleted: Boolean = false // Para el borrado lógico si es necesario

)