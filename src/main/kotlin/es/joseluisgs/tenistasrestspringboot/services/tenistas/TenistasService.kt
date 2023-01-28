package es.joseluisgs.tenistasrestspringboot.services.tenistas

import es.joseluisgs.tenistasrestspringboot.models.Raqueta
import es.joseluisgs.tenistasrestspringboot.models.Tenista
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import java.util.*

interface TenistasService {
    suspend fun findAll(): Flow<Tenista>
    suspend fun findById(id: Long): Tenista
    suspend fun findByUuid(uuid: UUID): Tenista
    suspend fun findByNombre(nombre: String): Flow<Tenista>
    suspend fun findByRanking(ranking: Int): Tenista
    suspend fun save(tenista: Tenista): Tenista
    suspend fun update(uuid: UUID, tenista: Tenista): Tenista
    suspend fun delete(tenista: Tenista): Tenista
    suspend fun deleteByUuid(uuid: UUID): Tenista
    suspend fun deleteById(id: Long)
    suspend fun findAllPage(pageRequest: PageRequest): Flow<Page<Tenista>>
    suspend fun countAll(): Long
    suspend fun findRaqueta(id: UUID?): Raqueta?
}