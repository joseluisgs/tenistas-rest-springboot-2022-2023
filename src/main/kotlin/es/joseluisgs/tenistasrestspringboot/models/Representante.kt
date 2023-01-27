package es.joseluisgs.tenistasrestspringboot.models

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.*

/**
 * Representante Model
 */
@Table(name = "REPRESENTANTES")
data class Representante(
    // Identificador, si usamos Spring Data Reactive con H2, los uuid fallan, por eso usamos Long
    @Id
    val id: Long? = null,

    val uuid: UUID = UUID.randomUUID(),


    // Datos
    @NotEmpty(message = "El nombre no puede estar vacío")
    val nombre: String,
    @Email(regexp = ".*@.*\\..*", message = "Email debe ser un email valido")
    val email: String,

    // Historicos y metadata
    @Column("created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @Column("updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val deleted: Boolean = false // Para el borrado lógico si es necesario
)