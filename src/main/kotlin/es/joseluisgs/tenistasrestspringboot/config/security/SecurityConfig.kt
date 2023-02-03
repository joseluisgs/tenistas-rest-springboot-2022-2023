package es.joseluisgs.tenistasrestspringboot.config.security

import es.joseluisgs.tenistasrestspringboot.config.APIConfig
import es.joseluisgs.tenistasrestspringboot.config.security.jwt.JwtAuthenticationFilter
import es.joseluisgs.tenistasrestspringboot.config.security.jwt.JwtAuthorizationFilter
import es.joseluisgs.tenistasrestspringboot.config.security.jwt.JwtTokenUtils
import es.joseluisgs.tenistasrestspringboot.services.usuarios.UsuariosService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

//https://blog.devgenius.io/implementing-authentication-and-authorization-using-spring-security-kotlin-and-jwt-an-easy-and-cc82a1f20567
// https://stackoverflow.com/questions/74609057/how-to-fix-spring-authorizerequests-is-deprecated

@Configuration
@EnableWebSecurity // Habilitamos la seguridad web
// Activamos la seguridad a nivel de método, por si queremos trabajar a nivel de controlador
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
class SecurityConfig @Autowired constructor(
    private val userService: UsuariosService,
    private val jwtTokenUtils: JwtTokenUtils
) {

    private fun authManager(http: HttpSecurity): AuthenticationManager {
        val authenticationManagerBuilder = http.getSharedObject(
            AuthenticationManagerBuilder::class.java
        )
        authenticationManagerBuilder.userDetailsService(userService)
        return authenticationManagerBuilder.build()
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        val authenticationManager = authManager(http)
        // Vamos a crear el filtro de autenticación y el de autorización
        http.authorizeHttpRequests()
            .requestMatchers("/api/**")
            .permitAll()
            // Ahora vamos a permitir el acceso a los endpoints de login y registro
            // O permitir por roles
            .requestMatchers("/user").hasRole("USER")
            // O por permisos y metodos
            .requestMatchers(HttpMethod.GET, APIConfig.API_PATH + "/usuarios/**").hasAnyRole("USER", "ADMIN")
            .anyRequest().authenticated().and().csrf().disable()
            .authenticationManager(authenticationManager)
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            // Le añadimos el filtro de autenticación y el de autorización a la configuración
            //  // Será el encargado de coger el token y si es válido lo dejaremos pasar...
            .and()
            .addFilter(JwtAuthenticationFilter(jwtTokenUtils, authenticationManager))
            .addFilter(JwtAuthorizationFilter(jwtTokenUtils, userService, authenticationManager))

        return http.build()
    }
}


