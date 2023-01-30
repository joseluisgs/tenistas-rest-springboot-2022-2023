package es.joseluisgs.tenistasrestspringboot.services.raquetas

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import es.joseluisgs.tenistasrestspringboot.config.websocket.ServerWebSocketConfig
import es.joseluisgs.tenistasrestspringboot.config.websocket.WebSocketHandler
import es.joseluisgs.tenistasrestspringboot.exceptions.RaquetaNotFoundException
import es.joseluisgs.tenistasrestspringboot.exceptions.RepresentanteNotFoundException
import es.joseluisgs.tenistasrestspringboot.mappers.toDto
import es.joseluisgs.tenistasrestspringboot.models.Notificacion
import es.joseluisgs.tenistasrestspringboot.models.Raqueta
import es.joseluisgs.tenistasrestspringboot.models.RaquetaNotification
import es.joseluisgs.tenistasrestspringboot.models.Representante
import es.joseluisgs.tenistasrestspringboot.repositories.raquetas.RaquetasCachedRepository
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

@Service
class RaquetasServiceImpl
@Autowired constructor(
    private val raquetasRepository: RaquetasCachedRepository, // Repositorio de datos
    private val representesRepository: RepresentantesCachedRepository, // Repositorio de datos
    private val webSocketConfig: ServerWebSocketConfig // Para enviar mensajes a los clientes ws normales
) : RaquetasService {
    // Inyectamos el servicio de websockets, pero lo hacemos de esta forma para que no se inyecte en el constructor
    //y casteamos a nuestro handler para poder usarlo (que tiene el método send)
    private val webSocketService = webSocketConfig.webSocketHandler() as WebSocketHandler

    init {
        logger.info { "Iniciando Servicio de Raquetas" }
    }

    override suspend fun findAll(): Flow<Raqueta> {
        logger.info { "Servicio de raquetas findAll" }

        return raquetasRepository.findAll()
    }

    override suspend fun findById(id: Long): Raqueta {
        logger.debug { "Servicio de raquetas findById con id: $id" }

        return raquetasRepository.findById(id)
            ?: throw RaquetaNotFoundException("No se ha encontrado la raqueta con id: $id")
    }

    override suspend fun findByUuid(uuid: UUID): Raqueta {
        logger.debug { "Servicio de raquetas findByUuid con uuid: $uuid" }

        return raquetasRepository.findByUuid(uuid)
            ?: throw RaquetaNotFoundException("No se ha encontrado la raqueta con uuid: $uuid")
    }

    override suspend fun findByMarca(marca: String): Flow<Raqueta> {
        logger.debug { "Servicio de raquetas findByMarca con marca: $marca" }

        return raquetasRepository.findByMarca(marca)
    }

    override suspend fun save(raqueta: Raqueta): Raqueta {
        logger.debug { "Servicio de raquetas save raqueta: $raqueta" }

        // Existe el representante!
        val representante = findRepresentante(raqueta.represetanteId)

        return raquetasRepository.save(raqueta)
            .also { onChange(Notificacion.Tipo.CREATE, it.uuid, it) }
    }

    override suspend fun update(uuid: UUID, raqueta: Raqueta): Raqueta {
        logger.debug { "Servicio de raquetas update raqueta con id: $uuid " }

        val existe = raquetasRepository.findByUuid(uuid)

        existe?.let {
            // Existe el representante!
            val representante = findRepresentante(raqueta.represetanteId)

            return raquetasRepository.update(uuid, raqueta)
                ?.also { onChange(Notificacion.Tipo.UPDATE, it.uuid, it) }!!
        } ?: throw RaquetaNotFoundException("No se ha encontrado la raqueta con uuid: $uuid")
    }

    override suspend fun delete(raqueta: Raqueta): Raqueta {
        logger.debug { "Servicio de raquetas delete raqueta: $raqueta" }

        val existe = raquetasRepository.findByUuid(raqueta.uuid)

        existe?.let {
            return raquetasRepository.delete(existe)
                ?.also { onChange(Notificacion.Tipo.DELETE, it.uuid, it) }!!
        }
            ?: throw RaquetaNotFoundException("No se ha encontrado la raqueta con uuid: ${raqueta.uuid}")

    }

    override suspend fun deleteByUuid(uuid: UUID): Raqueta {
        logger.debug { "Servicio de raquetas deleteByUuid con uuid: $uuid" }

        val existe = raquetasRepository.findByUuid(uuid)

        existe?.let {
            return raquetasRepository.deleteByUuid(uuid)
                ?.also { onChange(Notificacion.Tipo.DELETE, it.uuid, it) }!!
        } ?: throw RaquetaNotFoundException("No se ha encontrado la raqueta con uuid: $uuid")
    }

    override suspend fun deleteById(id: Long) {
        logger.debug { "Servicio de raquetas deleteById con id: $id" }

        raquetasRepository.deleteById(id)
    }

    override suspend fun findAllPage(pageRequest: PageRequest): Flow<Page<Raqueta>> {
        logger.debug { "Servicio de raquetas findAllPage con pageRequest: $pageRequest" }

        return raquetasRepository.findAllPage(pageRequest)
    }

    override suspend fun countAll(): Long {
        logger.debug { "Servicio de raquetas countAll" }

        return raquetasRepository.countAll()
    }

    override suspend fun findRepresentante(id: UUID): Representante {
        logger.debug { "findRepresentante: Buscando representante en servicio" }

        return representesRepository.findByUuid(id)
            ?: throw RepresentanteNotFoundException("No se ha encontrado el representante con id: $id")
    }

    // Enviamos la notificación a los clientes ws
    suspend fun onChange(tipo: Notificacion.Tipo, id: UUID, data: Raqueta? = null) {
        logger.debug { "Servicio de raquetas onChange con tipo: $tipo, id: $id, data: $data" }

        // data to json
        val mapper = jacksonObjectMapper()
        val json = mapper.writeValueAsString(
            RaquetaNotification(
                "RAQUETA",
                tipo,
                id,
                data?.toDto(findRepresentante(data.represetanteId))
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