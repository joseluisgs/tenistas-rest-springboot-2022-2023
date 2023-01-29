package es.joseluisgs.tenistasrestspringboot.repositories.tenistas

import es.joseluisgs.tenistasrestspringboot.models.Tenista
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TenistasRepository : CoroutineCrudRepository<Tenista, Long> {
    fun findByUuid(uuid: UUID): Flow<Tenista>
    fun findByNombreContainsIgnoreCase(nombre: String): Flow<Tenista>
    fun findByRanking(ranking: Int): Flow<Tenista>
    fun findAllBy(pageable: Pageable?): Flow<Tenista>
    fun findByOrderByRankingAsc(): Flow<Tenista>
}