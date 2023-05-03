package es.joseluisgs.tenistasrestspringboot.controllers

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import es.joseluisgs.tenistasrestspringboot.dto.RepresentanteRequestDto
import es.joseluisgs.tenistasrestspringboot.errors.RepresentanteError
import es.joseluisgs.tenistasrestspringboot.mappers.toDto
import es.joseluisgs.tenistasrestspringboot.models.Representante
import es.joseluisgs.tenistasrestspringboot.services.representantes.RepresentantesService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import joseluisgs.es.utils.toUUID
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

@ExtendWith(MockKExtension::class)
@SpringBootTest
class RepresentantesControllerTest {

    @MockK // @MockkBean
    private lateinit var service: RepresentantesService

    @InjectMockKs // @Autowired
    lateinit var controller: RepresentantesController

    final val representante = Representante(
        uuid = "71dac885-bfa5-465e-9bff-47a5902adae6".toUUID(),
        nombre = "TestRep",
        email = "test@mail.com"
    )

    val representanteDto = representante.toDto()

    val representanteRequestDto = RepresentanteRequestDto(
        nombre = "TestRep",
        email = "test@mail.com"
    )

    @Test
    fun finAll() = runTest {
        coEvery { service.findAll() } returns flowOf(representante)

        val result = controller.finAll()
        val res = result.body!!

        assertAll(
            { assertNotNull(result) },
            { assertNotNull(res) },
            { assertEquals(result.statusCode, HttpStatus.OK) },
            { assertEquals(1, res.size) },
            { assertEquals(representanteDto.id, res[0].id) },
            { assertEquals(representanteDto.nombre, res[0].nombre) },
            { assertEquals(representanteDto.email, res[0].email) },
        )

        coVerify(exactly = 1) { service.findAll() }
    }


    @Test
    fun findById() = runTest {
        coEvery { service.findByUuid(representante.uuid) } returns Ok(representante)

        val result = controller.findById(representante.uuid)
        val res = result.body!!

        assertAll(
            { assertNotNull(result) },
            { assertNotNull(res) },
            { assertEquals(result.statusCode, HttpStatus.OK) },
            { assertEquals(representanteDto.id, res.id) },
            { assertEquals(representanteDto.nombre, res.nombre) },
            { assertEquals(representanteDto.email, res.email) },
        )

        coVerify(exactly = 1) { service.findByUuid(representante.uuid) }
    }

    @Test
    fun findByIdNotFound() = runTest {
        coEvery { service.findByUuid(representante.uuid) } returns Err(
            RepresentanteError.NotFound("No se ha encontrado el representante con id: ${representante.uuid}")
        )

        val res = assertThrows<ResponseStatusException> {
            val result = controller.findById(representante.uuid)
        }

        assertEquals(
            """404 NOT_FOUND "No se ha encontrado el representante con id: ${representante.uuid}"""",
            res.message
        )

        coVerify(exactly = 1) { service.findByUuid(representante.uuid) }
    }

    @Test
    fun create() = runTest {
        coEvery { service.save(any()) } returns Ok(representante)

        val result = controller.create(representanteRequestDto)
        val res = result.body!!

        assertAll(
            { assertNotNull(result) },
            { assertNotNull(res) },
            { assertEquals(result.statusCode, HttpStatus.CREATED) },
            { assertEquals(representanteDto.id, res.id) },
            { assertEquals(representanteDto.nombre, res.nombre) },
            { assertEquals(representanteDto.email, res.email) },
        )

        coVerify(exactly = 1) { service.save(any()) }
    }

    @Test
    fun createCampoNombreBlanco() = runTest {
        coEvery { service.save(any()) } returns Err(RepresentanteError.BadRequest("El nombre no puede estar vacío"))

        val res = assertThrows<ResponseStatusException> {
            val result = controller.create(
                representanteRequestDto.copy(nombre = " ")
            )
        }

        assertEquals(
            """400 BAD_REQUEST "El nombre no puede estar vacío"""",
            res.message
        )

        coVerify(exactly = 0) { service.save(any()) }
    }

    @Test
    fun createCampoEmailBlanco() = runTest {
        coEvery { service.save(any()) } returns Err(RepresentanteError.BadRequest("El email no puede estar vacío y debe ser válido"))

        val res = assertThrows<ResponseStatusException> {
            val result = controller.create(
                representanteRequestDto.copy(email = "test")
            )
        }

        assertEquals(
            """400 BAD_REQUEST "El email no puede estar vacío y debe ser válido"""",
            res.message
        )

        coVerify(exactly = 0) { service.save(any()) }
    }

    @Test
    fun update() = runTest {
        coEvery { service.update(any(), any()) } returns Ok(representante)

        val result = controller.update(representante.uuid, representanteRequestDto)
        val res = result.body!!

        assertAll(
            { assertNotNull(result) },
            { assertNotNull(res) },
            { assertEquals(result.statusCode, HttpStatus.OK) },
            { assertEquals(representanteDto.id, res.id) },
            { assertEquals(representanteDto.nombre, res.nombre) },
            { assertEquals(representanteDto.email, res.email) },
        )

        coVerify(exactly = 1) { service.update(any(), any()) }
    }

    @Test
    fun updateNotFound() = runTest {
        coEvery {
            service.update(
                any(),
                any()
            )
        } returns Err(RepresentanteError.NotFound("No se ha encontrado el representante con id: ${representante.uuid}"))

        val res = assertThrows<ResponseStatusException> {
            val result = controller.update(representante.uuid, representanteRequestDto)
        }

        assertEquals(
            """404 NOT_FOUND "No se ha encontrado el representante con id: ${representante.uuid}"""",
            res.message
        )

        coVerify(exactly = 1) { service.update(any(), any()) }
    }

    @Test
    fun delete() = runTest {
        coEvery { service.deleteByUuid(representante.uuid) } returns Ok(representante)

        val result = controller.delete(representante.uuid)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(result.statusCode, HttpStatus.NO_CONTENT) },
        )

        coVerify(exactly = 1) { service.deleteByUuid(representante.uuid) }
    }

    @Test
    fun deleteNotFound() = runTest {
        coEvery { service.deleteByUuid(representante.uuid) } returns Err(RepresentanteError.NotFound("No se ha encontrado el representante con id: ${representante.uuid}"))

        val res = assertThrows<ResponseStatusException> {
            val result = controller.delete(representante.uuid)
        }

        assertEquals(
            """404 NOT_FOUND "No se ha encontrado el representante con id: ${representante.uuid}"""",
            res.message
        )
    }


    @Test
    fun findByName() = runTest {
        coEvery { service.findByNombre(any()) } returns flowOf(representante)

        val result = controller.findByName(representante.nombre)
        val res = result.body!!

        assertAll(
            { assertNotNull(result) },
            { assertNotNull(res) },
            { assertEquals(result.statusCode, HttpStatus.OK) },
            { assertEquals(representanteDto.id, res[0].id) },
            { assertEquals(representanteDto.nombre, res[0].nombre) },
            { assertEquals(representanteDto.email, res[0].email) },
        )

        coVerify { service.findByNombre(any()) }
    }

    @Test
    fun findByNameNotFound() = runTest {
        coEvery { service.findByNombre(any()) } returns flowOf()

        val result = controller.findByName(representante.nombre)
        val res = result.body!!

        assertAll(
            { assertNotNull(result) },
            { assertNotNull(res) },
            { assertEquals(result.statusCode, HttpStatus.OK) },
            { assertEquals(0, res.size) },
        )

        coVerify { service.findByNombre(any()) }
    }

}