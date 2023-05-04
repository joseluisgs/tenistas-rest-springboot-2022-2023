package es.joseluisgs.tenistasrestspringboot.controllers

import com.github.michaelbull.result.*
import es.joseluisgs.tenistasrestspringboot.config.APIConfig
import es.joseluisgs.tenistasrestspringboot.dto.RepresentanteDto
import es.joseluisgs.tenistasrestspringboot.dto.RepresentanteRequestDto
import es.joseluisgs.tenistasrestspringboot.dto.RepresentantesPageDto
import es.joseluisgs.tenistasrestspringboot.errors.RepresentanteError
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


/**
 * Controlador de Rest de Representantes
 * @param representanteService: Servicio de Representantes
 */
@RestController
@RequestMapping(APIConfig.API_PATH + "/representantes")
class RepresentantesController
@Autowired constructor(
    private val representanteService: RepresentantesService,
) {

    /**
     * GET all: Lista de representantes
     * @return ResponseEntity<List<RepresentanteDto>> con el listado de Representantes
     */
    @GetMapping("")
    suspend fun finAll(): ResponseEntity<List<RepresentanteDto>> {
        logger.info { "GET ALL Representantes" }

        val res = representanteService.findAll()
            .toList().map { it.toDto() }

        return ResponseEntity.ok(res)

    }

    /**
     * GET by id: Obtiene un representante por su id
     * @param id: UUID del representante
     * @return ResponseEntity<RepresentanteDto> con el representante
     * @throws ResponseStatusException con el error 404 si no lo encuentra
     */
    @GetMapping("/{id}")
    suspend fun findById(@PathVariable id: UUID): ResponseEntity<RepresentanteDto> {
        logger.info { "GET By ID Representante con id: $id" }

        // Nosotros usamos el UUID, pero para el DTO es id
        representanteService.findByUuid(id).mapBoth(
            success = { return ResponseEntity.ok(it.toDto()) },
            failure = { return handleErrors(it) }
        )
    }

    /**
     * POST: Crea un nuevo representante
     * @param representanteDto: RepresentanteRequestDto con los datos del representante
     * @return ResponseEntity<RepresentanteDto> con el representante creado
     * @throws ResponseStatusException con el error 400 si no es válido
     */
    @PostMapping("")
    suspend fun create(@Valid @RequestBody representanteDto: RepresentanteRequestDto): ResponseEntity<RepresentanteDto> {
        // Con valid hacemos la validación de los campos
        logger.info { "POST Representante" }

        representanteDto.validate().andThen {
            representanteService.save(it.toModel())
        }.mapBoth(
            success = { return ResponseEntity.status(HttpStatus.CREATED).body(it.toDto()) },
            failure = { return handleErrors(it) }
        )
    }


    /**
     * PUT: Modifica un nuevo representante
     * @param id: UUID del representante
     * @param representanteDto: RepresentanteRequestDto con los datos del representante
     * @return ResponseEntity<RepresentanteDto> con el representante modificado
     * @throws ResponseStatusException con el error 400 si no es válido
     * @throws ResponseStatusException con el error 404 si no lo encuentra
     */
    @PutMapping("/{id}")
    suspend fun update(
        @PathVariable id: UUID,
        @Valid @RequestBody representanteDto: RepresentanteRequestDto
    ): ResponseEntity<RepresentanteDto> {
        // Con valid hacemos la validación de los campos
        logger.info { "PUT Representante con id: $id" }

        representanteDto.validate().andThen {
            representanteService.update(id, it.toModel())
        }.mapBoth(
            success = { return ResponseEntity.ok(it.toDto()) },
            failure = { return handleErrors(it) }
        )
    }

    /**
     * DELETE: Elimina un representante
     * @param id: UUID del representante
     * @return ResponseEntity<RepresentanteDto> con el representante eliminado
     * @throws ResponseStatusException con el error 404 si no lo encuentra
     * @throws ResponseStatusException con el error 400 si no se puede eliminar
     */
    @DeleteMapping("/{id}")
    suspend fun delete(@PathVariable id: UUID): ResponseEntity<RepresentanteDto> {
        logger.info { "DELETE Representante con id: $id" }

        representanteService.deleteByUuid(id).mapBoth(
            success = { return ResponseEntity.noContent().build() },
            failure = { return handleErrors(it) }
        )
    }

    /**
     * GET by name: Obtiene un representante por su nombre
     * @param nombre: Nombre del representante como parámetro de búsqueda
     * @return ResponseEntity<List<RepresentanteDto>> con el listado de representantes
     */
    @GetMapping("find")
    suspend fun findByName(@RequestParam nombre: String): ResponseEntity<List<RepresentanteDto>> {
        logger.info { "GET By Name Representante con nombre: $nombre" }

        nombre.let {
            val res = representanteService.findByNombre(nombre.trim())
                .toList().map { it.toDto() }

            return ResponseEntity.ok(res)
        }
    }

    /**
     * GET All: Obtiene todos los representantes paginados
     * @param page: Página a consultar (0 por defecto)
     * @param size: Tamaño de la página (10 por defecto)
     * @param sortBy: Campo por el que ordenar (nombre por defecto)
     */
    @GetMapping("paging")
    suspend fun getAll(
        @RequestParam(defaultValue = APIConfig.PAGINATION_INIT) page: Int = 0,
        @RequestParam(defaultValue = APIConfig.PAGINATION_SIZE) size: Int = 10,
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

    private fun handleErrors(representanteError: RepresentanteError): ResponseEntity<RepresentanteDto> {
        when (representanteError) {
            is RepresentanteError.NotFound -> throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                representanteError.message
            )

            is RepresentanteError.BadRequest -> throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                representanteError.message
            )

            is RepresentanteError.ConflictIntegrity -> throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                representanteError.message
            )
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