package es.joseluisgs.tenistasrestspringboot.services.raquetas

import es.joseluisgs.tenistasrestspringboot.models.Raqueta
import es.joseluisgs.tenistasrestspringboot.models.Representante
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import java.util.*

interface RaquetasService {
    suspend fun findAll(): Flow<Raqueta>
    suspend fun findById(id: Long): Raqueta
    suspend fun findByUuid(uuid: UUID): Raqueta
    suspend fun findByMarca(marca: String): Flow<Raqueta>
    suspend fun save(raqueta: Raqueta): Raqueta
    suspend fun update(uuid: UUID, raqueta: Raqueta): Raqueta
    suspend fun delete(raqueta: Raqueta): Raqueta
    suspend fun deleteByUuid(uuid: UUID): Raqueta
    suspend fun deleteById(id: Long)
    suspend fun findAllPage(pageRequest: PageRequest): Flow<Page<Raqueta>>
    suspend fun countAll(): Long
    suspend fun findRepresentante(id: UUID): Representante
}