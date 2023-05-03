package es.joseluisgs.tenistasrestspringboot.services.raquetas

import com.github.michaelbull.result.Result
import es.joseluisgs.tenistasrestspringboot.errors.RaquetaError
import es.joseluisgs.tenistasrestspringboot.models.Raqueta
import es.joseluisgs.tenistasrestspringboot.models.Representante
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import java.util.*

interface RaquetasService {
    suspend fun findAll(): Flow<Raqueta>
    suspend fun findById(id: Long): Result<Raqueta, RaquetaError>
    suspend fun findByUuid(uuid: UUID): Result<Raqueta, RaquetaError>
    suspend fun findByMarca(marca: String): Flow<Raqueta>
    suspend fun save(raqueta: Raqueta): Result<Raqueta, RaquetaError>
    suspend fun update(uuid: UUID, raqueta: Raqueta): Result<Raqueta, RaquetaError>
    suspend fun delete(raqueta: Raqueta): Result<Raqueta, RaquetaError>
    suspend fun deleteByUuid(uuid: UUID): Result<Raqueta, RaquetaError>
    suspend fun deleteById(id: Long): Result<Raqueta, RaquetaError>
    suspend fun findAllPage(pageRequest: PageRequest): Flow<Page<Raqueta>>
    suspend fun countAll(): Long
    suspend fun findRepresentante(id: UUID): Result<Representante, RaquetaError>
}