package es.joseluisgs.tenistasrestspringboot.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

/**
 * Configuración global para
 */
@Configuration
// @EnableJpaAuditing // Activamos la auditoria, esto por ejemplo nos permite no meter la fecha si no que la tome automáticamente
class APIConfig {
    companion object {
        // Versión de la Api y versión del path, tomados de application.properties
        @Value("\${api.path}")
        const val API_PATH = "/api"

        @Value("\${api.version}")
        const val API_VERSION = "1.0"

        @Value("\${pagination.init}")
        const val PAGINATION_INIT = "0"

        @Value("\${pagination.size}")
        const val PAGINATION_SIZE = "10"

        @Value("\${pagination.sort}")
        const val PAGINATION_SORT = "id"

        @Value("\${project.name}")
        const val PROJECT_NAME = "Tenistas API REST Spring Boot"

        @Value("\${spring.profiles.active}")
        const val PROFILE = "dev"

    }
}