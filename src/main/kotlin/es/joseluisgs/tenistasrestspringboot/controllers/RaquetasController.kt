package es.joseluisgs.tenistasrestspringboot.controllers

import es.joseluisgs.tenistasrestspringboot.config.APIConfig
import es.joseluisgs.tenistasrestspringboot.dto.*
import es.joseluisgs.tenistasrestspringboot.exceptions.*
import es.joseluisgs.tenistasrestspringboot.mappers.toDto
import es.joseluisgs.tenistasrestspringboot.mappers.toModel
import es.joseluisgs.tenistasrestspringboot.services.raquetas.RaquetasService
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
import java.util.*


private val logger = KotlinLogging.logger {}

// Podemos evitar los try catch ya que podemos usar el ResponseStatusException en las excepciones
// El problema de hacerlo así es que pierdes el control de como mapear los errores
// Elige el que más te guste y que mejor se adapte a tu proyecto
// A mi me gusta mas este, porque sé lo que me va a devolver y puedo controlar el error
// y devolver el que yo quiera, o incluso devolver un error personalizado, o saber qué testear y esperar


@RestController
@RequestMapping(APIConfig.API_PATH + "/raquetas")
class RaquetasController
@Autowired constructor(
    private val raquetasService: RaquetasService,
) {

    @GetMapping("")
    suspend fun finAll(): ResponseEntity<List<RaquetaDto>> {
        logger.info { "GET ALL Raquetas" }

        val res = raquetasService.findAll()
            .toList()
            .map { it.toDto(raquetasService.findRepresentante(it.representanteId)) }

        return ResponseEntity.ok(res)

    }

    @GetMapping("/{id}")
    suspend fun findById(@PathVariable id: UUID): ResponseEntity<RaquetaDto> {
        logger.info { "GET By ID Raqueta con id: $id" }

        // Nosotros usamos el UUID, pero para el DTO es id
        val raqueta = raquetasService.findByUuid(id)
        val res = raqueta.toDto(raquetasService.findRepresentante(raqueta.representanteId))
        return ResponseEntity.ok(res)
    }

    @PostMapping("")
    suspend fun create(@Valid @RequestBody raquetaDto: RaquetaCreateDto): ResponseEntity<RaquetaDto> {
        // Con valid hacemos la validación de los campos
        logger.info { "POST Raqueta" }

        val rep = raquetaDto.validate().toModel()
        val res = raquetasService.save(rep).toDto(raquetasService.findRepresentante(rep.representanteId))
        return ResponseEntity.status(HttpStatus.CREATED).body(res)
    }

    @PutMapping("/{id}")
    suspend fun update(
        @PathVariable id: UUID,
        @Valid @RequestBody raquetaDto: RaquetaCreateDto
    ): ResponseEntity<RaquetaDto> {
        // Con valid hacemos la validación de los campos
        logger.info { "PUT Raqueta con id: $id" }

        val rep = raquetaDto.validate().toModel()
        val res = raquetasService.update(id, rep).toDto(raquetasService.findRepresentante(rep.representanteId))
        return ResponseEntity.status(HttpStatus.OK).body(res)

    }

    @DeleteMapping("/{id}")
    suspend fun delete(@PathVariable id: UUID): ResponseEntity<RaquetaDto> {
        logger.info { "DELETE Raqueta con id: $id" }

        raquetasService.deleteByUuid(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("find")
    suspend fun findByName(@RequestParam marca: String): ResponseEntity<List<RaquetaDto>> {
        logger.info { "GET By Marca Raqueta con marca: $marca" }

        marca.let {
            val res = raquetasService.findByMarca(marca.trim())
                .toList()
                .map { it.toDto(raquetasService.findRepresentante(it.representanteId)) }

            return ResponseEntity.ok(res)
        }
    }

    @GetMapping("{id}/representante")
    suspend fun findRepresentante(@PathVariable id: UUID): ResponseEntity<RepresentanteDto> {
        logger.info { "GET By ID Representante de la raqueta con id: $id" }

        val raqueta = raquetasService.findByUuid(id)
        val res = raquetasService.findRepresentante(raqueta.representanteId).toDto()
        return ResponseEntity.ok(res)

    }

    @GetMapping("paging")
    suspend fun getAll(
        @RequestParam(defaultValue = APIConfig.PAGINATION_INIT) page: Int = 0,
        @RequestParam(defaultValue = APIConfig.PAGINATION_SIZE) size: Int = 10,
        @RequestParam(defaultValue = APIConfig.PAGINATION_SORT) sortBy: String = "marca",
        request: HttpServletRequest?
    ): ResponseEntity<RaquetasPageDto> {
        // Consulto en base a las páginas

        logger.info { "GET Paging Raquetas" }


        val pageRequest = PageRequest.of(page, size, Sort.Direction.ASC, sortBy)
        val pageResult = raquetasService.findAllPage(pageRequest).firstOrNull()

        pageResult?.let {
            val dto = RaquetasPageDto(
                content = pageResult.content.map { it.toDto(raquetasService.findRepresentante(it.representanteId)) },
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