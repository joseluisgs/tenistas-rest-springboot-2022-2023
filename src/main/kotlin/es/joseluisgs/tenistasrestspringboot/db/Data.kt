package joseluisgs.es.db

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



