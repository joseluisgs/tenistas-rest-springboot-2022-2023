package es.joseluisgs.tenistasrestspringboot.config.websocket

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry


@Configuration
/*
@EnableWebSocketMessageBroker
class WebSocketConfig : WebSocketMessageBrokerConfigurer {
    */
/*override fun configureMessageBroker(config: MessageBrokerRegistry) {
        config.enableSimpleBroker("/updates")
        // config.setApplicationDestinationPrefixes("/app")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        // ...
        registry.addEndpoint("/ws")
        registry.addEndpoint("/ws").withSockJS()
    }*//*

}*/

@EnableWebSocket
class ServerWebSocketConfig : WebSocketConfigurer {
    // Definimos el endpoint de nuestro WebSocket
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(webSocketHandler(), "/updates")
    }

    // Definimos el Handler de nuestro WebSocket
    @Bean
    fun webSocketHandler(): WebSocketHandler {
        return WebSocketHandler()
    }
}
