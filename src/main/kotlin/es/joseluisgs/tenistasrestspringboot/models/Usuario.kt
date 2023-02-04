package es.joseluisgs.tenistasrestspringboot.models

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
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

    @get:JvmName("userName") // Para que no se llame getUsername
    val username: String,
    val email: String,

    @get:JvmName("userPassword") // Para que no se llame getPassword
    val password: String,

    val avatar: String,

    // Conjunto de permisos que tiene
    val rol: Rol = Rol.USER,

    // Historicos y metadata
    @Column("created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @Column("updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val deleted: Boolean = false, // Para el borrado lógico si es necesario
    @Column("last_password_change_at")
    val lastPasswordChangeAt: LocalDateTime = LocalDateTime.now()
) : UserDetails {

    enum class Rol {
        USER,  // Normal
        ADMIN // Administrador
    }

    // transformamos el conjunto de roles en una lista de GrantedAuthority
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        val ga = SimpleGrantedAuthority("ROLE_" + rol.name)
        return mutableListOf<GrantedAuthority>(ga)
    }

    override fun getPassword(): String {
        return password
    }

    override fun getUsername(): String {
        return username
    }


    /*override fun getPassword(): String {
        return password
    }

    override fun getUsername(): String {
        return username
    }
*/
    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

}