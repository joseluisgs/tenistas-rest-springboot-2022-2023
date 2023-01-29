package es.joseluisgs.tenistasrestspringboot.controllers

import es.joseluisgs.tenistasrestspringboot.config.APIConfig
import es.joseluisgs.tenistasrestspringboot.dto.RepresentanteDto
import es.joseluisgs.tenistasrestspringboot.dto.RepresentanteRequestDto
import es.joseluisgs.tenistasrestspringboot.dto.RepresentantesPageDto
import es.joseluisgs.tenistasrestspringboot.exceptions.RepresentanteBadRequestException
import es.joseluisgs.tenistasrestspringboot.exceptions.RepresentanteConflictIntegrityException
import es.joseluisgs.tenistasrestspringboot.exceptions.RepresentanteNotFoundException
import es.joseluisgs.tenistasrestspringboot.mappers.toDto
import es.joseluisgs.tenistasrestspringboot.mappers.toModel
import es.joseluisgs.tenistasrestspringboot.services.representantes.RepresentantesService
import es.joseluisgs.tenistasrestspringboot.validators.validate
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

private val logger = KotlinLogging.logger {}

// Podemos evitar los try catch ya que podemos usar el ResponseStatusException en las excepciones
// El problema de hacerlo así es que pierdes el control de como mapear los errores
// Elige el que más te guste y que mejor se adapte a tu proyecto
// A mi me gusta mas este, porque sé lo que me va a devolver y puedo controlar el error
// y devolver el que yo quiera, o incluso devolver un error personalizado, o saber qué testear y esperar


@RestController
@RequestMapping(APIConfig.API_PATH + "/representantes")
class RepresentantesController
@Autowired constructor(
    private val representanteService: RepresentantesService,
) {

    @GetMapping("")
    suspend fun finAll(): ResponseEntity<List<RepresentanteDto>> {
        logger.info { "GET ALL Representantes" }

        val res = representanteService.findAll()
            .toList().map { it.toDto() }

        return ResponseEntity.ok(res)

    }

    @GetMapping("/{id}")
    suspend fun findById(@PathVariable id: UUID): ResponseEntity<RepresentanteDto> {
        logger.info { "GET By ID Representante con id: $id" }

        try {
            // Nosotros usamos el UUID, pero para el DTO es id
            val res = representanteService.findByUuid(id).toDto()
            return ResponseEntity.ok(res)
        } catch (e: RepresentanteNotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, e.message)
        }
    }

    @PostMapping("")
    suspend fun create(@Valid @RequestBody representanteDto: RepresentanteRequestDto): ResponseEntity<RepresentanteDto> {
        // Con valid hacemos la validación de los campos
        logger.info { "POST Representante" }

        try {
            val rep = representanteDto.validate().toModel()
            val res = representanteService.save(rep).toDto()
            return ResponseEntity.status(HttpStatus.CREATED).body(res)
        } catch (e: RepresentanteBadRequestException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }

    @PutMapping("/{id}")
    suspend fun update(
        @PathVariable id: UUID,
        @Valid @RequestBody representanteDto: RepresentanteRequestDto
    ): ResponseEntity<RepresentanteDto> {
        // Con valid hacemos la validación de los campos
        logger.info { "PUT Representante con id: $id" }

        try {
            val rep = representanteDto.validate().toModel()
            val res = representanteService.update(id, rep).toDto()
            return ResponseEntity.status(HttpStatus.OK).body(res)
        } catch (e: RepresentanteNotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, e.message)
        } catch (e: RepresentanteBadRequestException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }

    @DeleteMapping("/{id}")
    suspend fun delete(@PathVariable id: UUID): ResponseEntity<RepresentanteDto> {
        logger.info { "DELETE Representante con id: $id" }

        try {
            representanteService.deleteByUuid(id)
            return ResponseEntity.noContent().build()
        } catch (e: RepresentanteNotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, e.message)
        } catch (e: RepresentanteConflictIntegrityException) {
            // Puedes usar CONFLICT semánticamente es correcto
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }

    @GetMapping("find")
    suspend fun findByName(@RequestParam nombre: String): ResponseEntity<List<RepresentanteDto>> {
        logger.info { "GET By Name Representante con nombre: $nombre" }

        nombre.let {
            val res = representanteService.findByNombre(nombre.trim())
                .toList().map { it.toDto() }

            return ResponseEntity.ok(res)
        }
    }

    @GetMapping("paging")
    suspend fun getAll(
        @RequestParam(defaultValue = APIConfig.PAGINATION_INIT) page: Int,
        @RequestParam(defaultValue = APIConfig.PAGINATION_SIZE) size: Int,
        @RequestParam(defaultValue = APIConfig.PAGINATION_SORT) sortBy: String = "nombre",
        request: HttpServletRequest?
    ): ResponseEntity<RepresentantesPageDto> {
        // Consulto en base a las páginas

        logger.info { "GET Paging Representantes" }


        val pageRequest = PageRequest.of(page, size, Sort.Direction.ASC, sortBy)
        val pageResult = representanteService.findAllPage(pageRequest).firstOrNull()

        pageResult?.let {
            val dto = RepresentantesPageDto(
                content = pageResult.content.map { it.toDto() },
                currentPage = pageResult.number,
                pageSize = pageResult.size,
                totalPages = if (pageResult.totalElements % pageResult.size == 0L) pageResult.totalElements / pageResult.size else (pageResult.totalElements / pageResult.size) + 1,
                totalElements = pageResult.totalElements,
                sort = "${pageResult.sort}",
            )

            return ResponseEntity.ok(dto)

        } ?: run {
            return ResponseEntity.notFound().build()
        }
    }


}