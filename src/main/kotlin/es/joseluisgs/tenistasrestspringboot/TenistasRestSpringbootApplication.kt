package es.joseluisgs.tenistasrestspringboot

import es.joseluisgs.tenistasrestspringboot.models.Usuario
import mu.KotlinLogging
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

private val logger = KotlinLogging.logger {}

@SpringBootApplication // Indicamos que es una aplicación Spring Boot
@EnableCaching // Habilitamos el cacheo
class TenistasRestSpringbootApplication
/*
Si quiero ejecutar algo antes de arrancar la aplicación, como por ejemplo cargar datos de prueba
borrar datos de prueba o lo que sea
en la base de datos, puedo hacerlo con esta clase, que implementa CommandLineRunner
*/
    : CommandLineRunner {
    override fun run(vararg args: String?) {
        logger.info { "Ejecutando código antes de arrancar la aplicación" }
        val roles: Set<Usuario.Rol> = setOf(Usuario.Rol.USER, Usuario.Rol.ADMIN)
        println(roles.toString())
    }

}


fun main(args: Array<String>) {
    runApplication<TenistasRestSpringbootApplication>(*args)
}
