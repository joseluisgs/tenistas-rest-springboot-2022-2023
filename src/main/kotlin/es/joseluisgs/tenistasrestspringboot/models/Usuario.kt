package es.joseluisgs.tenistasrestspringboot.models

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.*


@Table(name = "USUARIOS")
data class Usuario(
    // Identificador, si usamos Spring Data Reactive con H2, los uuid fallan, por eso usamos Long
    @Id
    val id: Long? = null,

    val uuid: UUID = UUID.randomUUID(),

    // Datos
    val nombre: String,
    val username: String,
    val email: String,
    val password: String,
    val avatar: String,
    // Conjunto de permisos que tiene
    val roles: Set<Rol> = setOf(Rol.USER),

    // Historicos y metadata
    @Column("created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @Column("updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val deleted: Boolean = false, // Para el borrado lógico si es necesario
    @Column("last_password_change_at")
    val lastPasswordChangeAt: LocalDateTime = LocalDateTime.now()
) {

    enum class Rol {
        USER,  // Normal
        ADMIN // Administrador
    }

}