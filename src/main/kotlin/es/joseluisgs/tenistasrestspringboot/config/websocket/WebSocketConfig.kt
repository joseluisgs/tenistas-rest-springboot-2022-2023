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
    // Definimos el endpoints de nuestro WebSocket
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        // cada endpoint con su handler
        registry.addHandler(webSocketRaquetasHandler(), "api/updates/raquetas")
        registry.addHandler(webSocketRepresentantesHandler(), "api/updates/representantes")
        registry.addHandler(webSocketTenistasHandler(), "api/updates/tenistas")
    }

    // Definimos el Handler de nuestro WebSocket o handler para atenderlos
    @Bean
    fun webSocketRaquetasHandler(): WebSocketHandler {
        return WebSocketHandler("Raquetas")
    }

    @Bean
    fun webSocketRepresentantesHandler(): WebSocketHandler {
        return WebSocketHandler("Representantes")
    }

    @Bean
    fun webSocketTenistasHandler(): WebSocketHandler {
        return WebSocketHandler("Tenistas")
    }
}
