package es.joseluisgs.tenistasrestspringboot.services.representantes

import es.joseluisgs.tenistasrestspringboot.exceptions.RepresentanteNotFoundException
import es.joseluisgs.tenistasrestspringboot.models.Representante
import es.joseluisgs.tenistasrestspringboot.repositories.representantes.RepresentantesCachedRepository
import kotlinx.coroutines.flow.Flow
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.util.*

private val logger = KotlinLogging.logger {}

@Service
class RepresentanteServiceImpl constructor(
    private val repository: RepresentantesCachedRepository
) : RepresentantesService {

    init {
        logger.info { "Iniciando Servicio de Representantes" }
    }

    override suspend fun findAll(): Flow<Representante> {
        logger.info { "Buscando todos los representantes en Servicio" }

        return repository.findAll()
    }

    override suspend fun findById(id: Long): Representante {
        logger.debug { "findById: Buscando representante en servicio con id: $id" }

        // return repository.findById(id) ?: throw NoSuchElementException("No se ha encontrado el representante con id: $id")
        return repository.findById(id)
            ?: throw RepresentanteNotFoundException("No se ha encontrado el representante con id: $id")
    }

    override suspend fun findByUuid(uuid: UUID): Representante {
        logger.debug { "findById: Buscando representante en servicio con uuid: $uuid" }

        // return repository.findByUuid(uuid) ?: throw NoSuchElementException("No se ha encontrado el representante con uuid: $uuid")
        return repository.findByUuid(uuid)
            ?: throw RepresentanteNotFoundException("No se ha encontrado el representante con uuid: $uuid")
    }

    override suspend fun findByNombre(nombre: String): Flow<Representante> {
        logger.debug { "findById: Buscando representante en servicio con nombre: $nombre" }

        return repository.findByNombre(nombre)
    }

    override suspend fun save(representante: Representante): Representante {
        logger.debug { "save: Guardando representante en servicio: $representante" }

        return repository.save(representante)
    }

    override suspend fun update(uuid: UUID, representante: Representante): Representante {
        logger.debug { "update: Actualizando representante en servicio: $representante" }

        return repository.update(uuid, representante)
            ?: throw RepresentanteNotFoundException("No se ha encontrado el representante con uuid: $uuid")
    }

    override suspend fun delete(representante: Representante): Representante {
        logger.debug { "delete: Borrando representante en servicio: $representante" }

        return repository.delete(representante)
            ?: throw RepresentanteNotFoundException("No se ha encontrado el representante con uuid: ${representante.uuid}")
    }

    override suspend fun deleteByUuid(uuid: UUID): Representante {
        logger.debug { "deleteByUuid: Borrando representante en servicio con uuid: $uuid" }

        return repository.deleteByUuid(uuid)
            ?: throw RepresentanteNotFoundException("No se ha encontrado el representante con uuid: $uuid")
    }

    override suspend fun deleteById(id: Long) {
        logger.debug { "deleteById: Borrando representante en servicio con id: $id" }

        repository.deleteById(id)
    }
}