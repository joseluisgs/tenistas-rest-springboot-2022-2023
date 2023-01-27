package es.joseluisgs.tenistasrestspringboot.services.representantes

import es.joseluisgs.tenistasrestspringboot.models.Representante
import kotlinx.coroutines.flow.Flow
import java.util.*

interface RepresentantesService {
    suspend fun findAll(): Flow<Representante>
    suspend fun findById(id: Long): Representante
    suspend fun findByUuid(uuid: UUID): Representante
    suspend fun findByNombre(nombre: String): Flow<Representante>
    suspend fun save(representante: Representante): Representante
    suspend fun update(uuid: UUID, representante: Representante): Representante
    suspend fun delete(representante: Representante): Representante
    suspend fun deleteByUuid(uuid: UUID): Representante
    suspend fun deleteById(id: Long)
}