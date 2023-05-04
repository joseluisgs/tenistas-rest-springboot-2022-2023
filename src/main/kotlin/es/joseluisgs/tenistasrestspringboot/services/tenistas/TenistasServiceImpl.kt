package es.joseluisgs.tenistasrestspringboot.services.tenistas

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.michaelbull.result.*
import es.joseluisgs.tenistasrestspringboot.config.websocket.ServerWebSocketConfig
import es.joseluisgs.tenistasrestspringboot.config.websocket.WebSocketHandler
import es.joseluisgs.tenistasrestspringboot.errors.TenistaError
import es.joseluisgs.tenistasrestspringboot.mappers.toDto
import es.joseluisgs.tenistasrestspringboot.models.Notificacion
import es.joseluisgs.tenistasrestspringboot.models.Raqueta
import es.joseluisgs.tenistasrestspringboot.models.Tenista
import es.joseluisgs.tenistasrestspringboot.models.TenistaNotification
import es.joseluisgs.tenistasrestspringboot.repositories.raquetas.RaquetasCachedRepository
import es.joseluisgs.tenistasrestspringboot.repositories.tenistas.TenistasCachedRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.util.*

private val logger = KotlinLogging.logger {}

@Service
class TenistasServiceImpl
@Autowired constructor(
    private val tenistasRepository: TenistasCachedRepository, // Repositorio de datos
    private val raquetasRepository: RaquetasCachedRepository, // Repositorio de datos
    private val webSocketConfig: ServerWebSocketConfig // Configuración del WebSocket
) : TenistasService {

    private val webSocketService = webSocketConfig.webSocketTenistasHandler() as WebSocketHandler

    init {
        logger.info { "Iniciando Servicio de Tenistas" }
    }

    override suspend fun findAll(): Flow<Tenista> {
        logger.info { "Servicio de tenistas findAll" }

        return tenistasRepository.findAll()
    }

    override suspend fun findById(id: Long): Result<Tenista, TenistaError> {
        logger.debug { "Servicio de tenista findById con id: $id" }

        return tenistasRepository.findById(id)
            ?.let { Ok(it) }
            ?: Err(TenistaError.NotFound("No se ha encontrado el tenista con id: $id"))
    }

    override suspend fun findByUuid(uuid: UUID): Result<Tenista, TenistaError> {
        logger.debug { "Servicio de tenistas findByUuid con uuid: $uuid" }

        return tenistasRepository.findByUuid(uuid)
            ?.let { Ok(it) }
            ?: Err(TenistaError.NotFound("No se ha encontrado el tenista con uuid: $uuid"))
    }

    override suspend fun findByNombre(nombre: String): Flow<Tenista> {
        logger.debug { "Servicio de tenistas findByNombre con nombre: $nombre" }

        return tenistasRepository.findByNombre(nombre)
    }

    override suspend fun findByRanking(ranking: Int): Result<Tenista, TenistaError> {
        logger.debug { "Servicio de tenistas findByRanking con ranking: $ranking" }

        return tenistasRepository.findByRanking(ranking).firstOrNull()
            ?.let { Ok(it) }
            ?: Err(TenistaError.NotFound("No se ha encontrado el tenista con ranking: $ranking"))
    }

    override suspend fun save(tenista: Tenista): Result<Tenista, TenistaError> {
        logger.debug { "Servicio de tenistas save tenista: $tenista" }

        return findRaqueta(tenista.raquetaId).andThen {
            tenistasRepository.save(tenista)
                .also { onChange(Notificacion.Tipo.CREATE, it.uuid, it) }
                .let { Ok(it) }
        }
    }

    override suspend fun update(uuid: UUID, tenista: Tenista): Result<Tenista, TenistaError> {
        logger.debug { "Servicio de tenistas update tenista con id: $uuid " }

        return findRaqueta(tenista.raquetaId).andThen {
            findByUuid(uuid).onSuccess {
                tenistasRepository.update(uuid, tenista)
                    .also { onChange(Notificacion.Tipo.UPDATE, it!!.uuid, it) }
                    .let { Ok(it) }
            }
        }
    }

    override suspend fun delete(tenista: Tenista): Result<Tenista, TenistaError> {
        logger.debug { "Servicio de tenistas delete raqueta: $tenista" }

        return findByUuid(tenista.uuid).onSuccess {
            tenistasRepository.delete(tenista)
                ?.also { onChange(Notificacion.Tipo.DELETE, it.uuid, it) }
                ?.let { Ok(it) }
        }
    }

    override suspend fun deleteByUuid(uuid: UUID): Result<Tenista, TenistaError> {
        logger.debug { "Servicio de tenistas deleteByUuid con uuid: $uuid" }

        return findByUuid(uuid).onSuccess {
            tenistasRepository.deleteByUuid(uuid)
                ?.also { onChange(Notificacion.Tipo.DELETE, it.uuid, it) }
                ?.let { Ok(it) }
        }

    }

    override suspend fun deleteById(id: Long): Result<Tenista, TenistaError> {
        logger.debug { "Servicio de tenistas deleteById con id: $id" }

        return findById(id).onSuccess { r ->
            tenistasRepository.deleteById(id)
                .also { onChange(Notificacion.Tipo.DELETE, r.uuid, r) }
                .let { Ok(it) }
        }
    }

    override suspend fun findAllPage(pageRequest: PageRequest): Flow<Page<Tenista>> {
        logger.debug { "Servicio de raquetas findAllPage con pageRequest: $pageRequest" }

        return tenistasRepository.findAllPage(pageRequest)
    }

    override suspend fun countAll(): Long {
        logger.debug { "Servicio de raquetas countAll" }

        return tenistasRepository.countAll()
    }

    override suspend fun findRaqueta(id: UUID?): Result<Raqueta?, TenistaError> {
        logger.debug { "findRepresentante: Buscando raqueta en servicio" }

        return id?.let {
            raquetasRepository.findByUuid(id)
                ?.let { Ok(it) }
                ?: Err(TenistaError.RaquetaNotFound("No se ha encontrado la raqueta con id: $id"))
        } ?: Ok(null)
    }

    // Enviamos la notificación a los clientes ws
    suspend fun onChange(tipo: Notificacion.Tipo, id: UUID, data: Tenista? = null) {
        logger.debug { "Servicio de raquetas onChange con tipo: $tipo, id: $id, data: $data" }

        // data to json
        val mapper = jacksonObjectMapper()
        val json = mapper.writeValueAsString(
            TenistaNotification(
                "TENISTA",
                tipo,
                id,
                data?.toDto(findRaqueta(data.raquetaId).get())
            )
        )
        // Enviamos la notificación a los clientes ws

        logger.info { "Enviando mensaje a los clientes ws" }

        val myScope = CoroutineScope(Dispatchers.IO)
        myScope.launch {
            webSocketService.sendMessage(json)
        }
    }

}