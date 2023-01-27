package es.joseluisgs.tenistasrestspringboot.repositories.representantes

import es.joseluisgs.tenistasrestspringboot.models.Representante
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import java.util.*

interface RepresentantesCachedRepository {
    suspend fun findAll(): Flow<Representante>
    suspend fun findById(id: Long): Representante?
    suspend fun findByUuid(uuid: UUID): Representante?
    suspend fun findByNombre(nombre: String): Flow<Representante>
    suspend fun save(representante: Representante): Representante
    suspend fun update(uuid: UUID, representante: Representante): Representante?
    suspend fun delete(representante: Representante): Representante?
    suspend fun deleteByUuid(uuid: UUID): Representante?
    suspend fun deleteById(id: Long)
    suspend fun findAllPage(pageRequest: PageRequest): Flow<Page<Representante>>

    suspend fun countAll(): Long
}