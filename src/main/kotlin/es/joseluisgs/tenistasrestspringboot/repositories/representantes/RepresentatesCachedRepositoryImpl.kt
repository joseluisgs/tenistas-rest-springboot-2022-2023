package es.joseluisgs.tenistasrestspringboot.repositories.representantes

import es.joseluisgs.tenistasrestspringboot.exceptions.RepresentanteConflictIntegrityException
import es.joseluisgs.tenistasrestspringboot.models.Representante
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
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

/**
 * Repositorio de Representantes con Cache
 * @param representantesRepository: RepresentantesRepository
 * @see RepresentantesRepository
 * @see Representante
 */
@Repository
class RepresentatesCachedRepositoryImpl
@Autowired constructor(
    private val representantesRepository: RepresentantesRepository
) : RepresentantesCachedRepository {

    init {
        logger.info { "Iniciando Repositorio Cache de Representantes" }
    }

    /**
     * Función que nos devuelve todos los representantes
     * @return Flow<Representante>
     */
    override suspend fun findAll(): Flow<Representante> = withContext(Dispatchers.IO) {
        logger.info { "Repositorio de representantes findAll" }

        return@withContext representantesRepository.findAll()
    }

    /**
     * Función que nos devuelve un representante por su id
     * @param id: Long Identificador del representante
     * @return Representante? Representante o null si no lo encuentra
     */
    @Cacheable("representantes")
    override suspend fun findById(id: Long): Representante? = withContext(Dispatchers.IO) {
        logger.info { "Repositorio de representantes findById con id: $id" }

        return@withContext representantesRepository.findById(id)
    }

    /**
     * Función que nos devuelve un representante por su uuid
     * @param uuid: UUID UUID del representante
     * @return Representante? Representante o null si no lo encuentra
     * @see Representante
     */
    @Cacheable("representantes")
    override suspend fun findByUuid(uuid: UUID): Representante? = withContext(Dispatchers.IO) {
        logger.info { "Repositorio de representantes findByUuid con uuid: $uuid" }

        return@withContext representantesRepository.findByUuid(uuid).firstOrNull()
    }

    /**
     * Función que nos devuelve un representante por su nombre
     * @param nombre: String Nombre del representante
     * @return Flow<Representante>
     * @see Representante
     */
    override suspend fun findByNombre(nombre: String): Flow<Representante> = withContext(Dispatchers.IO) {
        logger.info { "Repositorio de representantes findByName con nombre: $nombre" }

        return@withContext representantesRepository.findByNombreContainsIgnoreCase(nombre)
    }

    /**
     * Creamos un nuevo representante
     * @param representante: Representante Representante a crear
     * @return Representante Representante creado
     * @see Representante
     */
    @CachePut("representantes")
    override suspend fun save(representante: Representante): Representante = withContext(Dispatchers.IO) {
        logger.info { "Repositorio de representantes save representante: $representante" }

        val saved =
            representante.copy(
                uuid = UUID.randomUUID(),
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )

        return@withContext representantesRepository.save(representante)
    }

    /**
     * Actualizamos un representante
     * @param uuid: UUID UUID del representante
     * @param representante: Representante Representante a actualizar
     */
    @CachePut("representantes")
    override suspend fun update(uuid: UUID, representante: Representante): Representante? =
        withContext(Dispatchers.IO) {
            logger.info { "Repositorio de representantes update con uuid: $uuid representante: $representante" }

            val representanteDB = representantesRepository.findByUuid(uuid).firstOrNull()

            representanteDB?.let {
                val updated = it.copy(
                    uuid = it.uuid,
                    nombre = representante.nombre,
                    email = representante.email,
                    createdAt = it.createdAt,
                    updatedAt = LocalDateTime.now()
                )
                return@withContext representantesRepository.save(updated)
            }
            return@withContext null
        }

    /**
     * Borramos un representante por su id
     * @param id: Long Identificador del representante
     * @throws RepresentanteConflictIntegrityException Si el representante tiene raquetas asociadas
     */
    @CacheEvict("representantes")
    override suspend fun deleteById(id: Long) = withContext(Dispatchers.IO) {
        logger.info { "Repositorio de representantes deleteById con id: $id" }

        try {
            representantesRepository.deleteById(id)
        } catch (e: Exception) {
            throw RepresentanteConflictIntegrityException("No se puede borrar el representante con id: $id")
        }
    }

    /**
     * Obtiene los representantes paginados
     * @param pageRequest: PageRequest Petición de paginación
     * @return Flow<Page<Representante>>
     */
    override suspend fun findAllPage(pageRequest: PageRequest): Flow<Page<Representante>> {
        logger.info { "Repositorio de representantes findAllPage" }

        return representantesRepository.findAllBy(pageRequest)
            .toList()
            .windowed(pageRequest.pageSize, pageRequest.pageSize, true)
            .map { PageImpl(it, pageRequest, representantesRepository.count()) }
            .asFlow()
    }

    /**
     * Obtiene el número de representantes
     * @return Long Número de representantes
     */
    override suspend fun countAll(): Long {
        logger.info { "Repositorio de representantes countAll" }

        return representantesRepository.count()
    }

    /**
     * Borramos un representante por su uuid
     * @param uuid: UUID UUID del representante
     * @throws RepresentanteConflictIntegrityException Si el representante tiene raquetas asociadas
     */
    @CacheEvict("representantes")
    override suspend fun deleteByUuid(uuid: UUID): Representante? = withContext(Dispatchers.IO) {
        logger.info { "Repositorio de representantes deleteByUuid con uuid: $uuid" }

        val representanteDB = representantesRepository.findByUuid(uuid).firstOrNull()
        try {
            representanteDB?.let {
                representantesRepository.deleteById(it.id!!)
                return@withContext it
            }
        } catch (e: Exception) {
            throw RepresentanteConflictIntegrityException("No se puede borrar el representante: ${representanteDB?.nombre} ya que tiene raquetas asociadas")
        }
        return@withContext null
    }

    /**
     * Borramos un representante
     * @param nombre: String Nombre del representante
     * @throws RepresentanteConflictIntegrityException Si el representante tiene raquetas asociadas
     */
    @CacheEvict("representantes")
    override suspend fun delete(representante: Representante): Representante? = withContext(Dispatchers.IO) {
        logger.info { "Repositorio de representantes delete con representante: $representante" }

        val representanteDB = representantesRepository.findByUuid(representante.uuid).firstOrNull()
        try {
            representanteDB?.let {
                representantesRepository.deleteById(it.id!!)
                return@withContext it
            }
        } catch (e: Exception) {
            throw RepresentanteConflictIntegrityException("No se puede borrar el representante: ${representante.nombre} ya que tiene raquetas asociadas")
        }
        return@withContext null
    }

    /**
     * Borramos todos los representantes
     */
    @CacheEvict("representantes", allEntries = true)
    override suspend fun deleteAll() = withContext(Dispatchers.IO) {
        logger.info { "Repositorio de representantes deleteAll" }

        representantesRepository.deleteAll()
    }
}