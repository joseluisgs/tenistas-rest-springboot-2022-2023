package es.joseluisgs.tenistasrestspringboot.repositories.tenistas

import es.joseluisgs.tenistasrestspringboot.models.Tenista
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
class TenistasCachedRepositoryImpl constructor(
    @Autowired
    private val tenistasRepository: TenistasRepository
) : TenistasCachedRepository {

    init {
        logger.info { "Iniciando Repositorio Cache de Tenistas" }
    }

    override suspend fun findAll(): Flow<Tenista> = withContext(Dispatchers.IO) {
        logger.info { "Repositorio de tenistas findAll" }

        return@withContext tenistasRepository.findAll()
    }

    @Cacheable("tenistas")
    override suspend fun findById(id: Long): Tenista? = withContext(Dispatchers.IO) {
        logger.info { "Repositorio de tenistas findById con id: $id" }

        return@withContext tenistasRepository.findById(id)
    }

    @Cacheable("tenistas")
    override suspend fun findByUuid(uuid: UUID): Tenista? = withContext(Dispatchers.IO) {
        logger.info { "Repositorio de tenista findByUuid con uuid: $uuid" }

        return@withContext tenistasRepository.findByUuid(uuid).firstOrNull()
    }

    override suspend fun findByNombre(nombre: String): Flow<Tenista> = withContext(Dispatchers.IO) {
        logger.info { "Repositorio de tenistas findByMarca con nombre: $nombre" }

        return@withContext tenistasRepository.findByNombreContainsIgnoreCase(nombre)
    }

    override suspend fun findByRanking(ranking: Int): Flow<Tenista> {
        logger.info { "Repositorio de tenistas findByRanking con ranking: $ranking" }

        return tenistasRepository.findByRanking(ranking)
    }

    @CachePut("tenistas")
    override suspend fun save(tenista: Tenista): Tenista = withContext(Dispatchers.IO) {
        logger.info { "Repositorio de tenistas save tenista: $tenista" }

        val saved =
            tenista.copy(
                uuid = UUID.randomUUID(),
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )

        return@withContext tenistasRepository.save(tenista)
    }

    @CachePut("tenistas")
    override suspend fun update(uuid: UUID, tenista: Tenista): Tenista? =
        withContext(Dispatchers.IO) {
            logger.info { "Repositorio de tenista update con uuid: $uuid tenista: $tenista" }

            val tenistaDB = tenistasRepository.findByUuid(uuid).firstOrNull()

            tenistaDB?.let {
                val updated = it.copy(
                    uuid = it.uuid,
                    nombre = it.nombre,
                    ranking = it.ranking,
                    fechaNacimiento = it.fechaNacimiento,
                    añoProfesional = it.añoProfesional,
                    altura = it.altura,
                    peso = it.peso,
                    manoDominante = it.manoDominante,
                    tipoReves = it.tipoReves,
                    puntos = it.puntos,
                    pais = it.pais,
                    raquetaId = it.raquetaId,
                    createdAt = it.createdAt,
                    updatedAt = LocalDateTime.now()
                )
                return@withContext tenistasRepository.save(updated)
            }
            return@withContext null
        }

    @CacheEvict("tenistas")
    override suspend fun deleteById(id: Long) = withContext(Dispatchers.IO) {
        logger.info { "Repositorio de tenistas deleteById con id: $id" }

        tenistasRepository.deleteById(id)
    }

    override suspend fun findAllPage(pageRequest: PageRequest): Flow<Page<Tenista>> {
        logger.info { "Repositorio de tenistas findAllPage" }

        return tenistasRepository.findAllBy(pageRequest)
            .toList()
            .windowed(pageRequest.pageSize, pageRequest.pageSize, true)
            .map { PageImpl(it, pageRequest, tenistasRepository.count()) }
            .asFlow()
    }

    override suspend fun countAll(): Long {
        logger.info { "Repositorio de raquetas countAll" }

        return tenistasRepository.count()
    }

    @CacheEvict("tenistas")
    override suspend fun deleteByUuid(uuid: UUID): Tenista? = withContext(Dispatchers.IO) {
        logger.info { "Repositorio de tenistas deleteByUuid con uuid: $uuid" }

        val tenistaDB = tenistasRepository.findByUuid(uuid).firstOrNull()
        tenistaDB?.let {
            tenistasRepository.deleteById(it.id!!)
            return@withContext it
        }
        return@withContext null
    }

    @CacheEvict("tenistas")
    override suspend fun delete(tenista: Tenista): Tenista? = withContext(Dispatchers.IO) {
        logger.info { "Repositorio de tenistas delete tenista: $tenista" }

        val tenistaBD = tenistasRepository.findByUuid(tenista.uuid).firstOrNull()
        tenistaBD?.let {
            tenistasRepository.deleteById(it.id!!)
            return@withContext it
        }
        return@withContext null
    }

    @CacheEvict("tenistas", allEntries = true)
    suspend fun deleteAll() = withContext(Dispatchers.IO) {
        logger.info { "Repositorio de tenistas deleteAll" }

        tenistasRepository.deleteAll()
    }
}