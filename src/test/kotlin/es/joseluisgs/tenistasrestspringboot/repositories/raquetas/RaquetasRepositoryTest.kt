package es.joseluisgs.tenistasrestspringboot.repositories.raquetas

import es.joseluisgs.tenistasrestspringboot.models.Raqueta
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
internal class RaquetasRepositoryTest {

    @Autowired
    lateinit var repository: RaquetasRepository

    private val raqueta = Raqueta(
        uuid = UUID.fromString("044e6ec7-aa6c-46bb-9433-8094ef4ae8bc"),
        marca = "Test",
        precio = 199.9,
        representanteId = UUID.fromString("b39a2fd2-f7d7-405d-b73c-b68a8dedbcdf"),
    )


    @Test
    fun findAll() = runTest {
        val result = repository.findAll().toList()

        // Comprobamos que el resultado es correcto
        assertAll(
            { assertNotNull(result) },
            { assertEquals("Babolat", result[0].marca) },
        )
    }

    @Test
    fun findAllPageable() = runTest {
        val result = repository.findAllBy(Pageable.ofSize(1)).toList()

        // Comprobamos que el resultado es correcto
        assertAll(
            { assertNotNull(result) },
            { assertEquals("Babolat", result[0].marca) },
        )

    }

    @Test
    fun findByUuid() = runTest {
        val result = repository.findByUuid("86084458-4733-4d71-a3db-34b50cd8d68f".toUUID()).first()

        // Comprobamos que el resultado es correcto
        assertAll(
            { assertEquals("Babolat", result.marca) },
            { assertEquals(200.0, result.precio) },
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
        val result = repository.save(raqueta)

        // Comprobamos que el resultado es correcto
        assertAll(
            { assertNotNull(result) },
            { assertEquals("Test", result.marca) },
        )
        repository.delete(result)
    }

    @Test
    fun update() = runTest {
        val result = repository.save(raqueta)

        // Comprobamos que el resultado es correcto
        assertAll(
            { assertNotNull(result) },
            { assertEquals("Test", result.marca) },
        )
        repository.delete(result)
    }

    @Test
    fun delete() = runTest {
        val result = repository.save(raqueta)

        // Comprobamos que el resultado es correcto
        assertAll(
            { assertNotNull(result) },
            { assertEquals("Test", result.marca) },
        )
        repository.delete(result)
    }

}