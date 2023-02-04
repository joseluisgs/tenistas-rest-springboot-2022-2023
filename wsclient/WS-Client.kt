package es.joseluisgs.tenistasrestspringboot.wsclient

import org.springframework.messaging.converter.StringMessageConverter
import org.springframework.messaging.simp.stomp.StompSessionHandler
import org.springframework.web.socket.client.WebSocketClient
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient
import java.util.*

private const val URL = "ws://localhost:6969/ws"
fun main(args: Array<String>) {
    val client: WebSocketClient = StandardWebSocketClient()
    val stompClient = WebSocketStompClient(client)
    stompClient.messageConverter = StringMessageConverter() //MappingJackson2MessageConverter()
    val sessionHandler: StompSessionHandler = MyWSSessionHandler()
    stompClient.connectAsync(URL, sessionHandler)
    Scanner(System.`in`).nextLine() // Don't close immediately.
}
