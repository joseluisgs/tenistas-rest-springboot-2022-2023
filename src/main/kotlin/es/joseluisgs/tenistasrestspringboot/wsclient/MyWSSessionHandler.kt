package es.joseluisgs.tenistasrestspringboot.wsclient

import mu.KotlinLogging
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaders
import org.springframework.messaging.simp.stomp.StompSession
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter
import java.lang.reflect.Type


private val logger = KotlinLogging.logger {}

class MyWSSessionHandler : StompSessionHandlerAdapter() {
    override fun afterConnected(session: StompSession, connectedHeaders: StompHeaders) {
        logger.info { "Nueva sesión establecida : " + session.sessionId }
        session.subscribe("/updates/representantes", this)
        logger.info { "Subscribed to /updates/representantes" }
        session.subscribe("/updates/raquetas", this)
        logger.info { "Subscribed to /updates/raquetas" }
        session.subscribe("/updates/tenistas", this)
        logger.info { "Subscribed to /updates/tenistas" }
        // session.send("/app/chat", sampleMessage)
        // logger.info { "Message sent to websocket server" }
    }

    override fun handleException(
        session: StompSession,
        command: StompCommand?,
        headers: StompHeaders,
        payload: ByteArray,
        exception: Throwable
    ) {
        logger.error { "Got an exception ${exception.message}" }
    }

    override fun getPayloadType(headers: StompHeaders): Type {
        return String::class.java
    }

    override fun handleFrame(headers: StompHeaders, payload: Any?) {
        val msg: String? = payload as String?
        logger.info("Received : $msg")
        println("Received : $msg")

    }
}