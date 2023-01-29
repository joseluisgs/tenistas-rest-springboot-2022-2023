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
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

private val logger = KotlinLogging.logger {}

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
            .map { it.toDto(raquetasService.findRepresentante(it.represetanteId)) }

        return ResponseEntity.ok(res)

    }

    @GetMapping("/{id}")
    suspend fun findById(@PathVariable id: UUID): ResponseEntity<RaquetaDto> {
        logger.info { "GET By ID Raqueta con id: $id" }

        try {
            // Nosotros usamos el UUID, pero para el DTO es id
            val raqueta = raquetasService.findByUuid(id)
            val res = raqueta.toDto(raquetasService.findRepresentante(raqueta.represetanteId))
            return ResponseEntity.ok(res)
        } catch (e: RepresentanteNotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, e.message)
        }
    }

    @PostMapping("")
    suspend fun create(@Valid @RequestBody raquetaDto: RaquetaCreateDto): ResponseEntity<RaquetaDto> {
        // Con valid hacemos la validación de los campos
        logger.info { "POST Raqueta" }

        try {
            val rep = raquetaDto.validate().toModel()
            val res = raquetasService.save(rep).toDto(raquetasService.findRepresentante(rep.represetanteId))
            return ResponseEntity.status(HttpStatus.CREATED).body(res)
        } catch (e: RaquetaBadRequestException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        } catch (e: RepresentanteNotFoundException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }

    @PutMapping("/{id}")
    suspend fun update(
        @PathVariable id: UUID,
        @Valid @RequestBody raquetaDto: RaquetaCreateDto
    ): ResponseEntity<RaquetaDto> {
        // Con valid hacemos la validación de los campos
        logger.info { "PUT Raqueta con id: $id" }

        try {
            val rep = raquetaDto.validate().toModel()
            val res = raquetasService.update(id, rep).toDto(raquetasService.findRepresentante(rep.represetanteId))
            return ResponseEntity.status(HttpStatus.OK).body(res)
        } catch (e: RaquetaNotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, e.message)
        } catch (e: RepresentanteNotFoundException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        } catch (e: RaquetaBadRequestException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }

    @DeleteMapping("/{id}")
    suspend fun delete(@PathVariable id: UUID): ResponseEntity<RaquetaDto> {
        logger.info { "DELETE Raqueta con id: $id" }

        try {
            raquetasService.deleteByUuid(id)
            return ResponseEntity.noContent().build()
        } catch (e: RaquetaNotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, e.message)
        } catch (e: RaquetaConflictIntegrityException) {
            // Puedes usar CONFLICT semánticamente es correcto
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }

    @GetMapping("find")
    suspend fun findByName(@RequestParam marca: String): ResponseEntity<List<RaquetaDto>> {
        logger.info { "GET By Marca Raqueta con marca: $marca" }

        marca.let {
            val res = raquetasService.findByMarca(marca.trim())
                .toList()
                .map { it.toDto(raquetasService.findRepresentante(it.represetanteId)) }

            return ResponseEntity.ok(res)
        }
    }

    @GetMapping("{id}/representante")
    suspend fun findRepresentante(@PathVariable id: UUID): ResponseEntity<RepresentanteDto> {
        logger.info { "GET By ID Representante de la raqueta con id: $id" }

        try {
            val raqueta = raquetasService.findByUuid(id)
            val res = raquetasService.findRepresentante(raqueta.represetanteId).toDto()
            return ResponseEntity.ok(res)
        } catch (e: RaquetaNotFoundException) {
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
    ): ResponseEntity<RaquetasPageDto> {
        // Consulto en base a las páginas

        logger.info { "GET Paging Raquetas" }


        val pageRequest = PageRequest.of(page, size, Sort.Direction.ASC, sortBy)
        val pageResult = raquetasService.findAllPage(pageRequest).firstOrNull()

        pageResult?.let {
            val dto = RaquetasPageDto(
                content = pageResult.content.map { it.toDto(raquetasService.findRepresentante(it.represetanteId)) },
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