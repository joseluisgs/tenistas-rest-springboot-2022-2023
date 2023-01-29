package es.joseluisgs.tenistasrestspringboot.services.representantes

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import es.joseluisgs.tenistasrestspringboot.config.websocket.ServerWebSocketConfig
import es.joseluisgs.tenistasrestspringboot.config.websocket.WebSocketHandler
import es.joseluisgs.tenistasrestspringboot.exceptions.RepresentanteNotFoundException
import es.joseluisgs.tenistasrestspringboot.mappers.toDto
import es.joseluisgs.tenistasrestspringboot.models.Notificacion
import es.joseluisgs.tenistasrestspringboot.models.Representante
import es.joseluisgs.tenistasrestspringboot.models.RepresentanteNotification
import es.joseluisgs.tenistasrestspringboot.repositories.representantes.RepresentantesCachedRepository
import kotlinx.coroutines.flow.Flow
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.util.*

private val logger = KotlinLogging.logger {}

@Service
class RepresentanteServiceImpl
@Autowired constructor(
    private val representantesRepository: RepresentantesCachedRepository, // Repositorio de datos
    private val webSocketConfig: ServerWebSocketConfig // Para enviar mensajes a los clientes ws normales
    // private val simpMessagingTemplate: SimpMessagingTemplate // Para enviar mensajes a los clientes ws STOP
) : RepresentantesService {

    private val webSocketService = webSocketConfig.webSocketHandler() as WebSocketHandler

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
            .also { onChange(Notificacion.Tipo.CREATE, it.uuid, it) }
    }

    override suspend fun update(uuid: UUID, representante: Representante): Representante {
        logger.debug { "Servicio de representantes update representante con id: $uuid " }

        val existe = representantesRepository.findByUuid(uuid)

        existe?.let {
            return representantesRepository.update(uuid, representante)
                ?.also { onChange(Notificacion.Tipo.UPDATE, it.uuid, it) }!!
        } ?: throw RepresentanteNotFoundException("No se ha encontrado el representante con uuid: $uuid")
    }

    override suspend fun delete(representante: Representante): Representante {
        logger.debug { "Servicio de representantes delete representante: $representante" }

        val existe = representantesRepository.findByUuid(representante.uuid)

        existe?.let {
            return representantesRepository.delete(existe)
                ?.also { onChange(Notificacion.Tipo.DELETE, it.uuid, it) }!!
        }
            ?: throw RepresentanteNotFoundException("No se ha encontrado el representante con uuid: ${representante.uuid}")

    }

    override suspend fun deleteByUuid(uuid: UUID): Representante {
        logger.debug { "Servicio de representantes deleteByUuid con uuid: $uuid" }

        val existe = representantesRepository.findByUuid(uuid)

        existe?.let {
            return representantesRepository.deleteByUuid(uuid)
                ?.also { onChange(Notificacion.Tipo.DELETE, it.uuid, it) }!!
        } ?: throw RepresentanteNotFoundException("No se ha encontrado el representante con uuid: $uuid")
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

    // Enviamos la notificación a los clientes ws
    suspend fun onChange(tipo: Notificacion.Tipo, id: UUID, data: Representante? = null) {
        logger.debug { "Servicio de representantes onChange con tipo: $tipo, id: $id, data: $data" }

        // data to json
        val mapper = jacksonObjectMapper()
        val json = mapper.writeValueAsString(RepresentanteNotification("REPRESENTANTE", tipo, id, data?.toDto()))
        // Enviamos la notificación a los clientes ws

        // Siguiendo el modelo STOMP
        //simpMessagingTemplate.convertAndSend("/updates/representantes", json)
        //simpMessagingTemplate.convertAndSend("/updates/representantes2", json)

        // Siguiendo el modelo WebSockets, compatible con Postman
        logger.info { "Enviando mensaje a los clientes ws" }

        webSocketService.sendMessage(json)
    }

}