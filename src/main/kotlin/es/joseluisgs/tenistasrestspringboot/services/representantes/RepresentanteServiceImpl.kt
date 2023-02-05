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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.util.*

private val logger = KotlinLogging.logger {}

/**
 * Servicio de Representantes que implementa la interfaz de RepresentantesService
 * @see RepresentantesService para más información
 * @property representantesRepository Repositorio de datos
 * @property webSocketConfig Para enviar mensajes a los clientes por web socket de los cambios
 */
@Service
class RepresentanteServiceImpl
@Autowired constructor(
    private val representantesRepository: RepresentantesCachedRepository, // Repositorio de datos
    private val webSocketConfig: ServerWebSocketConfig // Para enviar mensajes a los clientes ws normales
    // private val simpMessagingTemplate: SimpMessagingTemplate // Para enviar mensajes a los clientes ws STOP
) : RepresentantesService {

    // Para enviar mensajes a los clientes ws normales, pero le hago un cast para que sea de tipo WebSocketHandler
    private val webSocketService = webSocketConfig.webSocketRepresentantesHandler() as WebSocketHandler

    init {
        logger.info { "Iniciando Servicio de Representantes" }
    }

    /**
     * Obtener todos los representantes
     * @return Flow<Representante> con todos los representantes
     */
    override suspend fun findAll(): Flow<Representante> {
        logger.info { "Servicio de representantes findAll" }

        return representantesRepository.findAll()
    }

    /**
     * Obtiene un representante por su id
     * @param id Long con el id del representante
     * @return Representante con el representante
     * @throws RepresentanteNotFoundException si no se encuentra el representante
     */
    override suspend fun findById(id: Long): Representante {
        logger.debug { "Servicio de representantes findById con id: $id" }

        return representantesRepository.findById(id)
            ?: throw RepresentanteNotFoundException("No se ha encontrado el representante con id: $id")
    }

    /**
     * Obtiene un representante por su uuid
     * @param uuid UUID con el uuid del representante
     * @return Representante con el representante
     * @throws RepresentanteNotFoundException si no se encuentra el representante
     */
    override suspend fun findByUuid(uuid: UUID): Representante {
        logger.debug { "Servicio de representantes findByUuid con uuid: $uuid" }

        // return repository.findByUuid(uuid) ?: throw NoSuchElementException("No se ha encontrado el representante con uuid: $uuid")
        return representantesRepository.findByUuid(uuid)
            ?: throw RepresentanteNotFoundException("No se ha encontrado el representante con uuid: $uuid")
    }

    /**
     * Obtiene un representante por su nombre
     * @param nombre String con el nombre del representante
     * @return Flow<Representante> con el representante
     */
    override suspend fun findByNombre(nombre: String): Flow<Representante> {
        logger.debug { "Servicio de representantes findByNombre con nombre: $nombre" }

        return representantesRepository.findByNombre(nombre)
    }

    /** Salva un representante
     * @param representante Representante a salvar
     * @return Representante con el representante salvado
     */
    override suspend fun save(representante: Representante): Representante {
        logger.debug { "Servicio de representantes save representante: $representante" }

        return representantesRepository.save(representante)
            .also { onChange(Notificacion.Tipo.CREATE, it.uuid, it) }
    }

    /**
     * Actualiza un representante
     * @param uuid UUID con el uuid del representante
     * @param representante Representante a actualizar
     * @return Representante con el representante actualizado
     * @throws RepresentanteNotFoundException si no se encuentra el representante
     */
    override suspend fun update(uuid: UUID, representante: Representante): Representante {
        logger.debug { "Servicio de representantes update representante con id: $uuid " }

        val existe = representantesRepository.findByUuid(uuid)

        existe?.let {
            return representantesRepository.update(uuid, representante)
                ?.also { onChange(Notificacion.Tipo.UPDATE, it.uuid, it) }!!
        } ?: throw RepresentanteNotFoundException("No se ha encontrado el representante con uuid: $uuid")
    }

    /**
     * Borra un representante
     * @param representante Representante a borrar
     * @return Representante con el representante borrado
     * @throws RepresentanteNotFoundException si no se encuentra el representante
     */
    override suspend fun delete(representante: Representante): Representante {
        logger.debug { "Servicio de representantes delete representante: $representante" }

        val existe = representantesRepository.findByUuid(representante.uuid)

        existe?.let {
            return representantesRepository.delete(existe)
                ?.also { onChange(Notificacion.Tipo.DELETE, it.uuid, it) }!!
        }
            ?: throw RepresentanteNotFoundException("No se ha encontrado el representante con uuid: ${representante.uuid}")

    }

    /**
     * Borra un representante por su uuid
     * @param uuid Long con el id del representante
     * @return Representante con el representante borrado
     * @throws RepresentanteNotFoundException si no se encuentra el representante
     */
    override suspend fun deleteByUuid(uuid: UUID): Representante {
        logger.debug { "Servicio de representantes deleteByUuid con uuid: $uuid" }

        val existe = representantesRepository.findByUuid(uuid)

        existe?.let {
            return representantesRepository.deleteByUuid(uuid)
                ?.also { onChange(Notificacion.Tipo.DELETE, it.uuid, it) }!!
        } ?: throw RepresentanteNotFoundException("No se ha encontrado el representante con uuid: $uuid")
    }

    /**
     * Borra un representante por su id
     * @param id Long con el id del representante
     * @throws RepresentanteNotFoundException si no se encuentra el representante
     */
    override suspend fun deleteById(id: Long) {
        logger.debug { "Servicio de representantes deleteById con id: $id" }

        representantesRepository.deleteById(id)
    }

    /**
     * Obtiene todos los representantes paginados
     * @param pageRequest PageRequest con la información de la paginación
     * @return Flow<Representante> con los representantes
     */
    override suspend fun findAllPage(pageRequest: PageRequest): Flow<Page<Representante>> {
        logger.debug { "Servicio de representantes findAllPage con pageRequest: $pageRequest" }

        return representantesRepository.findAllPage(pageRequest)
    }

    /**
     * Cuenta el número de representantes
     * @return Long con el número de representantes
     */
    override suspend fun countAll(): Long {
        logger.debug { "Servicio de representantes countAll" }

        return representantesRepository.countAll()
    }

    // Enviamos la notificación a los clientes ws
    /**
     * Envía una notificación a los clientes ws
     * @param tipo Tipo de notificación
     * @param id Long con el id del representante
     * @param data Representante con los datos del representante
     */
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

        val myScope = CoroutineScope(Dispatchers.IO)
        myScope.launch {
            webSocketService.sendMessage(json)
        }
    }

}