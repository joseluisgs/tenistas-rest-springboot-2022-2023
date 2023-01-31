package es.joseluisgs.tenistasrestspringboot.controllers

import es.joseluisgs.tenistasrestspringboot.dto.RaquetaCreateDto
import es.joseluisgs.tenistasrestspringboot.dto.RaquetaDto
import es.joseluisgs.tenistasrestspringboot.dto.RepresentanteDto
import es.joseluisgs.tenistasrestspringboot.exceptions.RaquetaBadRequestException
import es.joseluisgs.tenistasrestspringboot.exceptions.RaquetaNotFoundException
import es.joseluisgs.tenistasrestspringboot.exceptions.RepresentanteNotFoundException
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
import org.springframework.web.server.ResponseStatusException

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
        representanteId = "71dac885-bfa5-465e-9bff-47a5902adae6".toUUID()
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
        representante = RepresentanteDto(
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
            { assertEquals(raquetaDto.representante.id, res[0].representante.id) },
            { assertEquals(raquetaDto.representante.nombre, res[0].representante.nombre) },
            { assertEquals(raquetaDto.representante.email, res[0].representante.email) },
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
            { assertEquals(raquetaDto.representante.id, res.representante.id) },
            { assertEquals(raquetaDto.representante.nombre, res.representante.nombre) },
            { assertEquals(raquetaDto.representante.email, res.representante.email) },
        )

        coVerify { service.findByUuid(any()) }
    }

    @Test
    fun findByIdNotFound() = runTest {
        coEvery { service.findByUuid(any()) } throws RaquetaNotFoundException("No se ha encontrado la raqueta con id: ${raqueta.uuid}")

        val res = assertThrows<ResponseStatusException> {
            val result = controller.findById(raqueta.uuid)
        }

        assertEquals(
            """404 NOT_FOUND "No se ha encontrado la raqueta con id: ${raqueta.uuid}"""",
            res.message
        )
        
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
            { assertEquals(raquetaDto.representante.id, res.representante.id) },
            { assertEquals(raquetaDto.representante.nombre, res.representante.nombre) },
            { assertEquals(raquetaDto.representante.email, res.representante.email) },
        )
    }

    @Test
    fun createRepresentanteNotFound() = runTest {
        coEvery { service.save(any()) } throws RepresentanteNotFoundException("")
        coEvery { service.findRepresentante(any()) } throws RepresentanteNotFoundException("No se ha encontrado el representante con id: ${raqueta.representanteId}")

        val res = assertThrows<ResponseStatusException> {
            val result = controller.create(
                RaquetaCreateDto(
                    marca = "Test",
                    precio = 99.99,
                    representanteId = "71dac885-bfa5-465e-9bff-47a5902adae6".toUUID()
                )
            )
        }

        assertEquals(
            """400 BAD_REQUEST """"",
            res.message
        )

        coVerify(exactly = 1) { service.save(any()) }
    }

    @Test
    fun createCampoNombreBlanco() = runTest {
        coEvery { service.save(any()) } throws RaquetaBadRequestException("")
        coEvery { service.findRepresentante(any()) } returns representante

        val res = assertThrows<ResponseStatusException> {
            val result = controller.create(
                RaquetaCreateDto(
                    marca = "",
                    precio = 99.99,
                    representanteId = "71dac885-bfa5-465e-9bff-47a5902adae6".toUUID()
                )
            )
        }

        assertEquals(
            """400 BAD_REQUEST "La marca no puede estar vacía"""",
            res.message
        )

        coVerify(exactly = 0) { service.save(any()) }
    }

    @Test
    fun createCampoPrecioNegativo() = runTest {
        coEvery { service.save(any()) } throws RaquetaBadRequestException("")
        coEvery { service.findRepresentante(any()) } returns representante

        val res = assertThrows<ResponseStatusException> {
            val result = controller.create(
                RaquetaCreateDto(
                    marca = "Test",
                    precio = -99.99,
                    representanteId = "71dac885-bfa5-465e-9bff-47a5902adae6".toUUID()
                )
            )
        }

        assertEquals(
            """400 BAD_REQUEST "El precio no puede ser negativo"""",
            res.message
        )

        coVerify(exactly = 0) { service.save(any()) }
    }


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
            { assertEquals(raquetaDto.representante.id, res.representante.id) },
            { assertEquals(raquetaDto.representante.nombre, res.representante.nombre) },
            { assertEquals(raquetaDto.representante.email, res.representante.email) },
        )
    }

    @Test
    fun updateNotFound() = runTest {
        coEvery { service.findByUuid(any()) } throws RaquetaNotFoundException("")
        coEvery {
            service.update(
                any(),
                any()
            )
        } throws RaquetaNotFoundException("No se ha encontrado la raqueta con id: ${raqueta.uuid}")

        val res = assertThrows<ResponseStatusException> {
            val result = controller.update(
                raqueta.uuid,
                RaquetaCreateDto(
                    marca = "Test",
                    precio = 99.99,
                    representanteId = "71dac885-bfa5-465e-9bff-47a5902adae6".toUUID()
                )
            )
        }

        assertEquals(
            """404 NOT_FOUND "No se ha encontrado la raqueta con id: ${raqueta.uuid}"""",
            res.message
        )

        coVerify(exactly = 1) { service.update(any(), any()) }
    }

    // Los campos y las validaciones son iguales en el update, por lo que están testeados en el create

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
    fun deleteNotFound() = runTest {
        coEvery { service.findByUuid(any()) } throws RaquetaNotFoundException("")
        coEvery { service.deleteByUuid(any()) } throws RaquetaNotFoundException("No se ha encontrado la raqueta con id: ${raqueta.uuid}")

        val res = assertThrows<ResponseStatusException> {
            val result = controller.delete(raqueta.uuid)
        }

        assertEquals(
            """404 NOT_FOUND "No se ha encontrado la raqueta con id: ${raqueta.uuid}"""",
            res.message
        )

        coVerify(exactly = 1) { service.deleteByUuid(any()) }
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
            { assertEquals(raquetaDto.representante.id, res[0].representante.id) },
            { assertEquals(raquetaDto.representante.nombre, res[0].representante.nombre) },
            { assertEquals(raquetaDto.representante.email, res[0].representante.email) },
        )

        coVerify { service.findByMarca(any()) }
    }

    @Test
    fun findByNameNotFound() = runTest {
        coEvery { service.findByMarca(any()) } returns flowOf()

        val result = controller.findByName(raqueta.marca)
        val res = result.body!!

        assertAll(
            { assertNotNull(result) },
            { assertNotNull(res) },
            { assertEquals(result.statusCode, HttpStatus.OK) },
            { assertEquals(0, res.size) },
        )

        coVerify { service.findByMarca(any()) }
    }

    @Test
    fun findRepresentante() = runTest {
        coEvery { service.findByUuid(any()) } returns raqueta
        coEvery { service.findRepresentante(any()) } returns representante

        val result = controller.findRepresentante(raqueta.representanteId)
        val res = result.body!!

        assertAll(
            { assertNotNull(result) },
            { assertNotNull(res) },
            { assertEquals(result.statusCode, HttpStatus.OK) },
            { assertEquals(raquetaDto.representante.id, res.id) },
            { assertEquals(raquetaDto.representante.nombre, res.nombre) },
            { assertEquals(raquetaDto.representante.email, res.email) },
        )

        coVerify { service.findRepresentante(any()) }
    }
}