package es.joseluisgs.tenistasrestspringboot.repositories.representantes

import es.joseluisgs.tenistasrestspringboot.models.Representante
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import java.util.*

// Voy a usar MocKK en lugar de Mockito
// https://spring.io/guides/tutorials/spring-boot-kotlin/
// @ExtendWith(SpringExtension::class)
@ExtendWith(MockKExtension::class)
@SpringBootTest
class RepresentantesCachedRepositoryImplTest {

    private val representante = Representante(
        id = 99L,
        uuid = UUID.fromString("91e0c247-c611-4ed2-8db8-a495f1f16fee"),
        nombre = "Test",
        email = "test@example.com",
    )

    @MockK // @MockkBean
    lateinit var repo: RepresentantesRepository

    @InjectMockKs // @Autowired
    lateinit var repository: RepresentatesCachedRepositoryImpl


    init {
        MockKAnnotations.init(this)
    }

    @Test
    fun findAllPageable() = runTest {
        // Usamos coEvery para poder usar corutinas
        coEvery { repo.findAll() } returns flowOf(representante)

        // Llamamos al método
        val result = repository.findAll().toList()

        assertAll(
            { assertEquals(1, result.size) },
            { assertEquals(representante, result[0]) }
        )

        coVerify(exactly = 1) { repo.findAll() }
    }

    @Test
    fun findById() = runTest {
        // Usamos coEvery para poder usar corutinas
        coEvery { repo.findById(any()) } returns representante

        // Llamamos al método
        val result = repository.findById(1L)!!

        assertAll(
            { assertEquals(representante.nombre, result.nombre) },
            { assertEquals(representante.email, result.email) },
        )


        coVerify { repo.findById(any()) }
    }

    @Test
    fun findByIdNotFound() = runTest {
        // Usamos coEvery para poder usar corutinas
        coEvery { repo.findById(any()) } returns null

        // Llamamos al método
        val result = repository.findById(1L)

        assertNull(result)

        coVerify { repo.findById(any()) }
    }


    @Test
    fun findByUuid() = runTest {
        // Usamos coEvery para poder usar corutinas
        coEvery { repo.findByUuid(any()) } returns flowOf(representante)

        // Llamamos al método
        val result = repository.findByUuid(representante.uuid)!!

        assertAll(
            { assertEquals(representante.nombre, result.nombre) },
            { assertEquals(representante.email, result.email) },
        )
    }

    @Test
    fun findByUuidNotFound() = runTest {
        // Usamos coEvery para poder usar corutinas
        coEvery { repo.findByUuid(any()) } returns flowOf()

        // Llamamos al método
        val result = repository.findByUuid(representante.uuid)

        assertNull(result)
    }

    @Test
    fun findByMarca() = runTest {
        // Usamos coEvery para poder usar corutinas
        coEvery { repo.findByNombreContainsIgnoreCase(any()) } returns flowOf(representante)

        // Llamamos al método
        val result = repository.findByNombre("Test").toList()

        assertAll(
            { assertEquals(1, result.size) },
            { assertEquals(representante, result[0]) }
        )

        coVerify { repo.findByNombreContainsIgnoreCase(any()) }
    }

    @Test
    fun findByMarcaNotFound() = runTest {
        // Usamos coEvery para poder usar corutinas
        coEvery { repo.findByNombreContainsIgnoreCase(any()) } returns flowOf()

        // Llamamos al método
        val result = repository.findByNombre("Test").toList()

        assertAll(
            { assertEquals(0, result.size) },
        )
    }

    @Test
    fun save() = runTest {
        // Usamos coEvery para poder usar corutinas
        coEvery { repo.save(any()) } returns representante

        // Llamamos al método
        val result = repository.save(representante)

        assertAll(
            { assertEquals(representante.nombre, result.nombre) },
            { assertEquals(representante.email, result.email) },
        )

        coVerify { repo.save(any()) }
    }


    @Test
    fun update() = runTest {
        // Usamos coEvery para poder usar corutinas
        coEvery { repo.findByUuid(any()) } returns flowOf(representante)
        coEvery { repo.save(any()) } returns representante

        // Llamamos al método
        val result = repository.update(representante.uuid, representante)!!

        assertAll(
            { assertEquals(representante.nombre, result.nombre) },
            { assertEquals(representante.email, result.email) },
        )

        coVerify { repo.save(any()) }
    }

    @Test
    fun updateNotFound() = runTest {
        // Usamos coEvery para poder usar corutinas
        coEvery { repo.findByUuid(any()) } returns flowOf()

        // Llamamos al método
        val result = repository.update(representante.uuid, representante)

        assertNull(result)

    }

    @Test
    fun deleteById() = runTest {
        // Usamos coEvery para poder usar corutinas
        coEvery { repo.deleteById(any()) } returns Unit

        // Llamamos al método
        repository.deleteById(1L)

        coVerify { repo.deleteById(any()) }
    }

    @Test
    fun findAllPage() = runTest {
        // Usamos coEvery para poder usar corutinas
        coEvery { repo.findAllBy(any()) } returns flowOf(representante)
        coEvery { repo.count() } returns 1L

        // Llamamos al método
        val result = repository.findAllPage(PageRequest.of(0, 10)).toList()[0].content

        assertAll(
            { assertEquals(1, result.size) },
            { assertEquals(representante, result[0]) }
        )

        coVerify { repo.findAllBy(any()) }
    }

    @Test
    fun countAll() = runTest {
        // Usamos coEvery para poder usar corutinas
        coEvery { repo.count() } returns 1L

        // Llamamos al método
        val result = repository.countAll()

        assertEquals(1L, result)

        coVerify { repo.count() }

    }

    @Test
    fun deleteByUuid() = runTest {
        // Usamos coEvery para poder usar corutinas
        coEvery { repo.findByUuid(any()) } returns flowOf(representante)
        coEvery { repo.deleteById(any()) } returns Unit

        // Llamamos al método
        repository.deleteByUuid(representante.uuid)

        coVerify { repo.findByUuid(any()) }
    }

    @Test
    fun deleteByUuidNotFound() = runTest {
        // Usamos coEvery para poder usar corutinas
        coEvery { repo.findByUuid(any()) } returns flowOf()

        // Llamamos al método
        repository.deleteByUuid(representante.uuid)

        coVerify { repo.findByUuid(any()) }
    }

    @Test
    fun delete() = runTest {
        // Usamos coEvery para poder usar corutinas
        coEvery { repo.findByUuid(any()) } returns flowOf(representante)
        coEvery { repo.deleteById(any()) } returns Unit

        // Llamamos al método
        repository.delete(representante)

        coVerify { repo.findByUuid(any()) }
        coVerify { repo.deleteById(any()) }
    }

    @Test
    fun deleteAll() = runTest {
        // Usamos coEvery para poder usar corutinas
        coEvery { repo.deleteAll() } returns Unit

        // Llamamos al método
        repository.deleteAll()

        coVerify { repo.deleteAll() }
    }
}