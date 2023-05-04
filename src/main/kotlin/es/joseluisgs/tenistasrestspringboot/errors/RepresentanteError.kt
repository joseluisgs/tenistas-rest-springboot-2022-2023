package es.joseluisgs.tenistasrestspringboot.errors

/**
 * RepresentanteError
 * @param message: String Mensaje del error
 */
sealed class RepresentanteError(val message: String) {
    class NotFound(message: String) : RepresentanteError(message)
    class BadRequest(message: String) : RepresentanteError(message)
    class ConflictIntegrity(message: String) : RepresentanteError(message)

}