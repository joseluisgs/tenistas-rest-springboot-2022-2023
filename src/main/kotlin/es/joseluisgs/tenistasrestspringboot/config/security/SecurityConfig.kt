package es.joseluisgs.tenistasrestspringboot.config.security

import es.joseluisgs.tenistasrestspringboot.config.security.jwt.JwtAuthenticationFilter
import es.joseluisgs.tenistasrestspringboot.config.security.jwt.JwtAuthorizationFilter
import es.joseluisgs.tenistasrestspringboot.config.security.jwt.JwtTokenUtils
import es.joseluisgs.tenistasrestspringboot.services.usuarios.UsuariosService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain


//https://blog.devgenius.io/implementing-authentication-and-authorization-using-spring-security-kotlin-and-jwt-an-easy-and-cc82a1f20567
// https://stackoverflow.com/questions/74609057/how-to-fix-spring-authorizerequests-is-deprecated

private val logger = KotlinLogging.logger {}

@Configuration
@EnableWebSecurity // Habilitamos la seguridad web
// Activamos la seguridad a nivel de método, por si queremos trabajar a nivel de controlador
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
class SecurityConfig @Autowired constructor(
    private val userService: UsuariosService,
    private val jwtTokenUtils: JwtTokenUtils
) {

    @Bean
    fun authManager(http: HttpSecurity): AuthenticationManager {
        val authenticationManagerBuilder = http.getSharedObject(
            AuthenticationManagerBuilder::class.java
        )
        authenticationManagerBuilder.userDetailsService(userService)
        return authenticationManagerBuilder.build()
    }

    // Ignoramos los endpoints que no queremos que se autentiquen
    // importante para las excepciones personalizadas de ResponseStatusException
    // ya que el simpático de Spring Security se lo come las desvía a /error
    // Si no quieres hacer esto puedes añadir la librería que he dejado comentada en el build.gradle
    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web: WebSecurity ->
            web.ignoring().requestMatchers("/error/**")
        }
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        val authenticationManager = authManager(http)
        // Vamos a crear el filtro de autenticación y el de autorización
        http
            .csrf()
            .disable()
            .exceptionHandling()
            .and()

            // Indicamos que vamos a usar un autenticador basado en JWT
            .authenticationManager(authenticationManager)

            // Para el establecimiento de sesiones son estado, no usamos sesiones
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

            .and()

            .authorizeHttpRequests()
            .requestMatchers("/api/**")
            .permitAll()


            // Ahora vamos a permitir el acceso a los endpoints de login y registro
            .requestMatchers("users/login", "users/register").permitAll()

            // O permitir por roles
            .requestMatchers("/user/me").hasAnyRole("ADMIN")

            // O por permisos y metodos
            .requestMatchers(HttpMethod.GET, "/user/list").hasRole("ADMIN")

            // Las otras peticiones no requerirán autenticación,
            .anyRequest().authenticated() // .not().authenticated();

            .and()

            // Le añadimos el filtro de autenticación y el de autorización a la configuración
            // Será el encargado de coger el token y si es válido lo dejaremos pasar...
            //.addFilter(JwtAuthenticationFilter(jwtTokenUtils, authenticationManager))
            //.addFilter(JwtAuthorizationFilter(jwtTokenUtils, userService, authenticationManager))
            .addFilterBefore(
                JwtAuthenticationFilter(jwtTokenUtils, authenticationManager),
                JwtAuthorizationFilter::class.java
            )
            .addFilterBefore(
                JwtAuthorizationFilter(jwtTokenUtils, userService, authenticationManager),
                JwtAuthenticationFilter::class.java
            )

        return http.build()
    }
}


