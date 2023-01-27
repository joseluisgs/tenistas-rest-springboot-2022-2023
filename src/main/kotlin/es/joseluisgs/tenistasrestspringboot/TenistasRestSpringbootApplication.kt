package es.joseluisgs.tenistasrestspringboot

import mu.KotlinLogging
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

private val logger = KotlinLogging.logger {}

@SpringBootApplication
@EnableCaching
class TenistasRestSpringbootApplication : CommandLineRunner {
    override fun run(vararg args: String?) {
    }

}

fun main(args: Array<String>) {
    runApplication<TenistasRestSpringbootApplication>(*args)
}
