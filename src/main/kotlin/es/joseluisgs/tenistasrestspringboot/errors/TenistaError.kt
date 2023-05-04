package es.joseluisgs.tenistasrestspringboot.errors

/**
 * TenistaError
 * @param message: String Mensaje del error
 */
sealed class TenistaError(val message: String) {
    class NotFound(message: String) : TenistaError(message)
    class BadRequest(message: String) : TenistaError(message)
    class ConflictIntegrity(message: String) : TenistaError(message)
    class RaquetaNotFound(message: String) : TenistaError(message)

}