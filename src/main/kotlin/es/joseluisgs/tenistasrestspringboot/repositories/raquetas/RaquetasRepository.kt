package es.joseluisgs.tenistasrestspringboot.repositories.raquetas

import es.joseluisgs.tenistasrestspringboot.models.Raqueta
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RaquetasRepository : CoroutineCrudRepository<Raqueta, Long> {
    fun findByUuid(uuid: UUID): Flow<Raqueta>
    fun findByMarcaContainsIgnoreCase(marca: String): Flow<Raqueta>
    fun findAllBy(pageable: Pageable?): Flow<Raqueta>
}