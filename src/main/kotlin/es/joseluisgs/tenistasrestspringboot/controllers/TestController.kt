package es.joseluisgs.tenistasrestspringboot.controllers

import es.joseluisgs.tenistasrestspringboot.config.APIConfig
import es.joseluisgs.tenistasrestspringboot.dto.TestDto
import jakarta.validation.Valid
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping(APIConfig.API_PATH + "/test")
class TestController {

    // GET: /test?text=Hola
    @GetMapping("")
    fun getAll(@RequestParam texto: String?): ResponseEntity<List<TestDto>> {
        logger.info { "GET ALL Test" }
        return ResponseEntity.ok(listOf(TestDto("Hola : Query: $texto"), TestDto("Mundo : Query: $texto")))
    }

    // GET: /test/{id}
    @GetMapping("/{id}")
    fun getById(@PathVariable id: String): ResponseEntity<TestDto> {
        logger.info { "GET BY ID Test" }
        return when (id) {
            // Ejemplos de codigos de respuesta
            "1" -> ResponseEntity.ok(TestDto("Hola GET BY $id"))
            "kaka" -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(TestDto("No encontrado"))
            "admin" -> ResponseEntity.status(HttpStatus.FORBIDDEN).body(TestDto("No tienes permisos"))
            "nopuedes" -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(TestDto("No autorizado"))
            "error" -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(TestDto("Error interno"))
            else -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(TestDto("Hola GET BY $id"))

        }
    }

    // POST:/test
    @PostMapping("")
    fun create(@Valid @RequestBody testDto: TestDto): ResponseEntity<TestDto> {
        logger.info { "POST Test" }
        val new = TestDto("Hola POST ${testDto.message}")
        return ResponseEntity.status(HttpStatus.CREATED).body(new)
    }

    // PUT:/test/{id}
    @PutMapping("/{id}")
    fun update(@PathVariable id: String, @RequestBody testDto: TestDto): ResponseEntity<TestDto> {
        logger.info { "PUT Test" }
        val new = TestDto("Hola PUT $id: ${testDto.message}")
        return ResponseEntity.status(HttpStatus.OK).body(new)
    }

    // PATCH:/test/{id}
    @PatchMapping("/{id}")
    fun patch(@PathVariable id: String, @RequestBody testDto: TestDto): ResponseEntity<TestDto> {
        logger.info { "PATCH Test" }
        val new = TestDto("Hola PATCH $id: ${testDto.message}")
        return ResponseEntity.status(HttpStatus.OK).body(new)
    }

    // DELETE:/test/{id}
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String): ResponseEntity<Unit> {
        logger.info { "DELETE Test" }
        val new = TestDto("Hola DELETE $id")
        return ResponseEntity.noContent().build()
    }
}