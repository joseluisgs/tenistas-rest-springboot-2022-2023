package es.joseluisgs.tenistasrestspringboot.errors

// Vamos a tipificar las excepciones y a crear una jerarquía de excepciones
/**
 * RepresentanteException
 * @param message: String Mensaje de la excepción
 */
sealed class RepresentanteError(val message: String) {
    class NotFound(message: String) : RepresentanteError(message)
    class BadRequest(message: String) : RepresentanteError(message)
    class ConflictIntegrity(message: String) : RepresentanteError(message)

}