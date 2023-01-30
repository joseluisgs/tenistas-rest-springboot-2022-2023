package es.joseluisgs.tenistasrestspringboot.services.tenistas

import es.joseluisgs.tenistasrestspringboot.config.websocket.ServerWebSocketConfig
import es.joseluisgs.tenistasrestspringboot.exceptions.RaquetaNotFoundException
import es.joseluisgs.tenistasrestspringboot.exceptions.TenistaNotFoundException
import es.joseluisgs.tenistasrestspringboot.models.Raqueta
import es.joseluisgs.tenistasrestspringboot.models.Tenista
import es.joseluisgs.tenistasrestspringboot.repositories.raquetas.RaquetasCachedRepository
import es.joseluisgs.tenistasrestspringboot.repositories.tenistas.TenistasCachedRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
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
import java.time.LocalDate
import java.util.*

@ExtendWith(MockKExtension::class)
@SpringBootTest
class TenistasServiceImplTest {

    private val tenista = Tenista(
        id = 99L,
        uuid = UUID.fromString("5d1e6fe1-5fa6-4494-a492-ae9725959035"),
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

    private val raqueta = Raqueta(
        uuid = UUID.fromString("e4a7b78e-f9ca-43df-b186-3811554eeeb2"),
        marca = "Head",
        precio = 225.0,
        represetanteId = UUID.fromString("a33cd6a6-e767-48c3-b07b-ab7e015a73cd")
    )

    @MockK
    lateinit var repository: TenistasCachedRepository

    @MockK
    lateinit var raquetasRepository: RaquetasCachedRepository

    @SpyK
    var webSocketConfig: ServerWebSocketConfig = ServerWebSocketConfig()

    @InjectMockKs
    lateinit var service: TenistasServiceImpl

    init {
        MockKAnnotations.init(this)
    }

    @Test
    fun findAll() = runTest {
        // Usamos coEvery para poder usar corutinas
        coEvery { repository.findAll() } returns flowOf(tenista)

        // Llamamos al método
        val result = service.findAll().toList()

        assertAll(
            { assertEquals(1, result.size) },
            { assertEquals(tenista, result[0]) }
        )

        coVerify(exactly = 1) { repository.findAll() }
    }

    @Test
    fun findById() = runTest {
        coEvery { repository.findById(any()) } returns tenista

        val result = service.findById(tenista.id!!)

        assertAll(
            { assertEquals(tenista.nombre, result.nombre) },
            { assertEquals(tenista.ranking, result.ranking) },
        )

        coVerify { repository.findById(any()) }
    }

    @Test
    fun findByIdNotFound() = runTest {
        coEvery { repository.findById(any()) } returns null

        val res = assertThrows<TenistaNotFoundException> {
            service.findById(tenista.id!!)
        }

        assertEquals("No se ha encontrado tenista con id: ${tenista.id}", res.message)

        coVerify { repository.findById(any()) }

    }

    @Test
    fun findByUuid() = runTest {
        coEvery { repository.findByUuid(any()) } returns tenista

        val result = service.findByUuid(raqueta.uuid)

        assertAll(
            { assertEquals(tenista.nombre, result.nombre) },
            { assertEquals(tenista.ranking, result.ranking) },
        )

        coVerify { repository.findByUuid(any()) }
    }

    @Test
    fun findByUuidNotFound() = runTest {
        coEvery { repository.findByUuid(any()) } returns null

        val res = assertThrows<TenistaNotFoundException> {
            service.findByUuid(raqueta.uuid)
        }

        assertEquals("No se ha encontrado tenista con uuid: ${raqueta.uuid}", res.message)

        coVerify { repository.findByUuid(any()) }
    }

    @Test
    fun findByNombre() = runTest {
        coEvery { repository.findByNombre(any()) } returns flowOf(tenista)

        val result = service.findByNombre(raqueta.marca).first()

        assertAll(
            { assertEquals(tenista.nombre, result.nombre) },
            { assertEquals(tenista.ranking, result.ranking) },
        )

        coVerify { repository.findByNombre(any()) }
    }

    @Test
    fun findByRanking() = runTest {
        coEvery { repository.findByRanking(any()) } returns flowOf(tenista)

        val result = service.findByRanking(raqueta.precio.toInt())

        assertAll(
            { assertEquals(tenista.nombre, result.nombre) },
            { assertEquals(tenista.ranking, result.ranking) },
        )

        coVerify { repository.findByRanking(any()) }
    }

    @Test
    fun findByRankingNotFound() = runTest {
        coEvery { repository.findByRanking(any()) } returns flowOf()

        val res = assertThrows<TenistaNotFoundException> {
            service.findByRanking(tenista.ranking)
        }

        assertEquals("No se ha encontrado tenista con ranking: ${tenista.ranking}", res.message)

        coVerify { repository.findByRanking(any()) }
    }

    @Test
    fun save() = runTest {
        coEvery { raquetasRepository.findByUuid(any()) } returns raqueta
        coEvery { repository.save(any()) } returns tenista

        val result = service.save(tenista)

        assertAll(
            { assertEquals(tenista.nombre, result.nombre) },
            { assertEquals(tenista.ranking, result.ranking) },
        )

        coVerify { repository.save(any()) }

    }

    @Test
    fun update() = runTest {
        coEvery { raquetasRepository.findByUuid(any()) } returns raqueta
        coEvery { repository.findByUuid(any()) } returns tenista
        coEvery { repository.update(any(), any()) } returns tenista

        val result = service.update(tenista.uuid, tenista)

        assertAll(
            { assertEquals(tenista.nombre, result.nombre) },
            { assertEquals(tenista.ranking, result.ranking) },
        )

        coVerify { repository.update(any(), any()) }
    }

    @Test
    fun updateNotFound() = runTest {
        coEvery { repository.findByUuid(any()) } throws TenistaNotFoundException("No se ha encontrado tenista con uuid: ${tenista.uuid}")
        coEvery { repository.update(any(), any()) } returns null

        val res = assertThrows<TenistaNotFoundException> {
            service.update(tenista.uuid, tenista)
        }

        assertEquals("No se ha encontrado tenista con uuid: ${tenista.uuid}", res.message)

        coVerify(exactly = 0) { repository.update(any(), any()) } // No se llama al método update

    }

    @Test
    fun delete() = runTest {
        coEvery { repository.findByUuid(any()) } returns tenista
        coEvery { repository.delete(any()) } returns tenista
        coEvery { raquetasRepository.findByUuid(any()) } returns raqueta

        val result = service.delete(tenista)

        assertAll(
            { assertEquals(tenista.nombre, result.nombre) },
            { assertEquals(tenista.ranking, result.ranking) },
        )

        coVerify { repository.delete(any()) }

    }

    @Test
    fun deleteByUuid() = runTest {
        coEvery { repository.findByUuid(any()) } returns tenista
        coEvery { repository.deleteByUuid(any()) } returns tenista
        coEvery { raquetasRepository.findByUuid(any()) } returns raqueta

        val result = service.deleteByUuid(raqueta.uuid)

        assertAll(
            { assertEquals(tenista.nombre, result.nombre) },
            { assertEquals(tenista.ranking, result.ranking) },
        )

        coVerify { repository.deleteByUuid(any()) }

    }

    @Test
    fun deleteUuidNotFound() = runTest {
        coEvery { repository.findByUuid(any()) } throws TenistaNotFoundException("No se ha encontrado tenista con uuid: ${tenista.uuid}")
        coEvery { repository.deleteByUuid(any()) } returns tenista

        val res = assertThrows<TenistaNotFoundException> {
            service.deleteByUuid(raqueta.uuid)
        }

        assertEquals("No se ha encontrado tenista con uuid: ${tenista.uuid}", res.message)

        coVerify(exactly = 0) { repository.deleteByUuid(any()) } // No se llama al método deleteByUuid

    }

    @Test
    fun findAllPage() = runTest {
        coEvery { repository.findAllPage(any()) } returns flowOf(PageImpl(listOf(tenista)))

        val result = service.findAllPage(PageRequest.of(0, 10)).first()

        assertAll(
            { assertEquals(1, result.size) },
            { assertEquals(tenista, result.content[0]) },
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
    fun findRaqueta() = runTest {
        coEvery { raquetasRepository.findByUuid(any()) } returns raqueta

        val result = service.findRaqueta(raqueta.represetanteId)

        assertEquals(raqueta, result)

        coVerify { raquetasRepository.findByUuid(any()) }

    }

    @Test
    fun findRaquetaNotFound() = runTest {
        coEvery { raquetasRepository.findByUuid(any()) } throws RaquetaNotFoundException("No se ha encontrado la raqueta con id: ${raqueta.id}")

        val res = assertThrows<RaquetaNotFoundException> {
            service.findRaqueta(raqueta.represetanteId)
        }

        assertEquals("No se ha encontrado la raqueta con id: ${raqueta.id}", res.message)

        coVerify(exactly = 1) { raquetasRepository.findByUuid(any()) } // No se llama al método findByUuid

    }
}