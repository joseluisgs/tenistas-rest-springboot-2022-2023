package es.joseluisgs.tenistasrestspringboot.repositories.raquetas

import es.joseluisgs.tenistasrestspringboot.models.Tenista
import es.joseluisgs.tenistasrestspringboot.repositories.tenistas.TenistasRepository
import joseluisgs.es.utils.toUUID
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Pageable
import java.time.LocalDate
import java.util.*

// Como es reactivo no podemos testear usando @DataJpaTest y entityManager


/**
 * En el fondo este no hace falta hacerlo, porque ya lo ha testeado spring ;)
 * Para eos es suyo!!
 */
@SpringBootTest
// Levanta la base de datos en memoria
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // BeforeAll y AfterAll
internal class TenistasRepositoryTest {

    @Autowired
    lateinit var repository: TenistasRepository

    val tenista = Tenista(
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


    @Test
    fun findAll() = runTest {
        val result = repository.findAll().toList()

        // Comprobamos que el resultado es correcto
        assertAll(
            { assertNotNull(result) },
            { assertEquals("Rafael Nadal", result[0].nombre) },
        )
    }

    @Test
    fun findAllPageable() = runTest {
        val result = repository.findAllBy(Pageable.ofSize(1)).toList()

        // Comprobamos que el resultado es correcto
        assertAll(
            { assertNotNull(result) },
            { assertEquals("Rafael Nadal", result[0].nombre) },
        )

    }

    @Test
    fun findByRankingOrderByRanking() = runTest {
        val result = repository.findByOrderByRankingAsc().toList()

        // Comprobamos que el resultado es correcto
        assertAll(
            { assertNotNull(result) },
            { assertEquals("Carlos Alcaraz", result[0].nombre) },
        )
    }

    @Test
    fun findByUuid() = runTest {
        val result = repository.findByUuid("ea2962c6-2142-41b8-8dfb-0ecfe67e27df".toUUID()).first()

        // Comprobamos que el resultado es correcto
        assertAll(
            { assertEquals("Rafael Nadal", result.nombre) },
            { assertEquals(2, result.ranking) }
        )
    }

    @Test
    fun findByUudiNotExists() = runTest {
        val result = repository.findByUuid(UUID.randomUUID()).firstOrNull()

        // Comprobamos que el resultado es correcto
        assertNull(result)

    }

    @Test
    fun save() = runTest {
        val result = repository.save(tenista)

        // Comprobamos que el resultado es correcto
        assertAll(
            { assertNotNull(result) },
            { assertEquals("Test", result.nombre) },
        )
        repository.delete(result)
    }

    @Test
    fun update() = runTest {
        val result = repository.save(tenista)

        // Comprobamos que el resultado es correcto
        assertAll(
            { assertNotNull(result) },
            { assertEquals("Test", result.nombre) },
        )
        repository.delete(result)
    }

    @Test
    fun delete() = runTest {
        val result = repository.save(tenista)

        // Comprobamos que el resultado es correcto
        assertAll(
            { assertNotNull(result) },
            { assertEquals("Test", result.nombre) },
        )
        repository.delete(result)
    }

}