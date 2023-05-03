package es.joseluisgs.tenistasrestspringboot.services.tenistas

import com.github.michaelbull.result.Result
import es.joseluisgs.tenistasrestspringboot.errors.TenistaError
import es.joseluisgs.tenistasrestspringboot.models.Raqueta
import es.joseluisgs.tenistasrestspringboot.models.Tenista
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import java.util.*

interface TenistasService {
    suspend fun findAll(): Flow<Tenista>
    suspend fun findById(id: Long): Result<Tenista, TenistaError>
    suspend fun findByUuid(uuid: UUID): Result<Tenista, TenistaError>
    suspend fun findByNombre(nombre: String): Flow<Tenista>
    suspend fun findByRanking(ranking: Int): Result<Tenista, TenistaError>
    suspend fun save(tenista: Tenista): Result<Tenista, TenistaError>
    suspend fun update(uuid: UUID, tenista: Tenista): Result<Tenista, TenistaError>
    suspend fun delete(tenista: Tenista): Result<Tenista, TenistaError>
    suspend fun deleteByUuid(uuid: UUID): Result<Tenista, TenistaError>
    suspend fun deleteById(id: Long): Result<Tenista, TenistaError>
    suspend fun findAllPage(pageRequest: PageRequest): Flow<Page<Tenista>>
    suspend fun countAll(): Long
    suspend fun findRaqueta(id: UUID?): Result<Raqueta?, TenistaError>
}