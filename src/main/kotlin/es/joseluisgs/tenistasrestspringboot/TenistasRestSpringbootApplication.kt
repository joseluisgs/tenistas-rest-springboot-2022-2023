package es.joseluisgs.tenistasrestspringboot

import mu.KotlinLogging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

private val logger = KotlinLogging.logger {}

@SpringBootApplication // Indicamos que es una aplicación Spring Boot
@EnableCaching // Habilitamos el cacheo
class TenistasRestSpringbootApplication {
    /*
    Si quiero ejecutar algo antes de arrancar la aplicación, como por ejemplo cargar datos de prueba
    borrar datos de prueba o lo que sea
    en la base de datos, puedo hacerlo con esta clase, que implementa CommandLineRunner

        : CommandLineRunner {
        @Autowired
        lateinit var service: UsuariosService
        override fun run(vararg args: String?) = runBlocking {

            logger.info { "Ejecutando código antes de arrancar la aplicación" }

            // vamos a probar los metodos de busqueda
            val usuario = service.loadUserById(1)!!
            println(usuario.toString())
            println(usuario.rol)

            val usuario2 = service.loadUserByUsername("pepe")
            println(usuario2.authorities)

            service.findAll().toList().forEach { println(it) }

            val userCreateDto = UsuarioCreateDto(
                nombre = "test",
                email = "test@test.com",
                username = "test",
                password = "test"
            )
        }

    */
}


fun main(args: Array<String>) {
    runApplication<TenistasRestSpringbootApplication>(*args)
}
