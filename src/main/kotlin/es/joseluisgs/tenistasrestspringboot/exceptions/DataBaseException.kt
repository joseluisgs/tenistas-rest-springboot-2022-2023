package es.joseluisgs.tenistasrestspringboot.exceptions

sealed class DataBaseException(message: String?) : RuntimeException(message)
class DataBaseIntegrityViolationException(message: String? = null) : DataBaseException(message)