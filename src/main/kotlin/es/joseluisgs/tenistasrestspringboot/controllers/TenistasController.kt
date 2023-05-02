package es.joseluisgs.tenistasrestspringboot.controllers

import es.joseluisgs.tenistasrestspringboot.config.APIConfig
import es.joseluisgs.tenistasrestspringboot.dto.*
import es.joseluisgs.tenistasrestspringboot.exceptions.*
import es.joseluisgs.tenistasrestspringboot.mappers.toDto
import es.joseluisgs.tenistasrestspringboot.mappers.toModel
import es.joseluisgs.tenistasrestspringboot.mappers.toTenistaDto
import es.joseluisgs.tenistasrestspringboot.services.tenistas.TenistasService
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
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException
import org.springframework.validation.FieldError
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
@RequestMapping(APIConfig.API_PATH + "/tenistas")
class TenistasController
@Autowired constructor(
    private val tenistasService: TenistasService,
) {

    @GetMapping("")
    suspend fun finAll(): ResponseEntity<List<TenistaDto>> {
        logger.info { "GET ALL Tenistas" }

        val res = tenistasService.findAll()
            .toList()
            .map { it.toDto(tenistasService.findRaqueta(it.raquetaId)) }

        return ResponseEntity.ok(res)

    }

    @GetMapping("/{id}")
    suspend fun findById(@PathVariable id: UUID): ResponseEntity<TenistaDto> {
        logger.info { "GET By ID Tenista con id: $id" }

        try {
            // Nosotros usamos el UUID, pero para el DTO es id
            val tenista = tenistasService.findByUuid(id)
            val res = tenista.toDto(tenistasService.findRaqueta(tenista.raquetaId))
            return ResponseEntity.ok(res)
        } catch (e: TenistaNotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, e.message)
        }
    }

    @PostMapping("")
    suspend fun create(@Valid @RequestBody tenistaDto: TenistaCreateDto): ResponseEntity<TenistaDto> {
        // Con valid hacemos la validación de los campos
        logger.info { "POST Tenista" }

        try {
            val rep = tenistaDto.validate().toModel()
            val res = tenistasService.save(rep).toDto(tenistasService.findRaqueta(rep.raquetaId))
            return ResponseEntity.status(HttpStatus.CREATED).body(res)
        } catch (e: TenistaBadRequestException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        } catch (e: RaquetaNotFoundException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }

    @PutMapping("/{id}")
    suspend fun update(
        @PathVariable id: UUID,
        @Valid @RequestBody tenistaDto: TenistaCreateDto
    ): ResponseEntity<TenistaDto> {
        // Con valid hacemos la validación de los campos
        logger.info { "PUT Tenista con id: $id" }

        try {
            val rep = tenistaDto.validate().toModel()
            val res = tenistasService.update(id, rep).toDto(tenistasService.findRaqueta(rep.raquetaId))
            return ResponseEntity.status(HttpStatus.OK).body(res)
        } catch (e: TenistaNotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, e.message)
        } catch (e: RaquetaNotFoundException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        } catch (e: TenistaBadRequestException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }

    @DeleteMapping("/{id}")
    suspend fun delete(@PathVariable id: UUID): ResponseEntity<TenistaDto> {
        logger.info { "DELETE Tenista con id: $id" }

        try {
            tenistasService.deleteByUuid(id)
            return ResponseEntity.noContent().build()
        } catch (e: TenistaNotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, e.message)
        }
    }

    @GetMapping("find")
    suspend fun findByName(@RequestParam nombre: String): ResponseEntity<List<TenistaDto>> {
        logger.info { "GET By Nombre Tenista con nombre: $nombre" }

        nombre.let {
            val res = tenistasService.findByNombre(nombre.trim())
                .toList()
                .map { it.toDto(tenistasService.findRaqueta(it.raquetaId)) }

            return ResponseEntity.ok(res)
        }
    }

    @GetMapping("{id}/raqueta")
    suspend fun findRaqueta(@PathVariable id: UUID): ResponseEntity<RaquetaTenistaDto> {
        logger.info { "GET By ID Raqueta del tenista con id: $id" }

        try {
            val raqueta = tenistasService.findByUuid(id)
            val res = tenistasService.findRaqueta(raqueta.raquetaId)
            res?.let {
                return ResponseEntity.ok(it.toTenistaDto())
            } ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "No se encontró la raqueta o no tiene raqueta asignada el tenista con id: $id"
            )
        } catch (e: RaquetaNotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, e.message)
        } catch (e: RepresentanteNotFoundException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }

    @GetMapping("/ranking/{ranking}")
    suspend fun findByRanking(@PathVariable ranking: Int): ResponseEntity<TenistaDto> {
        logger.info { "GET By Ranking Tenista con ranking: $ranking" }

        try {
            val tenista = tenistasService.findByRanking(ranking)
            val res = tenista.toDto(tenistasService.findRaqueta(tenista.raquetaId))
            return ResponseEntity.ok(res)
        } catch (e: TenistaNotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, e.message)
        } catch (e: RepresentanteNotFoundException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }

    @GetMapping("paging")
    suspend fun getAll(
        @RequestParam(defaultValue = APIConfig.PAGINATION_INIT) page: Int,
        @RequestParam(defaultValue = APIConfig.PAGINATION_SIZE) size: Int,
        @RequestParam(defaultValue = APIConfig.PAGINATION_SORT) sortBy: String = "marca",
        request: HttpServletRequest?
    ): ResponseEntity<TenistasPageDto> {
        // Consulto en base a las páginas

        logger.info { "GET Paging Tenistas" }


        val pageRequest = PageRequest.of(page, size, Sort.Direction.ASC, sortBy)
        val pageResult = tenistasService.findAllPage(pageRequest).firstOrNull()

        pageResult?.let {
            val dto = TenistasPageDto(
                content = pageResult.content.map { it.toDto(tenistasService.findRaqueta(it.raquetaId)) },
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

    // Para capturar los errores de validación
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(
        ex: MethodArgumentNotValidException
    ): Map<String, String>? {
        val errors: MutableMap<String, String> = HashMap()
        ex.bindingResult?.allErrors?.forEach { error ->
            val fieldName = (error as FieldError).field
            val errorMessage: String? = error.getDefaultMessage()
            errors[fieldName] = errorMessage ?: ""
        }
        return errors
    }


}