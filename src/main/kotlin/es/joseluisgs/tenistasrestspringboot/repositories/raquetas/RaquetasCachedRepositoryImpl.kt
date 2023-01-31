package es.joseluisgs.tenistasrestspringboot.repositories.raquetas

import es.joseluisgs.tenistasrestspringboot.exceptions.RaquetaConflictIntegrityException
import es.joseluisgs.tenistasrestspringboot.exceptions.RepresentanteConflictIntegrityException
import es.joseluisgs.tenistasrestspringboot.models.Raqueta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

private val logger = KotlinLogging.logger {}

@Repository
class RaquetasCachedRepositoryImpl
@Autowired constructor(
    private val raquetasRepository: RaquetasRepository
) : RaquetasCachedRepository {

    init {
        logger.info { "Iniciando Repositorio Cache de Raquetas" }
    }

    override suspend fun findAll(): Flow<Raqueta> = withContext(Dispatchers.IO) {
        logger.info { "Repositorio de raquetas findAll" }

        return@withContext raquetasRepository.findAll()
    }

    @Cacheable("raquetas")
    override suspend fun findById(id: Long): Raqueta? = withContext(Dispatchers.IO) {
        logger.info { "Repositorio de raquetas findById con id: $id" }

        return@withContext raquetasRepository.findById(id)
    }

    @Cacheable("raquetas")
    override suspend fun findByUuid(uuid: UUID): Raqueta? = withContext(Dispatchers.IO) {
        logger.info { "Repositorio de raquetas findByUuid con uuid: $uuid" }

        return@withContext raquetasRepository.findByUuid(uuid).firstOrNull()
    }

    override suspend fun findByMarca(marca: String): Flow<Raqueta> = withContext(Dispatchers.IO) {
        logger.info { "Repositorio de raquetas findByMarca con marca: $marca" }

        return@withContext raquetasRepository.findByMarcaContainsIgnoreCase(marca)
    }

    @CachePut("raquetas")
    override suspend fun save(raqueta: Raqueta): Raqueta = withContext(Dispatchers.IO) {
        logger.info { "Repositorio de raquetas save raqueta: $raqueta" }

        val saved =
            raqueta.copy(
                uuid = UUID.randomUUID(),
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )

        return@withContext raquetasRepository.save(raqueta)
    }

    @CachePut("raquetas")
    override suspend fun update(uuid: UUID, raqueta: Raqueta): Raqueta? =
        withContext(Dispatchers.IO) {
            logger.info { "Repositorio de raquetas update con uuid: $uuid raqueta: $raqueta" }

            val raquetaDB = raquetasRepository.findByUuid(uuid).firstOrNull()

            raquetaDB?.let {
                val updated = it.copy(
                    uuid = it.uuid,
                    marca = raqueta.marca,
                    precio = raqueta.precio,
                    representanteId = raqueta.representanteId,
                    createdAt = it.createdAt,
                    updatedAt = LocalDateTime.now()
                )
                return@withContext raquetasRepository.save(updated)
            }
            return@withContext null
        }

    @CacheEvict("raquetas")
    override suspend fun deleteById(id: Long) = withContext(Dispatchers.IO) {
        logger.info { "Repositorio de raquetas deleteById con id: $id" }

        try {
            raquetasRepository.deleteById(id)
        } catch (e: Exception) {
            throw RepresentanteConflictIntegrityException("No se puede borrar la raqueta con id: $id porque tiene tenistas asociados")
        }
    }

    override suspend fun findAllPage(pageRequest: PageRequest): Flow<Page<Raqueta>> {
        logger.info { "Repositorio de raquetas findAllPage" }

        return raquetasRepository.findAllBy(pageRequest)
            .toList()
            .windowed(pageRequest.pageSize, pageRequest.pageSize, true)
            .map { PageImpl(it, pageRequest, raquetasRepository.count()) }
            .asFlow()
    }

    override suspend fun countAll(): Long {
        logger.info { "Repositorio de raquetas countAll" }

        return raquetasRepository.count()
    }

    @CacheEvict("raquetas")
    override suspend fun deleteByUuid(uuid: UUID): Raqueta? = withContext(Dispatchers.IO) {
        logger.info { "Repositorio de raquetas deleteByUuid con uuid: $uuid" }

        val raquetaDB = raquetasRepository.findByUuid(uuid).firstOrNull()
        try {
            raquetaDB?.let {
                raquetasRepository.deleteById(it.id!!)
                return@withContext it
            }
        } catch (e: Exception) {
            throw RaquetaConflictIntegrityException("No se puede borrar la raqueta: ${raquetaDB?.marca} ya que tiene tenistas asociados")
        }
        return@withContext null
    }

    @CacheEvict("raquetas")
    override suspend fun delete(raqueta: Raqueta): Raqueta? = withContext(Dispatchers.IO) {
        logger.info { "Repositorio de raquetas delete raqueta: $raqueta" }

        val raquetaBD = raquetasRepository.findByUuid(raqueta.uuid).firstOrNull()
        try {
            raquetaBD?.let {
                raquetasRepository.deleteById(it.id!!)
                return@withContext it
            }
        } catch (e: Exception) {
            throw RaquetaConflictIntegrityException("No se puede borrar la raqueta: ${raqueta.marca} ya que tiene tenistas asociados")
        }
        return@withContext null
    }

    @CacheEvict("raquetas", allEntries = true)
    override suspend fun deleteAll() = withContext(Dispatchers.IO) {
        logger.info { "Repositorio de raquetas deleteAll" }

        raquetasRepository.deleteAll()
    }
}