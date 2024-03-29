package es.joseluisgs.tenistasrestspringboot.services.raquetas

import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import es.joseluisgs.tenistasrestspringboot.config.websocket.ServerWebSocketConfig
import es.joseluisgs.tenistasrestspringboot.errors.RaquetaError
import es.joseluisgs.tenistasrestspringboot.exceptions.RaquetaConflictIntegrityException
import es.joseluisgs.tenistasrestspringboot.models.Raqueta
import es.joseluisgs.tenistasrestspringboot.models.Representante
import es.joseluisgs.tenistasrestspringboot.repositories.raquetas.RaquetasCachedRepository
import es.joseluisgs.tenistasrestspringboot.repositories.representantes.RepresentantesCachedRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import joseluisgs.es.utils.toUUID
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.util.*

@ExtendWith(MockKExtension::class)
@SpringBootTest
class RaquetasServiceImplTest {

    private val raqueta = Raqueta(
        id = 99L,
        // usa un UUID para que no de problemas
        uuid = UUID.fromString("431ad089-d422-4aa4-b9a5-35a620eae8d4"),
        marca = "Test",
        precio = 199.9,
        representanteId = UUID.fromString("b39a2fd2-f7d7-405d-b73c-b68a8dedbcdf"),
    )

    val representante = Representante(
        id = 99L,
        uuid = UUID.fromString("b39a2fd2-f7d7-405d-b73c-b68a8dedbcdf"),
        nombre = "Pepe Perez",
        email = "pepe@perez.com"
    )


    @MockK
    lateinit var repository: RaquetasCachedRepository

    @MockK
    lateinit var represetantesRepository: RepresentantesCachedRepository

    @SpyK
    var webSocketConfig: ServerWebSocketConfig = ServerWebSocketConfig()

    @InjectMockKs
    lateinit var service: RaquetasServiceImpl

    init {
        MockKAnnotations.init(this)
    }

    @Test
    fun findAll() = runTest {
        // Usamos coEvery para poder usar corutinas
        coEvery { repository.findAll() } returns flowOf(raqueta)

        // Llamamos al método
        val result = service.findAll().toList()

        assertAll(
            { assertEquals(1, result.size) },
            { assertEquals(raqueta, result[0]) }
        )

        coVerify(exactly = 1) { repository.findAll() }
    }

    @Test
    fun findById() = runTest {
        coEvery { repository.findById(any()) } returns raqueta

        val result = service.findById(raqueta.id!!).get()!!

        assertAll(
            { assertEquals(raqueta.marca, result.marca) },
            { assertEquals(raqueta.precio, result.precio) },
        )

        coVerify { repository.findById(any()) }
    }

    @Test
    fun findByIdNotFound() = runTest {
        coEvery { repository.findById(any()) } returns null

        val res = service.findById(raqueta.id!!).getError()!!

        assertAll(
            { assertTrue(res is RaquetaError.NotFound) },
            { assertTrue(res.message.contains("No se ha encontrado la raqueta con id")) }
        )


        coVerify { repository.findById(any()) }

    }

    @Test
    fun findByUuid() = runTest {
        coEvery { repository.findByUuid(any()) } returns raqueta

        val result = service.findByUuid(raqueta.uuid).get()!!

        assertAll(
            { assertEquals(raqueta.marca, result.marca) },
            { assertEquals(raqueta.precio, result.precio) },
        )

        coVerify { repository.findByUuid(any()) }
    }

    @Test
    fun findByUuidNotFound() = runTest {
        coEvery { repository.findByUuid(any()) } returns null

        val res = service.findByUuid(raqueta.uuid).getError()!!

        assertAll(
            { assertTrue(res is RaquetaError.NotFound) },
            { assertTrue(res.message.contains("No se ha encontrado la raqueta con uuid")) }
        )

        coVerify { repository.findByUuid(any()) }
    }

    @Test
    fun findByMarca() = runTest {
        coEvery { repository.findByMarca(any()) } returns flowOf(raqueta)

        val result = service.findByMarca(raqueta.marca).toList()

        assertAll(
            { assertEquals(1, result.size) },
            { assertEquals(raqueta, result[0]) }
        )

        coVerify { repository.findByMarca(any()) }
    }

    @Test
    fun save() = runTest {
        coEvery { represetantesRepository.findByUuid(any()) } returns representante
        coEvery { repository.save(any()) } returns raqueta

        val result = service.save(raqueta).get()!!

        assertAll(
            { assertEquals(raqueta.marca, result.marca) },
            { assertEquals(raqueta.precio, result.precio) },
        )

        coVerify { repository.save(any()) }

    }

    @Test
    fun saveRepresentanteNotExists() = runTest {
        coEvery { represetantesRepository.findByUuid(any()) } returns null
        coEvery { repository.save(any()) } returns raqueta

        val res = service.save(raqueta).getError()!!

        assertAll(
            { assertTrue(res is RaquetaError.RepresentanteNotFound) },
            { assertTrue(res.message.contains("No se ha encontrado el representante con id")) }
        )

        assertEquals("No se ha encontrado el representante con id: ${raqueta.representanteId}", res.message)

    }

    @Test
    fun update() = runTest {
        coEvery { represetantesRepository.findByUuid(any()) } returns representante
        coEvery { repository.findByUuid(any()) } returns raqueta
        coEvery { repository.update(any(), any()) } returns raqueta

        val result = service.update(raqueta.uuid, raqueta).get()!!
        assertAll(
            { assertEquals(raqueta.marca, result.marca) },
            { assertEquals(raqueta.precio, result.precio) },
        )

        coVerify { repository.update(any(), any()) }
    }

    @Test
    fun updateNotFound() = runTest {
        coEvery { repository.findByUuid(any()) } returns null
        coEvery { repository.update(any(), any()) } returns null
        coEvery { represetantesRepository.findByUuid(any()) } returns representante

        val res = service.update(raqueta.uuid, raqueta).getError()!!

        assertAll(
            { assertTrue(res is RaquetaError.NotFound) },
            { assertTrue(res.message.contains("No se ha encontrado la raqueta con uuid")) }
        )

        coVerify(exactly = 0) { repository.update(any(), any()) } // No se llama al método update

    }

    @Test
    fun delete() = runTest {
        coEvery { repository.findByUuid(any()) } returns raqueta
        coEvery { repository.delete(any()) } returns raqueta
        coEvery { represetantesRepository.findByUuid(any()) } returns representante

        val result = service.delete(raqueta).get()!!

        assertAll(
            { assertEquals(raqueta.marca, result.marca) },
            { assertEquals(raqueta.precio, result.precio) },
        )

        coVerify { repository.delete(any()) }

    }

    @Test
    fun deleteNotRaquetaConflict() = runTest {
        val uuid = "86084458-4733-4d71-a3db-34b50cd8d68f".toUUID()
        coEvery { repository.findByUuid(any()) } returns raqueta
        coEvery { repository.delete(any()) } throws RaquetaConflictIntegrityException("No se puede borrar la raqueta ya que tiene tenistas asociados")

        val res = assertThrows<RaquetaConflictIntegrityException> {
            service.delete(raqueta)
        }

        assertEquals("No se puede borrar la raqueta ya que tiene tenistas asociados", res.message)

        coVerify { repository.delete(any()) }
    }

    @Test
    fun deleteByUuid() = runTest {
        coEvery { repository.findByUuid(any()) } returns raqueta
        coEvery { repository.deleteByUuid(any()) } returns raqueta
        coEvery { represetantesRepository.findByUuid(any()) } returns representante

        val result = service.deleteByUuid(raqueta.uuid).get()!!

        assertAll(
            { assertEquals(raqueta.marca, result.marca) },
            { assertEquals(raqueta.precio, result.precio) },
        )

        coVerify { repository.deleteByUuid(any()) }

    }

    @Test
    fun deleteUuidNotFound() = runTest {
        coEvery { repository.findByUuid(any()) } returns null

        val res = service.deleteByUuid(raqueta.uuid).getError()!!

        assertAll(
            { assertTrue(res is RaquetaError.NotFound) },
            { assertTrue(res.message.contains("No se ha encontrado la raqueta con uuid")) }
        )

        coVerify(exactly = 0) { repository.deleteByUuid(any()) } // No se llama al método deleteByUuid

    }

    @Test
    fun findAllPage() = runTest {
        coEvery { repository.findAllPage(any()) } returns flowOf(PageImpl(listOf(raqueta)))

        val result = service.findAllPage(PageRequest.of(0, 10)).first()

        assertAll(
            { assertEquals(1, result.size) },
            { assertEquals(raqueta, result.content[0]) },
        )

        coVerify { repository.findAllPage(any()) }

    }

    @Test
    fun countAll() = runTest {
        coEvery { repository.countAll() } returns 1

        val result = service.countAll()

        assertEquals(1, result)

        coVerify { repository.countAll() }

    }

    @Test
    fun findRepresentante() = runTest {
        coEvery { represetantesRepository.findByUuid(any()) } returns representante

        val result = service.findRepresentante(raqueta.representanteId).get()!!

        assertEquals(representante, result)

        coVerify { represetantesRepository.findByUuid(any()) }

    }

    @Test
    fun findRepresentanteNotFound() = runTest {
        coEvery { represetantesRepository.findByUuid(any()) } returns null

        val res = service.findRepresentante(raqueta.representanteId).getError()!!

        assertAll(
            { assertTrue(res is RaquetaError.RepresentanteNotFound) },
            { assertTrue(res.message.contains("No se ha encontrado el representante con id")) }
        )
    }
}