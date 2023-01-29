package es.joseluisgs.tenistasrestspringboot.controllers

import es.joseluisgs.tenistasrestspringboot.config.APIConfig
import es.joseluisgs.tenistasrestspringboot.exceptions.StorageBadRequestException
import es.joseluisgs.tenistasrestspringboot.exceptions.StorageException
import es.joseluisgs.tenistasrestspringboot.services.storage.StorageService
import jakarta.servlet.http.HttpServletRequest
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.time.LocalDateTime

private val logger = KotlinLogging.logger {}

// Podemos evitar los try catch ya que podemos usar el ResponseStatusException en las excepciones
// El problema de hacerlo así es que pierdes el control de como mapear los errores
// Elige el que más te guste y que mejor se adapte a tu proyecto
// A mi me gusta mas este, porque sé lo que me va a devolver y puedo controlar el error
// y devolver el que yo quiera, o incluso devolver un error personalizado, o saber qué testear y esperar

@RestController
@RequestMapping(APIConfig.API_PATH + "/storage")
class StorageController
@Autowired constructor(
    private val storageService: StorageService
) {
    @GetMapping(value = ["{filename:.+}"])
    @ResponseBody
    fun serveFile(
        @PathVariable filename: String?,
        request: HttpServletRequest
    )
            : ResponseEntity<Resource> {

        logger.info { "GET File: $filename" }

        val file: Resource = storageService.loadAsResource(filename.toString())
        var contentType: String? = null
        contentType = try {
            request.servletContext.getMimeType(file.file.absolutePath)
        } catch (ex: IOException) {
            throw StorageBadRequestException("No se puede determinar el tipo del fichero", ex)
        }
        if (contentType == null) {
            contentType = "application/octet-stream"
        }
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .body<Resource?>(file)
    }

    @PostMapping(
        value = [""],
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun uploadFile(
        @RequestPart("file") file: MultipartFile
    ): ResponseEntity<Map<String, String>> {

        logger.info { "POST File: ${file.originalFilename}" }

        return try {
            if (!file.isEmpty) {
                val fileStored = storageService.store(file)
                val urlStored = storageService.getUrl(fileStored)
                val response =
                    mapOf("url" to urlStored, "name" to fileStored, "created_at" to LocalDateTime.now().toString())
                ResponseEntity.status(HttpStatus.CREATED).body(response)
            } else {
                throw StorageBadRequestException("No se puede subir un fichero vacío")
            }
        } catch (e: StorageException) {
            throw StorageBadRequestException(e.message.toString())
        }
    }

    @DeleteMapping(value = ["{filename:.+}"])
    @ResponseBody
    fun deleteFile(
        @PathVariable filename: String?,
        request: HttpServletRequest
    )
            : ResponseEntity<Resource> {

        logger.info { "DELETE File: $filename" }
        try {
            storageService.delete(filename.toString())
            return ResponseEntity.ok().build()
        } catch (e: StorageException) {
            throw StorageBadRequestException(e.message.toString())
        }
    }

    // Implementar el resto de metodos del servicio que nos interesen...
    // Delete file, listar ficheros, etc....
}
