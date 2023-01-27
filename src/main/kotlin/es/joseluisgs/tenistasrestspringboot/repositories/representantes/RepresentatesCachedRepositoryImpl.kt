package es.joseluisgs.tenistasrestspringboot.repositories.representantes

import es.joseluisgs.tenistasrestspringboot.models.Representante
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

private val logger = KotlinLogging.logger {}

@Repository
class RepresentatesCachedRepositoryImpl constructor(
    @Autowired
    private val representantRepository: RepresentantesRepository
) : RepresentantesCachedRepository {

    init {
        logger.info { "Iniciando Repositorio Cache de Representates" }
    }

    override suspend fun findAll(): Flow<Representante> = withContext(Dispatchers.IO) {
        logger.info { "Buscando todos los representantes en Repositorio" }
        return@withContext representantRepository.findAll()
    }

    @Cacheable("representantes")
    override suspend fun findById(id: Long): Representante? = withContext(Dispatchers.IO) {
        logger.info { "Buscando un representante por id en Repositorio" }
        return@withContext representantRepository.findById(id)
    }

    @Cacheable("representantes")
    override suspend fun findByUuid(uuid: UUID): Representante? = withContext(Dispatchers.IO) {
        logger.info { "Buscando un representante por uuid en Repositorio" }
        return@withContext representantRepository.findByUuid(uuid).firstOrNull()
    }

    override suspend fun findByNombre(nombre: String): Flow<Representante> = withContext(Dispatchers.IO) {
        logger.info { "Buscando un representante por nombre en Repositorio" }
        return@withContext representantRepository.findByNombre(nombre)
    }

    @CachePut("representantes")
    override suspend fun save(representante: Representante): Representante = withContext(Dispatchers.IO) {
        logger.info { "Guardando un representante en Repositorio" }
        return@withContext representantRepository.save(representante)
    }

    @CachePut("representantes")
    override suspend fun update(uuid: UUID, representante: Representante): Representante? =
        withContext(Dispatchers.IO) {
            logger.info { "Actualizando un representante en Repositorio" }
            val representanteDB = representantRepository.findByUuid(uuid).firstOrNull()
            representanteDB?.let {
                val updated = it.copy(
                    uuid = it.uuid,
                    createdAt = it.createdAt,
                    updatedAt = LocalDateTime.now()
                )
                return@withContext representantRepository.save(updated)
            }
            return@withContext null
        }

    @CacheEvict("representantes")
    override suspend fun deleteById(id: Long) = withContext(Dispatchers.IO) {
        logger.info { "Borrando un representante por id en Repositorio" }
        representantRepository.deleteById(id)
    }

    override suspend fun deleteByUuid(uuid: UUID): Representante? = withContext(Dispatchers.IO) {
        logger.info { "Borrando un representante por uuid en Repositorio" }
        val representanteDB = representantRepository.findByUuid(uuid).firstOrNull()
        representanteDB?.let {
            representantRepository.deleteById(it.id!!)
            return@withContext it
        }
        return@withContext null
    }

    @CacheEvict("representantes")
    override suspend fun delete(representante: Representante): Representante? = withContext(Dispatchers.IO) {
        logger.info { "Borrando un representante en Repositorio" }
        val representanteDB = representantRepository.findByUuid(representante.uuid).firstOrNull()
        representanteDB?.let {
            representantRepository.deleteById(it.id!!)
            return@withContext it
        }
        return@withContext null
    }

    @CacheEvict("representantes", allEntries = true)
    suspend fun deleteAll() = withContext(Dispatchers.IO) {
        logger.info { "Borrando todos los representantes en Repositorio" }
        representantRepository.deleteAll()
    }
}