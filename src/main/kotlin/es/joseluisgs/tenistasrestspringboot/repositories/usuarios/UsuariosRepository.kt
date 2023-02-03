package es.joseluisgs.tenistasrestspringboot.repositories.usuarios

import es.joseluisgs.tenistasrestspringboot.models.Usuario
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UsuariosRepository : CoroutineCrudRepository<Usuario, Long> {
    fun findByUuid(uuid: UUID): Flow<Usuario>
    fun findByUsername(username: String): Flow<Usuario>
}