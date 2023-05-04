package es.joseluisgs.tenistasrestspringboot.controllers

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import es.joseluisgs.tenistasrestspringboot.dto.TenistaCreateDto
import es.joseluisgs.tenistasrestspringboot.errors.TenistaError
import es.joseluisgs.tenistasrestspringboot.mappers.toDto
import es.joseluisgs.tenistasrestspringboot.models.Raqueta
import es.joseluisgs.tenistasrestspringboot.models.Tenista
import es.joseluisgs.tenistasrestspringboot.services.tenistas.TenistasService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import joseluisgs.es.utils.toUUID
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate
import java.util.*

@ExtendWith(MockKExtension::class)
@SpringBootTest
class TenistasControllerTest {

    @MockK // @MockkBean
    private lateinit var service: TenistasService

    @InjectMockKs // @Autowired
    lateinit var controller: TenistasController

    final val raqueta = Raqueta(
        uuid = "a765df90-e4aa-4306-b93e-20500accf8f7".toUUID(),
        marca = "Test",
        precio = 99.99,
        representanteId = "71dac885-bfa5-465e-9bff-47a5902adae6".toUUID()
    )

    private val tenista = Tenista(
        id = 99L,
        uuid = UUID.fromString("91e0c247-c611-4ed2-8db8-a495f1f16fee"),
        nombre = "Test",
        ranking = 99,
        fechaNacimiento = LocalDate.parse("1981-01-01"),
        añoProfesional = 2000,
        altura = 188,
        peso = 83,
        manoDominante = Tenista.ManoDominante.DERECHA,
        tipoReves = Tenista.TipoReves.UNA_MANO,
        puntos = 3789,
        pais = "Suiza",
        raquetaId = UUID.fromString("b0b5b2a1-5b1f-4b0f-8b1f-1b2c2b3c4d5e")
    )

    val tenistaDto = tenista.toDto(raqueta)

    val tenistaCreateDto = TenistaCreateDto(
        nombre = "Test",
        ranking = 99,
        fechaNacimiento = LocalDate.parse("1981-01-01").toString(),
        añoProfesional = 2000,
        altura = 188,
        peso = 83,
        manoDominante = Tenista.ManoDominante.DERECHA,
        tipoReves = Tenista.TipoReves.UNA_MANO,
        puntos = 3789,
        pais = "Suiza",
        raquetaId = UUID.fromString("b0b5b2a1-5b1f-4b0f-8b1f-1b2c2b3c4d5e")
    )

    @Test
    fun finAll() = runTest {
        coEvery { service.findAll() } returns flowOf(tenista)
        coEvery { service.findRaqueta(any()) } returns Ok(raqueta)

        val result = controller.finAll()
        val res = result.body!!

        Assertions.assertAll(
            { assertNotNull(result) },
            { assertNotNull(res) },
            { assertEquals(result.statusCode, HttpStatus.OK) },
            { assertEquals(1, res.size) },
            { assertEquals(tenistaDto.id, res[0].id) },
            { assertEquals(tenistaDto.nombre, res[0].nombre) },
            { assertEquals(tenistaDto.ranking, res[0].ranking) },
            { assertEquals(tenistaDto.raqueta?.id, res[0].raqueta?.id) },
        )

        coVerify { service.findAll() }
    }

    @Test
    fun findById() = runTest {
        coEvery { service.findByUuid(any()) } returns Ok(tenista)
        coEvery { service.findRaqueta(any()) } returns Ok(raqueta)

        val result = controller.findById(tenista.uuid)
        val res = result.body!!

        assertAll(
            { assertNotNull(result) },
            { assertNotNull(res) },
            { assertEquals(result.statusCode, HttpStatus.OK) },
            { assertEquals(tenistaDto.id, res.id) },
            { assertEquals(tenistaDto.nombre, res.nombre) },
            { assertEquals(tenistaDto.ranking, res.ranking) },
            { assertEquals(tenistaDto.raqueta?.id, res.raqueta?.id) },
        )

        coVerify { service.findByUuid(any()) }
    }

    @Test
    fun findByIdNotFound() = runTest {
        coEvery { service.findByUuid(any()) } returns Err(TenistaError.NotFound("No se ha encontrado tenista con id: ${raqueta.uuid}"))

        val res = assertThrows<ResponseStatusException> {
            val result = controller.findById(raqueta.uuid)
        }

        assertEquals(
            """404 NOT_FOUND "No se ha encontrado tenista con id: ${raqueta.uuid}"""",
            res.message
        )

        coVerify { service.findByUuid(any()) }

    }

    @Test
    fun create() = runTest {
        coEvery { service.save(any()) } returns Ok(tenista)
        coEvery { service.findRaqueta(any()) } returns Ok(raqueta)

        val result = controller.create(tenistaCreateDto)
        val res = result.body!!

        assertAll(
            { assertNotNull(result) },
            { assertNotNull(res) },
            { assertEquals(result.statusCode, HttpStatus.CREATED) },
            { assertEquals(tenistaDto.id, res.id) },
            { assertEquals(tenistaDto.nombre, res.nombre) },
            { assertEquals(tenistaDto.ranking, res.ranking) },
            { assertEquals(tenistaDto.raqueta?.id, res.raqueta?.id) },
        )

        coVerify { service.save(any()) }
    }

    @Test
    fun createTenistaRaquetaNotFound() = runTest {
        coEvery { service.save(any()) } returns Err(TenistaError.RaquetaNotFound("No se ha encontrado raqueta con id: ${raqueta.uuid}"))
        coEvery { service.findRaqueta(any()) } returns Err(TenistaError.RaquetaNotFound("No se ha encontrado raqueta con id: ${raqueta.uuid}"))

        val res = assertThrows<ResponseStatusException> {
            val result = controller.create(tenistaCreateDto)
        }

        assertEquals(
            """400 BAD_REQUEST "No se ha encontrado raqueta con id: ${raqueta.uuid}"""",
            res.message
        )

        coVerify(exactly = 1) { service.save(any()) }
    }

    @Test
    fun createCampoNombreBlanco() = runTest {
        coEvery { service.save(any()) } returns Err(TenistaError.BadRequest("El nombre no puede estar vacío"))
        coEvery { service.findRaqueta(any()) } returns Ok(raqueta)

        val res = assertThrows<ResponseStatusException> {
            val result = controller.create(tenistaCreateDto.copy(nombre = " "))
        }

        assertEquals(
            """400 BAD_REQUEST "El nombre no puede estar vacío"""",
            res.message
        )

        coVerify(exactly = 0) { service.save(any()) }
    }

    @Test
    fun createCampoRankingNegativo() = runTest {
        coEvery { service.save(any()) } returns Err(TenistaError.BadRequest("El ranking debe ser mayor que 0"))
        coEvery { service.findRaqueta(any()) } returns Ok(raqueta)

        val res = assertThrows<ResponseStatusException> {
            val result = controller.create(tenistaCreateDto.copy(ranking = -1))
        }

        assertEquals(
            """400 BAD_REQUEST "El ranking debe ser mayor que 0"""",
            res.message
        )

        coVerify(exactly = 0) { service.save(any()) }
    }

    // Y así con el resto de validaciones!!!


    @Test
    fun update() = runTest {
        coEvery { service.update(any(), any()) } returns Ok(tenista)
        coEvery { service.findRaqueta(any()) } returns Ok(raqueta)

        val result = controller.update(tenista.uuid, tenistaCreateDto)
        val res = result.body!!

        assertAll(
            { assertNotNull(result) },
            { assertNotNull(res) },
            { assertEquals(result.statusCode, HttpStatus.OK) },
            { assertEquals(tenistaDto.id, res.id) },
            { assertEquals(tenistaDto.nombre, res.nombre) },
            { assertEquals(tenistaDto.ranking, res.ranking) },
            { assertEquals(tenistaDto.raqueta?.id, res.raqueta?.id) },
        )

        coVerify { service.update(any(), any()) }
    }

    @Test
    fun updateTenistaNotFound() = runTest {
        coEvery {
            service.update(
                any(),
                any()
            )
        } returns Err(TenistaError.NotFound("No se ha encontrado tenista con id: ${tenista.uuid}"))
        coEvery { service.findRaqueta(any()) } returns Ok(raqueta)

        val res = assertThrows<ResponseStatusException> {
            val result = controller.update(tenista.uuid, tenistaCreateDto)
        }

        assertEquals(
            """404 NOT_FOUND "No se ha encontrado tenista con id: ${tenista.uuid}"""",
            res.message
        )

        coVerify { service.update(any(), any()) }
    }

    @Test
    fun delete() = runTest {
        coEvery { service.deleteByUuid(tenista.uuid) } returns Ok(tenista)

        val result = controller.delete(tenista.uuid)

        Assertions.assertAll(
            { assertNotNull(result) },
            { assertEquals(result.statusCode, HttpStatus.NO_CONTENT) },
        )

        coVerify(exactly = 1) { service.deleteByUuid(tenista.uuid) }
    }

    @Test
    fun deleteNotFound() = runTest {
        coEvery { service.findByUuid(any()) } returns Err(TenistaError.NotFound("No se ha encontrado tenista con id: ${tenista.uuid}"))
        coEvery { service.deleteByUuid(any()) } returns Err(TenistaError.NotFound("No se ha encontrado tenista con id: ${tenista.uuid}"))

        val res = assertThrows<ResponseStatusException> {
            val result = controller.delete(tenista.uuid)
        }

        assertEquals(
            """404 NOT_FOUND "No se ha encontrado tenista con id: ${tenista.uuid}"""",
            res.message
        )

        coVerify(exactly = 1) { service.deleteByUuid(any()) }
    }

    @Test
    fun findByName() = runTest {
        coEvery { service.findByNombre(any()) } returns flowOf(tenista)
        coEvery { service.findRaqueta(any()) } returns Ok(raqueta)

        val result = controller.findByName(tenista.nombre)
        val res = result.body!!

        Assertions.assertAll(
            { assertNotNull(result) },
            { assertNotNull(res) },
            { assertEquals(result.statusCode, HttpStatus.OK) },
            { assertEquals(1, res.size) },
            { assertEquals(tenistaDto.id, res[0].id) },
            { assertEquals(tenistaDto.nombre, res[0].nombre) },
            { assertEquals(tenistaDto.ranking, res[0].ranking) },
            { assertEquals(tenistaDto.raqueta?.id, res[0].raqueta?.id) },
        )

        coVerify { service.findByNombre(any()) }
    }

    @Test
    fun findByNameNotFound() = runTest {
        coEvery { service.findByNombre(any()) } returns flowOf()

        val result = controller.findByName(tenista.nombre)
        val res = result.body!!

        Assertions.assertAll(
            { assertNotNull(result) },
            { assertNotNull(res) },
            { assertEquals(result.statusCode, HttpStatus.OK) },
            { assertEquals(0, res.size) },
        )

        coVerify { service.findByNombre(any()) }
    }

    @Test
    fun findRaqueta() = runTest {
        coEvery { service.findRaqueta(any()) } returns Ok(raqueta)
        coEvery { service.findByUuid(any()) } returns Ok(tenista)

        val result = controller.findRaqueta(tenista.uuid)
        val res = result.body!!

        assertAll(
            { assertNotNull(result) },
            { assertNotNull(res) },
            { assertEquals(result.statusCode, HttpStatus.OK) },
            { assertEquals(raqueta.uuid, res.id) },
            { assertEquals(raqueta.marca, res.marca) },
            { assertEquals(raqueta.precio, res.precio) },
        )

        coVerify { service.findRaqueta(any()) }
    }

    @Test
    fun findByRanking() = runTest {
        coEvery { service.findByRanking(any()) } returns Ok(tenista)
        coEvery { service.findRaqueta(any()) } returns Ok(raqueta)

        val result = controller.findByRanking(tenista.ranking)
        val res = result.body!!

        assertAll(
            { assertNotNull(result) },
            { assertNotNull(res) },
            { assertEquals(result.statusCode, HttpStatus.OK) },
            { assertEquals(tenistaDto.id, res.id) },
            { assertEquals(tenistaDto.nombre, res.nombre) },
            { assertEquals(tenistaDto.ranking, res.ranking) },
            { assertEquals(tenistaDto.raqueta?.id, res.raqueta?.id) },
        )

        coVerify { service.findByRanking(any()) }
    }

    @Test
    fun findByRankingNotFound() = runTest {
        coEvery { service.findByRanking(any()) } returns Err(TenistaError.NotFound("No se ha encontrado tenista con ranking: ${tenista.ranking}"))

        val res = assertThrows<ResponseStatusException> {
            val result = controller.findByRanking(tenista.ranking)
        }

        assertEquals(
            """404 NOT_FOUND "No se ha encontrado tenista con ranking: ${tenista.ranking}"""",
            res.message
        )

        coVerify { service.findByRanking(any()) }
    }
}