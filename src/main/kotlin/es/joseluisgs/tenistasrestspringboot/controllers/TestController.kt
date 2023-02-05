package es.joseluisgs.tenistasrestspringboot.controllers

import es.joseluisgs.tenistasrestspringboot.config.APIConfig
import es.joseluisgs.tenistasrestspringboot.dto.TestDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
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
    @Operation(summary = "Get all Test", description = "Obtiene una lista de objetos Test", tags = ["Test"])
    @Parameter(name = "texto", description = "Texto a buscar", required = false, example = "Hola")
    @ApiResponse(responseCode = "200", description = "Lista de Test")
    @GetMapping("")
    fun getAll(@RequestParam texto: String?): ResponseEntity<List<TestDto>> {
        logger.info { "GET ALL Test" }
        return ResponseEntity.ok(listOf(TestDto("Hola : Query: $texto"), TestDto("Mundo : Query: $texto")))
    }

    // GET: /test/{id}
    @Operation(summary = "Get Test by ID", description = "Obtiene un objeto Test por su ID", tags = ["Test"])
    @Parameter(name = "id", description = "ID del Test", required = true, example = "1")
    @ApiResponse(responseCode = "200", description = "Test encontrado")
    @ApiResponse(responseCode = "404", description = "Test no encontrado si id = kaka")
    @ApiResponse(responseCode = "403", description = "No tienes permisos si id = admin")
    @ApiResponse(responseCode = "401", description = "No autorizado si id = nopuedes")
    @ApiResponse(responseCode = "500", description = "Error interno si id = error")
    @ApiResponse(responseCode = "400", description = "Petición incorrecta si id = otro")
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
    @Operation(summary = "Create Test", description = "Crea un objeto Test", tags = ["Test"])
    @ApiResponse(responseCode = "201", description = "Test creado")
    @PostMapping("")
    fun create(@Valid @RequestBody testDto: TestDto): ResponseEntity<TestDto> {
        logger.info { "POST Test" }
        val new = TestDto("Hola POST ${testDto.message}")
        return ResponseEntity.status(HttpStatus.CREATED).body(new)
    }

    // PUT:/test/{id}
    @Operation(summary = "Update Test", description = "Modifica un objeto Test", tags = ["Test"])
    @Parameter(name = "id", description = "ID del Test", required = true, example = "1")
    @ApiResponse(responseCode = "200", description = "Test modificado")
    @ApiResponse(responseCode = "404", description = "Test no encontrado si id = kaka")
    @PutMapping("/{id}")
    fun update(@PathVariable id: String, @RequestBody testDto: TestDto): ResponseEntity<TestDto> {
        logger.info { "PUT Test" }
        return if (id != "kaka") {
            val new = TestDto("Hola PUT $id: ${testDto.message}")
            ResponseEntity.status(HttpStatus.OK).body(new)
        } else
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(TestDto("No encontrado"))
    }

    // PATCH:/test/{id}
    @Operation(summary = "Patch Test", description = "Modifica un objeto Test", tags = ["Test"])
    @Parameter(name = "id", description = "ID del Test", required = true, example = "1")
    @ApiResponse(responseCode = "200", description = "Test modificado")
    @ApiResponse(responseCode = "404", description = "Test no encontrado si id = kaka")
    @PatchMapping("/{id}")
    fun patch(@PathVariable id: String, @RequestBody testDto: TestDto): ResponseEntity<TestDto> {
        logger.info { "PATCH Test" }
        return if (id != "kaka") {
            val new = TestDto("Hola PATCH $id: ${testDto.message}")
            ResponseEntity.status(HttpStatus.OK).body(new)
        } else
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(TestDto("No encontrado"))
    }

    // DELETE:/test/{id}
    @Operation(summary = "Delete Test", description = "Elimina un objeto Test", tags = ["Test"])
    @Parameter(name = "id", description = "ID del Test", required = true, example = "1")
    @ApiResponse(responseCode = "204", description = "Test eliminado")
    @ApiResponse(responseCode = "404", description = "Test no encontrado si id = kaka")
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String): ResponseEntity<Unit> {
        logger.info { "DELETE Test" }
        return if (id != "kaka") {
            ResponseEntity.noContent().build()
        } else
            ResponseEntity.notFound().build()
    }
}