package es.joseluisgs.tenistasrestspringboot.controllers

import es.joseluisgs.tenistasrestspringboot.dto.RaquetaCreateDto
import es.joseluisgs.tenistasrestspringboot.dto.RaquetaDto
import es.joseluisgs.tenistasrestspringboot.dto.RepresentanteDto
import es.joseluisgs.tenistasrestspringboot.exceptions.RaquetaNotFoundException
import es.joseluisgs.tenistasrestspringboot.models.Raqueta
import es.joseluisgs.tenistasrestspringboot.models.Representante
import es.joseluisgs.tenistasrestspringboot.services.raquetas.RaquetasService
import io.mockk.MockKAnnotations
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

@ExtendWith(MockKExtension::class)
@SpringBootTest
class RaquetasControllerTest {

    @MockK // @MockkBean
    private lateinit var service: RaquetasService

    @InjectMockKs // @Autowired
    lateinit var controller: RaquetasController

    val raqueta = Raqueta(
        uuid = "a765df90-e4aa-4306-b93e-20500accf8f7".toUUID(),
        marca = "Test",
        precio = 99.99,
        represetanteId = "71dac885-bfa5-465e-9bff-47a5902adae6".toUUID()
    )

    val representante = Representante(
        uuid = "71dac885-bfa5-465e-9bff-47a5902adae6".toUUID(),
        nombre = "TestRep",
        email = "test@mail.com"
    )

    val raquetaDto = RaquetaDto(
        id = "a765df90-e4aa-4306-b93e-20500accf8f7".toUUID(),
        marca = "Test",
        precio = 99.99,
        represetante = RepresentanteDto(
            id = "71dac885-bfa5-465e-9bff-47a5902adae6".toUUID(),
            nombre = "TestRep",
            email = "test@mail.com"
        )
    )


    init {
        MockKAnnotations.init(this)
    }

    @Test
    fun finAll() = runTest {
        coEvery { service.findAll() } returns flowOf(raqueta)
        coEvery { service.findRepresentante(any()) } returns representante

        val result = controller.finAll()
        val res = result.body!!

        assertAll(
            { assertNotNull(result) },
            { assertNotNull(res) },
            { assertEquals(result.statusCode, HttpStatus.OK) },
            { assertEquals(1, res.size) },
            { assertEquals(raquetaDto.id, res[0].id) },
            { assertEquals(raquetaDto.marca, res[0].marca) },
            { assertEquals(raquetaDto.precio, res[0].precio) },
            { assertEquals(raquetaDto.represetante.id, res[0].represetante.id) },
            { assertEquals(raquetaDto.represetante.nombre, res[0].represetante.nombre) },
            { assertEquals(raquetaDto.represetante.email, res[0].represetante.email) },
        )

        coVerify { service.findAll() }
    }

    @Test
    fun findById() = runTest {
        coEvery { service.findByUuid(any()) } returns raqueta
        coEvery { service.findRepresentante(any()) } returns representante

        val result = controller.findById(raqueta.uuid)
        val res = result.body!!

        assertAll(
            { assertNotNull(result) },
            { assertNotNull(res) },
            { assertEquals(result.statusCode, HttpStatus.OK) },
            { assertEquals(raquetaDto.id, res.id) },
            { assertEquals(raquetaDto.marca, res.marca) },
            { assertEquals(raquetaDto.precio, res.precio) },
            { assertEquals(raquetaDto.represetante.id, res.represetante.id) },
            { assertEquals(raquetaDto.represetante.nombre, res.represetante.nombre) },
            { assertEquals(raquetaDto.represetante.email, res.represetante.email) },
        )

        coVerify { service.findByUuid(any()) }
    }

    @Test
    fun findByIdNotFound() = runTest {
        coEvery { service.findByUuid(any()) } throws RaquetaNotFoundException("No se ha encontrado la raqueta con id: ${raqueta.uuid}")

        val res = assertThrows<RaquetaNotFoundException> {
            controller.findById(raqueta.uuid)
        }

        assertEquals("No se ha encontrado la raqueta con id: ${raqueta.uuid}", res.message)

        coVerify { service.findByUuid(any()) }

    }

    @Test
    fun create() = runTest {
        coEvery { service.save(any()) } returns raqueta
        coEvery { service.findRepresentante(any()) } returns representante

        val result = controller.create(
            RaquetaCreateDto(
                marca = "Test",
                precio = 99.99,
                representanteId = "71dac885-bfa5-465e-9bff-47a5902adae6".toUUID()
            )
        )

        val res = result.body!!
        assertAll(
            { assertNotNull(result) },
            { assertNotNull(res) },
            { assertEquals(result.statusCode, HttpStatus.CREATED) },
            { assertEquals(raquetaDto.id, res.id) },
            { assertEquals(raquetaDto.marca, res.marca) },
            { assertEquals(raquetaDto.precio, res.precio) },
            { assertEquals(raquetaDto.represetante.id, res.represetante.id) },
            { assertEquals(raquetaDto.represetante.nombre, res.represetante.nombre) },
            { assertEquals(raquetaDto.represetante.email, res.represetante.email) },
        )
    }

    // Deberíamos probar los bad request!!

    @Test
    fun update() = runTest {
        coEvery { service.findByUuid(any()) } returns raqueta
        coEvery { service.update(any(), any()) } returns raqueta
        coEvery { service.findRepresentante(any()) } returns representante

        val result = controller.update(
            raqueta.uuid,
            RaquetaCreateDto(
                marca = "Test",
                precio = 99.99,
                representanteId = "71dac885-bfa5-465e-9bff-47a5902adae6".toUUID()
            )
        )

        val res = result.body!!
        assertAll(
            { assertNotNull(result) },
            { assertNotNull(res) },
            { assertEquals(result.statusCode, HttpStatus.OK) },
            { assertEquals(raquetaDto.id, res.id) },
            { assertEquals(raquetaDto.marca, res.marca) },
            { assertEquals(raquetaDto.precio, res.precio) },
            { assertEquals(raquetaDto.represetante.id, res.represetante.id) },
            { assertEquals(raquetaDto.represetante.nombre, res.represetante.nombre) },
            { assertEquals(raquetaDto.represetante.email, res.represetante.email) },
        )
    }

    @Test
    fun delete() = runTest {
        coEvery { service.findByUuid(any()) } returns raqueta
        coEvery { service.deleteByUuid(any()) } returns raqueta

        val result = controller.delete(raqueta.uuid)

        assertAll(
            { assertNotNull(result) },
            { assertEquals(result.statusCode, HttpStatus.NO_CONTENT) },
        )
    }

    @Test
    fun findByName() = runTest {
        coEvery { service.findByMarca(any()) } returns flowOf(raqueta)
        coEvery { service.findRepresentante(any()) } returns representante

        val result = controller.findByName(raqueta.marca)
        val res = result.body!!

        assertAll(
            { assertNotNull(result) },
            { assertNotNull(res) },
            { assertEquals(result.statusCode, HttpStatus.OK) },
            { assertEquals(raquetaDto.id, res[0].id) },
            { assertEquals(raquetaDto.marca, res[0].marca) },
            { assertEquals(raquetaDto.precio, res[0].precio) },
            { assertEquals(raquetaDto.represetante.id, res[0].represetante.id) },
            { assertEquals(raquetaDto.represetante.nombre, res[0].represetante.nombre) },
            { assertEquals(raquetaDto.represetante.email, res[0].represetante.email) },
        )

        coVerify { service.findByMarca(any()) }
    }

    @Test
    fun findRepresentante() = runTest {
        coEvery { service.findByUuid(any()) } returns raqueta
        coEvery { service.findRepresentante(any()) } returns representante

        val result = controller.findRepresentante(raqueta.uuid)
        val res = result.body!!

        assertAll(
            { assertNotNull(result) },
            { assertNotNull(res) },
            { assertEquals(result.statusCode, HttpStatus.OK) },
            { assertEquals(raquetaDto.represetante.id, res.id) },
            { assertEquals(raquetaDto.represetante.nombre, res.nombre) },
            { assertEquals(raquetaDto.represetante.email, res.email) },
        )

        coVerify { service.findRepresentante(any()) }
    }
}