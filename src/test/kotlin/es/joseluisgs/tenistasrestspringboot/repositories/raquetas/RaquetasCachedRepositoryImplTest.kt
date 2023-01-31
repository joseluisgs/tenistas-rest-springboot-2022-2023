package es.joseluisgs.tenistasrestspringboot.repositories.raquetas

import es.joseluisgs.tenistasrestspringboot.models.Raqueta
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
class RaquetasCachedRepositoryImplTest {

    private val raqueta = Raqueta(
        id = 99L,
        // usa un UUID para que no de problemas
        uuid = UUID.fromString("431ad089-d422-4aa4-b9a5-35a620eae8d4"),
        marca = "Test",
        precio = 199.9,
        representanteId = UUID.fromString("b39a2fd2-f7d7-405d-b73c-b68a8dedbcdf"),
    )

    @MockK
    lateinit var repo: RaquetasRepository

    @InjectMockKs
    lateinit var repository: RaquetasCachedRepositoryImpl


    init {
        MockKAnnotations.init(this)
    }

    @Test
    fun findAllPageable() = runTest {
        // Usamos coEvery para poder usar corutinas
        coEvery { repo.findAll() } returns flowOf(raqueta)

        // Llamamos al método
        val result = repository.findAll().toList()

        assertAll(
            { assertEquals(1, result.size) },
            { assertEquals(raqueta, result[0]) }
        )

        coVerify(exactly = 1) { repo.findAll() }
    }

    @Test
    fun findById() = runTest {
        // Usamos coEvery para poder usar corutinas
        coEvery { repo.findById(any()) } returns raqueta

        // Llamamos al método
        val result = repository.findById(1L)

        assertAll(
            { assertEquals(raqueta.marca, result!!.marca) },
            { assertEquals(raqueta.precio, result!!.precio) },
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
        coEvery { repo.findByUuid(any()) } returns flowOf(raqueta)

        // Llamamos al método
        val result = repository.findByUuid(raqueta.uuid)!!

        assertAll(
            { assertEquals(raqueta.marca, result.marca) },
            { assertEquals(raqueta.precio, result.precio) },
        )
    }

    @Test
    fun findByUuidNotFound() = runTest {
        // Usamos coEvery para poder usar corutinas
        coEvery { repo.findByUuid(any()) } returns flowOf()

        // Llamamos al método
        val result = repository.findByUuid(raqueta.uuid)

        assertNull(result)
    }

    @Test
    fun findByMarca() = runTest {
        // Usamos coEvery para poder usar corutinas
        coEvery { repo.findByMarcaContainsIgnoreCase(any()) } returns flowOf(raqueta)

        // Llamamos al método
        val result = repository.findByMarca("Test").toList()

        assertAll(
            { assertEquals(1, result.size) },
            { assertEquals(raqueta, result[0]) }
        )

        coVerify { repo.findByMarcaContainsIgnoreCase(any()) }
    }

    @Test
    fun findByMarcaNotFound() = runTest {
        // Usamos coEvery para poder usar corutinas
        coEvery { repo.findByMarcaContainsIgnoreCase(any()) } returns flowOf()

        // Llamamos al método
        val result = repository.findByMarca("Test").toList()

        assertAll(
            { assertEquals(0, result.size) },
        )
    }

    @Test
    fun save() = runTest {
        // Usamos coEvery para poder usar corutinas
        coEvery { repo.save(any()) } returns raqueta

        // Llamamos al método
        val result = repository.save(raqueta)

        assertAll(
            { assertEquals(raqueta.marca, result.marca) },
            { assertEquals(raqueta.precio, result.precio) },
        )

        coVerify { repo.save(any()) }
    }


    @Test
    fun update() = runTest {
        // Usamos coEvery para poder usar corutinas
        coEvery { repo.findByUuid(any()) } returns flowOf(raqueta)
        coEvery { repo.save(any()) } returns raqueta

        // Llamamos al método
        val result = repository.update(raqueta.uuid, raqueta)!!

        assertAll(
            { assertEquals(raqueta.marca, result.marca) },
            { assertEquals(raqueta.precio, result.precio) },
        )

        coVerify { repo.save(any()) }
    }

    @Test
    fun updateNotFound() = runTest {
        // Usamos coEvery para poder usar corutinas
        coEvery { repo.findByUuid(any()) } returns flowOf()

        // Llamamos al método
        val result = repository.update(raqueta.uuid, raqueta)

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
        coEvery { repo.findAllBy(any()) } returns flowOf(raqueta)
        coEvery { repo.count() } returns 1L

        // Llamamos al método
        val result = repository.findAllPage(PageRequest.of(0, 10)).toList()[0].content

        assertAll(
            { assertEquals(1, result.size) },
            { assertEquals(raqueta, result[0]) }
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
        coEvery { repo.findByUuid(any()) } returns flowOf(raqueta)
        coEvery { repo.deleteById(any()) } returns Unit

        // Llamamos al método
        repository.deleteByUuid(raqueta.uuid)

        coVerify { repo.findByUuid(any()) }
    }

    @Test
    fun deleteByUuidNotFound() = runTest {
        // Usamos coEvery para poder usar corutinas
        coEvery { repo.findByUuid(any()) } returns flowOf()

        // Llamamos al método
        repository.deleteByUuid(raqueta.uuid)

        coVerify { repo.findByUuid(any()) }
    }

    @Test
    fun delete() = runTest {
        // Usamos coEvery para poder usar corutinas
        coEvery { repo.findByUuid(any()) } returns flowOf(raqueta)
        coEvery { repo.deleteById(any()) } returns Unit

        // Llamamos al método
        repository.delete(raqueta)

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