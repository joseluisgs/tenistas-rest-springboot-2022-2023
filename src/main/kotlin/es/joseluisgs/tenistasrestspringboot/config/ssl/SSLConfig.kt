package es.joseluisgs.tenistasrestspringboot.config.ssl

import org.apache.catalina.connector.Connector
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.servlet.server.ServletWebServerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class SecurityConfig {
    @Configuration
    class HttpHttpsConfigV1 {
        // (User-defined Property)
        @Value("\${server.http.port}")
        private val httpPort = 6969

        @Bean
        fun servletContainer(): ServletWebServerFactory {
            val connector = Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL)
            connector.port = httpPort
            val tomcat = TomcatServletWebServerFactory()
            tomcat.addAdditionalTomcatConnectors(connector)
            return tomcat
        }
    }

}