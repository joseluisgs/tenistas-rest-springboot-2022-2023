package es.joseluisgs.tenistasrestspringboot.models

import es.joseluisgs.tenistasrestspringboot.dto.RaquetaDto
import es.joseluisgs.tenistasrestspringboot.dto.RepresentanteDto
import java.time.LocalDateTime
import java.util.*

// Las notificaciones son un modelo de datos que se usan para enviar mensajes a los usuarios
// Los tipos de cambios que permito son
data class Notificacion<T>(
    val entity: String,
    val type: Tipo,
    val id: UUID,
    val data: T,
    val createdAt: String = LocalDateTime.now().toString()
) {
    enum class Tipo { CREATE, UPDATE, DELETE }
}

// Mis alias, para no estar con los genéricos, mando el DTO por que es lo que quiero que se envíe con sus datos
// visibles en el DTO igual que se ven en las llamadas REST
typealias RepresentanteNotification = Notificacion<RepresentanteDto?> // RepresentanteDto?
typealias RaquetaNotification = Notificacion<RaquetaDto?> // RaquetaDto?

