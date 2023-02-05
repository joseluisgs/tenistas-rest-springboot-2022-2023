package es.joseluisgs.tenistasrestspringboot.config.swagger

import io.swagger.v3.oas.models.ExternalDocumentation
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

// https://springdoc.org/v2/#Introduction
// https://stackoverflow.com/questions/74614369/how-to-run-swagger-3-on-spring-boot-3
// http://localhost:XXXX/swagger-ui/index.html
@Configuration
class SwaggerConfig {
    @Bean
    fun apiInfo(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("API REST Tenistas Spring Boot Reactive")
                    .version("1.0.0")
                    .description("API de ejemplo del curso Desarrollo de un API REST con Spring Boot. 2022/2023")
                    .termsOfService("https://joseluisgs.dev/docs/license/")
                    .license(
                        License()
                            .name("CC BY-NC-SA 4.0")
                            .url("https://joseluisgs.dev/docs/license/")
                    )
                    .contact(
                        Contact()
                            .name("José Luis González Sánchez")
                            .email("joseluis.gonzales@iesluisvives.org")
                            .url("https://joseluisgs.dev")
                    )

            )
            .externalDocs(
                ExternalDocumentation()
                    .description("Repositorio y Documentación del Proyecto y API")
                    .url("https://github.com/joseluisgs/tenistas-rest-springboot-2022-2023")
            )
    }


    @Bean
    fun httpApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("http")
            //.pathsToMatch("/api/**")
            //.pathsToMatch("/api/tenistas/**")
            .pathsToMatch("/api/test/**")
            .displayName("HTTP-API Tenistas Test")
            .build()
    }
}