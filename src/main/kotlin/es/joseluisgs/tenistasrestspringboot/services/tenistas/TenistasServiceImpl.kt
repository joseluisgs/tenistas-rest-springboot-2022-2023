package es.joseluisgs.tenistasrestspringboot.services.tenistas

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import es.joseluisgs.tenistasrestspringboot.config.websocket.ServerWebSocketConfig
import es.joseluisgs.tenistasrestspringboot.config.websocket.WebSocketHandler
import es.joseluisgs.tenistasrestspringboot.exceptions.RaquetaNotFoundException
import es.joseluisgs.tenistasrestspringboot.exceptions.TenistaNotFoundException
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

    private val webSocketService = webSocketConfig.webSocketHandler() as WebSocketHandler

    init {
        logger.info { "Iniciando Servicio de Tenistas" }
    }

    override suspend fun findAll(): Flow<Tenista> {
        logger.info { "Servicio de tenistas findAll" }

        return tenistasRepository.findAll()
    }

    override suspend fun findById(id: Long): Tenista {
        logger.debug { "Servicio de tenista findById con id: $id" }

        return tenistasRepository.findById(id)
            ?: throw TenistaNotFoundException("No se ha encontrado tenista con id: $id")
    }

    override suspend fun findByUuid(uuid: UUID): Tenista {
        logger.debug { "Servicio de tenistas findByUuid con uuid: $uuid" }

        return tenistasRepository.findByUuid(uuid)
            ?: throw TenistaNotFoundException("No se ha encontrado tenista con uuid: $uuid")
    }

    override suspend fun findByNombre(nombre: String): Flow<Tenista> {
        logger.debug { "Servicio de tenistas findByNombre con nombre: $nombre" }

        return tenistasRepository.findByNombre(nombre)
    }

    override suspend fun findByRanking(ranking: Int): Tenista {
        logger.debug { "Servicio de tenistas findByRanking con ranking: $ranking" }

        return tenistasRepository.findByRanking(ranking).firstOrNull()
            ?: throw TenistaNotFoundException("No se ha encontrado tenista con ranking: $ranking")
    }

    override suspend fun save(tenista: Tenista): Tenista {
        logger.debug { "Servicio de tenistas save tenista: $tenista" }

        // Existe la raqueta
        val existe = findRaqueta(tenista.raquetaId)

        return tenistasRepository.save(tenista)
            .also { onChange(Notificacion.Tipo.CREATE, it.uuid, it) }
    }

    override suspend fun update(uuid: UUID, tenista: Tenista): Tenista {
        logger.debug { "Servicio de tenistas update tenista con id: $uuid " }

        val existe = tenistasRepository.findByUuid(uuid)

        existe?.let {
            // Existe la raqueta
            val representante = findRaqueta(tenista.raquetaId)

            return tenistasRepository.update(uuid, tenista)
                ?.also { onChange(Notificacion.Tipo.UPDATE, it.uuid, it) }!!
        } ?: throw TenistaNotFoundException("No se ha encontrado tenista con uuid: $uuid")
    }

    override suspend fun delete(tenista: Tenista): Tenista {
        logger.debug { "Servicio de tenistas delete raqueta: $tenista" }

        val existe = tenistasRepository.findByUuid(tenista.uuid)

        existe?.let {
            return tenistasRepository.delete(existe)
                ?.also { onChange(Notificacion.Tipo.DELETE, it.uuid, it) }!!
        }
            ?: throw TenistaNotFoundException("No se ha encontrado tenista con uuid: ${tenista.uuid}")

    }

    override suspend fun deleteByUuid(uuid: UUID): Tenista {
        logger.debug { "Servicio de tenistas deleteByUuid con uuid: $uuid" }

        val existe = tenistasRepository.findByUuid(uuid)

        existe?.let {
            return tenistasRepository.deleteByUuid(uuid)
                ?.also { onChange(Notificacion.Tipo.DELETE, it.uuid, it) }!!
        } ?: throw TenistaNotFoundException("No se ha encontrado tenista con uuid: $uuid")
    }

    override suspend fun deleteById(id: Long) {
        logger.debug { "Servicio de tenistas deleteById con id: $id" }

        tenistasRepository.deleteById(id)
    }

    override suspend fun findAllPage(pageRequest: PageRequest): Flow<Page<Tenista>> {
        logger.debug { "Servicio de raquetas findAllPage con pageRequest: $pageRequest" }

        return tenistasRepository.findAllPage(pageRequest)
    }

    override suspend fun countAll(): Long {
        logger.debug { "Servicio de raquetas countAll" }

        return tenistasRepository.countAll()
    }

    override suspend fun findRaqueta(id: UUID?): Raqueta? {
        logger.debug { "findRepresentante: Buscando raqueta en servicio" }

        id?.let {
            return raquetasRepository.findByUuid(id)
                ?: throw RaquetaNotFoundException("No se ha encontrado la raqueta con id: $id")
        }
        return null
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
                data?.toDto(findRaqueta(data.raquetaId))
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