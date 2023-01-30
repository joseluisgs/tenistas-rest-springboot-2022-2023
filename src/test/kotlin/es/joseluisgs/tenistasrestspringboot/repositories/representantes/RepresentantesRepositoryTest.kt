package es.joseluisgs.tenistasrestspringboot.repositories.raquetas

import es.joseluisgs.tenistasrestspringboot.models.Representante
import es.joseluisgs.tenistasrestspringboot.repositories.representantes.RepresentantesRepository
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
internal class RepresentantesRepositoryTest {

    @Autowired
    lateinit var repository: RepresentantesRepository

    private val representante = Representante(
        uuid = UUID.fromString("91e0c247-c611-4ed2-8db8-a495f1f16fee"),
        nombre = "Test",
        email = "test@example.com",
    )


    @Test
    fun findAll() = runTest {
        val result = repository.findAll().toList()

        // Comprobamos que el resultado es correcto
        assertAll(
            { assertNotNull(result) },
            { assertEquals("Pepe Perez", result[0].nombre) },
        )
    }

    @Test
    fun findAllPageable() = runTest {
        val result = repository.findAllBy(Pageable.ofSize(1)).toList()

        // Comprobamos que el resultado es correcto
        assertAll(
            { assertNotNull(result) },
            { assertEquals("Pepe Perez", result[0].nombre) },
        )

    }

    @Test
    fun findByUuid() = runTest {
        val result = repository.findByUuid("b39a2fd2-f7d7-405d-b73c-b68a8dedbcdf".toUUID()).first()

        // Comprobamos que el resultado es correcto
        assertAll(
            { assertEquals("Pepe Perez", result.nombre) },
            { assertEquals("pepe@perez.com", result.email) },
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
        val result = repository.save(representante)

        // Comprobamos que el resultado es correcto
        assertAll(
            { assertNotNull(result) },
            { assertEquals("Test", result.nombre) },
        )
        repository.delete(result)
    }

    @Test
    fun update() = runTest {
        val result = repository.save(representante)

        // Comprobamos que el resultado es correcto
        assertAll(
            { assertNotNull(result) },
            { assertEquals("Test", result.nombre) },
        )
        repository.delete(result)
    }

    @Test
    fun delete() = runTest {
        val result = repository.save(representante)

        // Comprobamos que el resultado es correcto
        assertAll(
            { assertNotNull(result) },
            { assertEquals("Test", result.nombre) },
        )
        repository.delete(result)
    }

}