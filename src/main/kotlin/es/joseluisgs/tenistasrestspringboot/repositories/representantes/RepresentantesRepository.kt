package es.joseluisgs.tenistasrestspringboot.repositories.representantes

import es.joseluisgs.tenistasrestspringboot.models.Representante
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RepresentantesRepository : CoroutineCrudRepository<Representante, Long> {

    fun findByUuid(uuid: UUID): Flow<Representante>
    fun findByNombre(nombre: String): Flow<Representante>
}