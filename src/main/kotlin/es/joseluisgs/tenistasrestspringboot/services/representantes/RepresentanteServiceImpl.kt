package es.joseluisgs.tenistasrestspringboot.services.representantes

import es.joseluisgs.tenistasrestspringboot.exceptions.RepresentanteNotFoundException
import es.joseluisgs.tenistasrestspringboot.models.Representante
import es.joseluisgs.tenistasrestspringboot.repositories.representantes.RepresentantesCachedRepository
import kotlinx.coroutines.flow.Flow
import mu.KotlinLogging
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.util.*

private val logger = KotlinLogging.logger {}

@Service
class RepresentanteServiceImpl constructor(
    private val representantesRepository: RepresentantesCachedRepository
) : RepresentantesService {

    init {
        logger.info { "Iniciando Servicio de Representantes" }
    }

    override suspend fun findAll(): Flow<Representante> {
        logger.info { "Servicio de representantes findAll" }

        return representantesRepository.findAll()
    }

    override suspend fun findById(id: Long): Representante {
        logger.debug { "Servicio de representantes findById con id: $id" }

        return representantesRepository.findById(id)
            ?: throw RepresentanteNotFoundException("No se ha encontrado el representante con id: $id")
    }

    override suspend fun findByUuid(uuid: UUID): Representante {
        logger.debug { "Servicio de representantes findByUuid con uuid: $uuid" }

        // return repository.findByUuid(uuid) ?: throw NoSuchElementException("No se ha encontrado el representante con uuid: $uuid")
        return representantesRepository.findByUuid(uuid)
            ?: throw RepresentanteNotFoundException("No se ha encontrado el representante con uuid: $uuid")
    }

    override suspend fun findByNombre(nombre: String): Flow<Representante> {
        logger.debug { "Servicio de representantes findByNombre con nombre: $nombre" }

        return representantesRepository.findByNombre(nombre)
    }

    override suspend fun save(representante: Representante): Representante {
        logger.debug { "Servicio de representantes save representante: $representante" }

        return representantesRepository.save(representante)
    }

    override suspend fun update(uuid: UUID, representante: Representante): Representante {
        logger.debug { "Servicio de representantes update representante con id: $uuid " }

        return representantesRepository.update(uuid, representante)
            ?: throw RepresentanteNotFoundException("No se ha encontrado el representante con uuid: $uuid")
    }

    override suspend fun delete(representante: Representante): Representante {
        logger.debug { "Servicio de representantes delete representante: $representante" }

        return representantesRepository.delete(representante)
            ?: throw RepresentanteNotFoundException("No se ha encontrado el representante con uuid: ${representante.uuid}")
    }

    override suspend fun deleteByUuid(uuid: UUID): Representante {
        logger.debug { "Servicio de representantes deleteByUuid con uuid: $uuid" }

        return representantesRepository.deleteByUuid(uuid)
            ?: throw RepresentanteNotFoundException("No se ha encontrado el representante con uuid: $uuid")
    }

    override suspend fun deleteById(id: Long) {
        logger.debug { "Servicio de representantes deleteById con id: $id" }

        representantesRepository.deleteById(id)
    }

    override suspend fun findAllPage(pageRequest: PageRequest): Flow<Page<Representante>> {
        logger.debug { "Servicio de representantes findAllPage con pageRequest: $pageRequest" }

        return representantesRepository.findAllPage(pageRequest)
    }

    override suspend fun countAll(): Long {
        logger.debug { "Servicio de representantes countAll" }

        return representantesRepository.countAll()
    }

}