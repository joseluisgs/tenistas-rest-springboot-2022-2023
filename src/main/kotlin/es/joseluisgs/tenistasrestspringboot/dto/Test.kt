package es.joseluisgs.tenistasrestspringboot.dto

import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

data class TestDto(
    // Podemos validar los campos con anotaciones de Spring
    @field:NotBlank(message = "El mensaje no puede estar vacío")
    val message: String,
    @field:NotBlank(message = "La fecha no puede estar vacía")
    val createdAt: String = LocalDateTime.now().toString()
)