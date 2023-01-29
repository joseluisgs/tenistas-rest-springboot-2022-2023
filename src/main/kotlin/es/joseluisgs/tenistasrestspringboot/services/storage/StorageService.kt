package es.joseluisgs.tenistasrestspringboot.services.storage

import org.springframework.core.io.Resource
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Path
import java.util.stream.Stream

// Interfaz del servicio de almacenamiento
interface StorageService {
    // Inicia sl sistema de ficheros
    fun init()

    // Almacena un fichero llegado como un contenido multiparte
    fun store(file: MultipartFile): String

    // Devuleve un Stream con todos los ficheros
    fun loadAll(): Stream<Path>

    // Devuleve el Path o ruta de un fichero
    fun load(filename: String): Path

    // Devuelve el fichero como recurso
    fun loadAsResource(filename: String): Resource

    // Borra un fichero
    fun delete(filename: String)

    // Borra todos los ficheros
    fun deleteAll()

    // Obtiene la URL del fichero
    fun getUrl(filename: String): String
}
