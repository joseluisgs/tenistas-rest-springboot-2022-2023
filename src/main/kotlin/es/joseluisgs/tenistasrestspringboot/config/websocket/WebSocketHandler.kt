package es.joseluisgs.tenistasrestspringboot.config.websocket

import mu.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.SubProtocolCapable
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.io.IOException
import java.time.LocalTime
import java.util.concurrent.CopyOnWriteArraySet

private val logger = KotlinLogging.logger {}

class WebSocketHandler : TextWebSocketHandler(), SubProtocolCapable, WebSocketSender {
    // Para poder enviar mensajes a todos los clientes almacenamos la sesión de cada uno
    // Patron observer
    private val sessions: MutableSet<WebSocketSession> = CopyOnWriteArraySet()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        logger.info { "Conexión establecida con el servidor" }
        logger.info { "Sesión: $session" }
        sessions.add(session)
        val message = TextMessage("Updates de Tenistas API REST Spring Boot")
        logger.info { "Servidor envía: $message" }
        session.sendMessage(message)
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        logger.info { "Conexión cerrada con el servidor: $status" }
        sessions.remove(session)
    }

    // Para poder enviar mensajes a todos los clientes almacenamos la sesión de cada uno
    override fun sendMessage(message: String) {
        logger.info { "Enviar mensaje: $message" }
        for (session in sessions) {
            if (session.isOpen) {
                logger.info { "Servidor envía: $message" }
                session.sendMessage(TextMessage(message))
            }
        }
    }

    @Scheduled(fixedRate = 1000)
    @Throws(IOException::class)
    override fun sendPeriodicMessages() {
        for (session in sessions) {
            if (session.isOpen) {
                val broadcast = "server periodic message " + LocalTime.now()
                logger.info("Server sends: {}", broadcast)
                session.sendMessage(TextMessage(broadcast))
            }
        }
    }

    @Throws(Exception::class)
    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        // No hago nada con los mensajes que me llegan

        /*val request = message.payload
        logger.info("Server received: {}", request)
        val response = String.format("response from server to '%s'", HtmlUtils.htmlEscape(request))
        logger.info("Server sends: {}", response)
        session.sendMessage(TextMessage(response))*/
    }

    override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
        logger.info { "Error de transporte con el servidor ${exception.message}" }
    }

    override fun getSubProtocols(): List<String> {
        return listOf("subprotocol.demo.websocket")
    }
}