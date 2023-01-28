package joseluisgs.es.db

import es.joseluisgs.tenistasrestspringboot.models.Raqueta
import es.joseluisgs.tenistasrestspringboot.models.Representante
import java.util.*

// Datos de prueba

// Representantes
fun getRepresentantesInit() = listOf(
    Representante(
        uuid = UUID.fromString("b39a2fd2-f7d7-405d-b73c-b68a8dedbcdf"),
        nombre = "Pepe Perez",
        email = "pepe@perez.com"
    ),
    Representante(
        uuid = UUID.fromString("c53062e4-31ea-4f5e-a99d-36c228ed01a3"),
        nombre = "Juan Lopez",
        email = "juan@lopez.com"
    ),
    Representante(
        uuid = UUID.fromString("a33cd6a6-e767-48c3-b07b-ab7e015a73cd"),
        nombre = "Maria Garcia",
        email = "maria@garcia.com"
    ),
)

// Raquetas
fun getRaquetasInit() = listOf(
    Raqueta(
        uuid = UUID.fromString("86084458-4733-4d71-a3db-34b50cd8d68f"),
        marca = "Babolat",
        precio = 200.0,
        represetanteId = UUID.fromString("b39a2fd2-f7d7-405d-b73c-b68a8dedbcdf")
    ),
    Raqueta(
        uuid = UUID.fromString("b0b5b2a1-5b1f-4b0f-8b1f-1b2c2b3c4d5e"),
        marca = "Wilson",
        precio = 250.0,
        represetanteId = UUID.fromString("c53062e4-31ea-4f5e-a99d-36c228ed01a3")
    ),
    Raqueta(
        uuid = UUID.fromString("e4a7b78e-f9ca-43df-b186-3811554eeeb2"),
        marca = "Head",
        precio = 225.0,
        represetanteId = UUID.fromString("a33cd6a6-e767-48c3-b07b-ab7e015a73cd")
    ),
)



