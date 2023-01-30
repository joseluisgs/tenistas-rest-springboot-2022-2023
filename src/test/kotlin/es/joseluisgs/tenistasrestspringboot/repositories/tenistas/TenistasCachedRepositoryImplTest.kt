package es.joseluisgs.tenistasrestspringboot.repositories.tenistas

import es.joseluisgs.tenistasrestspringboot.models.Tenista
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
import java.time.LocalDate
import java.util.*

// Voy a usar MocKK en lugar de Mockito
// https://spring.io/guides/tutorials/spring-boot-kotlin/
// @ExtendWith(SpringExtension::class)
@ExtendWith(MockKExtension::class)
@SpringBootTest
class TenistasCachedRepositoryImplTest {

    val tenista = Tenista(
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

    @MockK // @MockkBean
    lateinit var repo: TenistasRepository

    @InjectMockKs // @Autowired
    lateinit var repository: TenistasCachedRepositoryImpl


    init {
        MockKAnnotations.init(this)
    }

    @Test
    fun findAllPageable() = runTest {
        // Usamos coEvery para poder usar corutinas
        coEvery { repo.findByOrderByRankingAsc() } returns flowOf(tenista)

        // Llamamos al método
        val result = repository.findAll().toList()

        assertAll(
            { assertEquals(1, result.size) },
            { assertEquals(tenista, result[0]) }
        )

        coVerify(exactly = 1) { repo.findByOrderByRankingAsc() }
    }

    @Test
    fun findById() = runTest {
        // Usamos coEvery para poder usar corutinas
        coEvery { repo.findById(any()) } returns tenista

        // Llamamos al método
        val result = repository.findById(1L)!!

        assertAll(
            { assertEquals(tenista.nombre, result.nombre) },
            { assertEquals(tenista.ranking, result.ranking) },
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
        coEvery { repo.findByUuid(any()) } returns flowOf(tenista)

        // Llamamos al método
        val result = repository.findByUuid(tenista.uuid)!!

        assertAll(
            { assertEquals(tenista.nombre, result.nombre) },
            { assertEquals(tenista.ranking, result.ranking) },
        )
    }

    @Test
    fun findByUuidNotFound() = runTest {
        // Usamos coEvery para poder usar corutinas
        coEvery { repo.findByUuid(any()) } returns flowOf()

        // Llamamos al método
        val result = repository.findByUuid(tenista.uuid)

        assertNull(result)
    }

    @Test
    fun findByMarca() = runTest {
        // Usamos coEvery para poder usar corutinas
        coEvery { repo.findByNombreContainsIgnoreCase(any()) } returns flowOf(tenista)

        // Llamamos al método
        val result = repository.findByNombre("Test").toList()

        assertAll(
            { assertEquals(1, result.size) },
            { assertEquals(tenista, result[0]) }
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
        coEvery { repo.save(any()) } returns tenista

        // Llamamos al método
        val result = repository.save(tenista)

        assertAll(
            { assertEquals(tenista.nombre, result.nombre) },
            { assertEquals(tenista.ranking, result.ranking) },
        )

        coVerify { repo.save(any()) }
    }


    @Test
    fun update() = runTest {
        // Usamos coEvery para poder usar corutinas
        coEvery { repo.findByUuid(any()) } returns flowOf(tenista)
        coEvery { repo.save(any()) } returns tenista

        // Llamamos al método
        val result = repository.update(tenista.uuid, tenista)!!

        assertAll(
            { assertEquals(tenista.nombre, result.nombre) },
            { assertEquals(tenista.ranking, result.ranking) },
        )

        coVerify { repo.save(any()) }
    }

    @Test
    fun updateNotFound() = runTest {
        // Usamos coEvery para poder usar corutinas
        coEvery { repo.findByUuid(any()) } returns flowOf()

        // Llamamos al método
        val result = repository.update(tenista.uuid, tenista)

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
        coEvery { repo.findAllBy(any()) } returns flowOf(tenista)
        coEvery { repo.count() } returns 1L

        // Llamamos al método
        val result = repository.findAllPage(PageRequest.of(0, 10)).toList()[0].content

        assertAll(
            { assertEquals(1, result.size) },
            { assertEquals(tenista, result[0]) }
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
        coEvery { repo.findByUuid(any()) } returns flowOf(tenista)
        coEvery { repo.deleteById(any()) } returns Unit

        // Llamamos al método
        repository.deleteByUuid(tenista.uuid)

        coVerify { repo.findByUuid(any()) }
    }

    @Test
    fun deleteByUuidNotFound() = runTest {
        // Usamos coEvery para poder usar corutinas
        coEvery { repo.findByUuid(any()) } returns flowOf()

        // Llamamos al método
        repository.deleteByUuid(tenista.uuid)

        coVerify { repo.findByUuid(any()) }
    }

    @Test
    fun delete() = runTest {
        // Usamos coEvery para poder usar corutinas
        coEvery { repo.findByUuid(any()) } returns flowOf(tenista)
        coEvery { repo.deleteById(any()) } returns Unit

        // Llamamos al método
        repository.delete(tenista)

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