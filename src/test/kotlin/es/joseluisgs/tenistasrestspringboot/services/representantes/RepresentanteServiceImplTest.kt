package es.joseluisgs.tenistasrestspringboot.services.representantes

import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import es.joseluisgs.tenistasrestspringboot.config.websocket.ServerWebSocketConfig
import es.joseluisgs.tenistasrestspringboot.errors.RepresentanteError
import es.joseluisgs.tenistasrestspringboot.exceptions.RepresentanteConflictIntegrityException
import es.joseluisgs.tenistasrestspringboot.models.Representante
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
class RepresentanteServiceImplTest {

    private val representante = Representante(
        id = 99L,
        uuid = UUID.fromString("91e0c247-c611-4ed2-8db8-a495f1f16fee"),
        nombre = "Test",
        email = "test@example.com",
    )

    @MockK
    lateinit var repository: RepresentantesCachedRepository

    @SpyK
    var webSocketConfig: ServerWebSocketConfig = ServerWebSocketConfig()

    @InjectMockKs
    lateinit var service: RepresentanteServiceImpl

    init {
        MockKAnnotations.init(this)
    }

    @Test
    fun findAll() = runTest {
        // Usamos coEvery para poder usar corutinas
        coEvery { repository.findAll() } returns flowOf(representante)

        // Llamamos al método
        val result = service.findAll().toList()

        assertAll(
            { assertEquals(1, result.size) },
            { assertEquals(representante, result[0]) }
        )

        coVerify(exactly = 1) { repository.findAll() }
    }

    @Test
    fun findById() = runTest {
        coEvery { repository.findById(any()) } returns representante

        val result = service.findById(representante.id!!).get()!!

        assertAll(
            { assertEquals(representante.nombre, result.nombre) },
            { assertEquals(representante.email, result.email) },
        )

        coVerify { repository.findById(any()) }
    }

    @Test
    fun findByIdNotFound() = runTest {
        coEvery { repository.findById(any()) } returns null

        val res = service.findById(representante.id!!).getError()!!

        assertAll(
            { assertTrue(res is RepresentanteError.NotFound) },
            { assertTrue(res.message.contains("No se ha encontrado el representante con id")) }
        )

        coVerify { repository.findById(any()) }

    }

    @Test
    fun findByUuid() = runTest {
        coEvery { repository.findByUuid(any()) } returns representante

        val result = service.findByUuid(representante.uuid).get()!!

        assertAll(
            { assertEquals(representante.nombre, result.nombre) },
            { assertEquals(representante.email, result.email) },
        )

        coVerify { repository.findByUuid(any()) }
    }

    @Test
    fun findByUuidNotFound() = runTest {
        coEvery { repository.findByUuid(any()) } returns null

        val res = service.findByUuid(representante.uuid).getError()!!

        assertAll(
            { assertTrue(res is RepresentanteError.NotFound) },
            { assertTrue(res.message.contains("No se ha encontrado el representante con uuid")) }
        )

        coVerify { repository.findByUuid(any()) }
    }


    @Test
    fun findByNombre() = runTest {
        coEvery { repository.findByNombre(any()) } returns flowOf(representante)

        val result = service.findByNombre(representante.nombre).toList()

        assertAll(
            { assertEquals(1, result.size) },
            { assertEquals(representante, result[0]) }
        )

        coVerify { repository.findByNombre(any()) }
    }

    @Test
    fun save() = runTest {
        coEvery { repository.save(any()) } returns representante

        val result = service.save(representante).get()!!

        assertAll(
            { assertEquals(representante.nombre, result.nombre) },
            { assertEquals(representante.email, result.email) },
        )

        coVerify { repository.save(any()) }

    }

    @Test
    fun update() = runTest {
        coEvery { repository.findByUuid(any()) } returns representante
        coEvery { repository.update(any(), any()) } returns representante

        val result = service.update(representante.uuid, representante).get()!!

        assertAll(
            { assertEquals(representante.nombre, result.nombre) },
            { assertEquals(representante.email, result.email) },
        )

        coVerify { repository.update(any(), any()) }
    }

    @Test
    fun updateNotFound() = runTest {
        coEvery { repository.findByUuid(any()) } returns null
        coEvery { repository.update(any(), any()) } returns null

        val res = service.update(representante.uuid, representante).getError()!!

        assertAll(
            { assertTrue(res is RepresentanteError.NotFound) },
            { assertTrue(res.message.contains("No se ha encontrado el representante con uuid")) }
        )

        coVerify(exactly = 0) { repository.update(any(), any()) } // No se llama al método update

    }

    @Test
    fun delete() = runTest {
        coEvery { repository.findByUuid(any()) } returns representante
        coEvery { repository.delete(any()) } returns representante

        val result = service.delete(representante).get()!!

        assertAll(
            { assertEquals(representante.nombre, result.nombre) },
            { assertEquals(representante.email, result.email) },
        )

        coVerify { repository.delete(any()) }

    }

    @Test
    fun deleteNotRepresentanteConflict() = runTest {
        val uuid = "86084458-4733-4d71-a3db-34b50cd8d68f".toUUID()
        coEvery { repository.findByUuid(any()) } returns representante
        coEvery { repository.delete(any()) } throws RepresentanteConflictIntegrityException("No se puede borrar el representante con id: $uuid porque tiene raquetas asociadas")

        val res = assertThrows<RepresentanteConflictIntegrityException> {
            service.delete(representante)
        }

        assertEquals("No se puede borrar el representante con id: $uuid porque tiene raquetas asociadas", res.message)

        coVerify { repository.delete(any()) }
    }

    @Test
    fun deleteByUuid() = runTest {
        coEvery { repository.findByUuid(any()) } returns representante
        coEvery { repository.deleteByUuid(any()) } returns representante

        val result = service.deleteByUuid(representante.uuid).get()!!

        assertAll(
            { assertEquals(representante.nombre, result.nombre) },
            { assertEquals(representante.email, result.email) },
        )

        coVerify { repository.deleteByUuid(any()) }

    }

    @Test
    fun deleteUuidNotFound() = runTest {
        coEvery { repository.findByUuid(any()) } returns null
        coEvery { repository.deleteByUuid(any()) } returns representante

        val res = service.deleteByUuid(representante.uuid).getError()!!

        assertAll(
            { assertTrue(res is RepresentanteError.NotFound) },
            { assertTrue(res.message.contains("No se ha encontrado el representante con uuid")) }
        )
        
        coVerify(exactly = 0) { repository.deleteByUuid(any()) } // No se llama al método deleteByUuid

    }

    @Test
    fun findAllPage() = runTest {
        coEvery { repository.findAllPage(any()) } returns flowOf(PageImpl(listOf(representante)))

        val result = service.findAllPage(PageRequest.of(0, 10)).first()

        assertAll(
            { assertEquals(1, result.size) },
            { assertEquals(representante, result.content[0]) },
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

}