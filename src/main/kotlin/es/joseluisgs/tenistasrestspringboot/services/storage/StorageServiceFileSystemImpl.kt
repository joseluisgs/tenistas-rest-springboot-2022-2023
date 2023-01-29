package es.joseluisgs.tenistasrestspringboot.services.storage

import es.joseluisgs.tenistasrestspringboot.controllers.StorageController
import es.joseluisgs.tenistasrestspringboot.exceptions.StorageBadRequestException
import es.joseluisgs.tenistasrestspringboot.exceptions.StorageFileNotFoundException
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import org.springframework.util.FileSystemUtils
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder
import java.io.IOException
import java.net.MalformedURLException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*
import java.util.stream.Stream

private val logger = KotlinLogging.logger {}

@Service
class StorageServiceFileSystemImpl(
    @Value("\${upload.root-location}") path: String,
    @Value("\${spring.profiles.active}") mode: String
) : StorageService {
    // Directorio raiz de nuestro almacén de ficheros
    private val rootLocation: Path

    // Inicializador
    init {
        logger.info { "Inicializando Servicio de Almacenamiento de Ficheros" }
        rootLocation = Paths.get(path)
        // Inicializamos el servicio de ficheros
        // Tomamos el perfil de la aplicación
        // Si es dev, borramos todos los ficheros
        // Si es prod, no borramos nada
        if (mode == "dev") {
            this.deleteAll()
        }
        this.init() // inicializamos
    }

    override fun store(file: MultipartFile): String {
        logger.info { "Almacenando fichero: ${file.originalFilename}" }

        val filename = StringUtils.cleanPath(file.originalFilename.toString())
        val extension = StringUtils.getFilenameExtension(filename).toString()
        val justFilename = filename.replace(".$extension", "")
        // Si queremos almacenar el fichero con el nombre original
        // val storedFilename = System.currentTimeMillis().toString() + "_" + justFilename + "." + extension
        // Si queremos almacenar el fichero con un nombre aleatorio
        val storedFilename = UUID.randomUUID().toString() + "." + extension
        try {
            if (file.isEmpty) {
                throw StorageBadRequestException("Fallo al almacenar un fichero vacío $filename")
            }
            if (filename.contains("..")) {
                // This is a security check
                throw StorageBadRequestException("No se puede almacenar un fichero fuera del path permitido $filename")
            }
            file.inputStream.use { inputStream ->
                Files.copy(
                    inputStream, rootLocation.resolve(storedFilename),
                    StandardCopyOption.REPLACE_EXISTING
                )
                return storedFilename
            }
        } catch (e: IOException) {
            throw StorageBadRequestException("Fallo al almacenar fichero $filename", e)
        }
    }

    override fun loadAll(): Stream<Path> {
        logger.info { "Cargando todos los ficheros" }

        return try {
            Files.walk(rootLocation, 1)
                .filter { path -> !path.equals(rootLocation) }
                .map(rootLocation::relativize)
        } catch (e: IOException) {
            throw StorageBadRequestException("Fallo al leer los ficheros almacenados", e)
        }
    }

    override fun load(filename: String): Path {
        logger.info { "Cargando fichero: $filename" }

        return rootLocation.resolve(filename)
    }

    override fun loadAsResource(filename: String): Resource {
        logger.info { "Cargando fichero como recurso: $filename" }

        return try {
            val file = load(filename)
            val resource = UrlResource(file.toUri())
            if (resource.exists() || resource.isReadable) {
                resource
            } else {
                throw StorageFileNotFoundException(
                    "No se puede leer fichero: $filename"
                )
            }
        } catch (e: MalformedURLException) {
            throw StorageFileNotFoundException("No se puede leer fichero: $filename", e)
        }
    }

    override fun deleteAll() {
        logger.info { "Borrando todos los ficheros" }

        FileSystemUtils.deleteRecursively(rootLocation.toFile())
    }


    override fun init() {
        logger.info { "Inicializando directorio de almacenamiento de ficheros" }

        try {
            // Si no existe el directorio lo creamos
            if (!Files.exists(rootLocation))
                Files.createDirectory(rootLocation)
        } catch (e: IOException) {
            throw StorageBadRequestException("No se puede inicializar el sistema de almacenamiento", e)
        }
    }

    override fun delete(filename: String) {
        logger.info { "Borrando fichero: $filename" }

        val justFilename: String = StringUtils.getFilename(filename).toString()
        try {
            val file = load(justFilename)
            // Si el fichero existe lo borramos, pero no ofrecemos error si no existe
            Files.deleteIfExists(file)
            // Si queremos mostrar un error si el fichero no existe
            /* if (!Files.exists(file))
                 throw StorageFileNotFoundException("Fichero $filename no existe")
             else
                 Files.delete(file)*/
        } catch (e: IOException) {
            throw StorageBadRequestException("Error al eliminar un fichero", e)
        }
    }

    override fun getUrl(filename: String): String {
        logger.info { "Obteniendo URL de fichero: $filename" }

        return MvcUriComponentsBuilder // El segundo argumento es necesario solo cuando queremos obtener la imagen
            // En este caso tan solo necesitamos obtener la URL
            .fromMethodName(StorageController::class.java, "serveFile", filename, null)
            .build().toUriString()
    }
}