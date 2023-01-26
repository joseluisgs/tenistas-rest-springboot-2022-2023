package es.joseluisgs.tenistasrestspringboot.config.ssl

import org.apache.catalina.connector.Connector
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.servlet.server.ServletWebServerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


// Por defecto la conexion es con SSL, por lo que vamos a decirle que use el puerto 6969
// para la conexión sin SSL
@Configuration
class SSLConfig {
    // (User-defined Property)
    @Value("\${server.http.port}")
    private val httpPort = "6969"

    // Creamos un bean que nos permita configurar el puerto de conexión sin SSL
    @Bean
    fun servletContainer(): ServletWebServerFactory {
        val connector = Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL)
        connector.port = httpPort.toInt()
        val tomcat = TomcatServletWebServerFactory()
        tomcat.addAdditionalTomcatConnectors(connector)
        return tomcat
    }
}

