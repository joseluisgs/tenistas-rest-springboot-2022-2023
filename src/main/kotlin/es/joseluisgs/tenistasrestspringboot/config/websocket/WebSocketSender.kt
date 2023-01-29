package es.joseluisgs.tenistasrestspringboot.config.websocket

interface WebSocketSender {
    fun sendMessage(message: String) // Método para enviar mensajes a todos los clientes
    fun sendPeriodicMessages()
}