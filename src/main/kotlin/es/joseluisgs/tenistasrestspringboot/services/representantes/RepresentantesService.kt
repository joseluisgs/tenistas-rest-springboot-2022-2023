package es.joseluisgs.tenistasrestspringboot.services.representantes

import com.github.michaelbull.result.Result
import es.joseluisgs.tenistasrestspringboot.errors.RepresentanteError
import es.joseluisgs.tenistasrestspringboot.models.Representante
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import java.util.*

interface RepresentantesService {
    suspend fun findAll(): Flow<Representante>
    suspend fun findById(id: Long): Result<Representante, RepresentanteError>
    suspend fun findByUuid(uuid: UUID): Result<Representante, RepresentanteError>
    suspend fun findByNombre(nombre: String): Flow<Representante>
    suspend fun save(representante: Representante): Result<Representante, RepresentanteError>
    suspend fun update(uuid: UUID, representante: Representante): Result<Representante, RepresentanteError>
    suspend fun delete(representante: Representante): Result<Representante, RepresentanteError>
    suspend fun deleteByUuid(uuid: UUID): Result<Representante, RepresentanteError>
    suspend fun deleteById(id: Long): Result<Representante, RepresentanteError>
    suspend fun findAllPage(pageRequest: PageRequest): Flow<Page<Representante>>
    suspend fun countAll(): Long
}