# Tenistas REST Spring Boot

Api REST de Tenistas con Spring Boot para acceso a Datos de 2º de DAM. Curso 2022/2023

[![Kotlin](https://img.shields.io/badge/Code-Kotlin-blueviolet)](https://kotlinlang.org/)
[![LICENSE](https://img.shields.io/badge/Lisence-CC-%23e64545)](https://joseluisgs.dev/docs/license/)
![GitHub](https://img.shields.io/github/last-commit/joseluisgs/tenistas-rest-springboot-2022-2023)

![imagen](./images/spring-boot.png)

- [Tenistas REST Spring Boot](#tenistas-rest-spring-boot)
  - [Descripción](#descripción)
    - [Advertencia](#advertencia)
    - [Tecnologías](#tecnologías)
  - [Dominio](#dominio)
    - [Representante](#representante)
    - [Raqueta](#raqueta)
    - [Tenista](#tenista)
    - [Usuario](#usuario)
  - [Proyectos y documentación anteriores](#proyectos-y-documentación-anteriores)
  - [Arquitectura](#arquitectura)
  - [Endpoints](#endpoints)
    - [Representantes](#representantes)
    - [Raquetas](#raquetas)
    - [Tenistas](#tenistas)
    - [Test](#test)
  - [Spring Boot](#spring-boot)
    - [Creando un proyecto](#creando-un-proyecto)
    - [Punto de Entrada](#punto-de-entrada)
    - [Parametrizando la aplicación](#parametrizando-la-aplicación)
    - [Componentes de Spring Boot](#componentes-de-spring-boot)
    - [IoC y DI en SpringBoot](#ioc-y-di-en-springboot)
    - [Spring Data JPA](#spring-data-jpa)
    - [Creando rutas](#creando-rutas)
      - [Comprensión de contenido](#comprensión-de-contenido)
      - [CORS](#cors)
    - [Responses](#responses)
      - [Paginación y ordenamiento](#paginación-y-ordenamiento)
    - [Requests](#requests)
      - [Parámetros de ruta](#parámetros-de-ruta)
      - [Parámetros de consulta](#parámetros-de-consulta)
      - [Peticiones datos serializados](#peticiones-datos-serializados)
      - [Peticiones con formularios](#peticiones-con-formularios)
      - [Peticiones multiparte](#peticiones-multiparte)
      - [Request validation](#request-validation)
    - [WebSockets](#websockets)
    - [SSL y Certificados](#ssl-y-certificados)
    - [Autenticación y Autorización con JWT](#autenticación-y-autorización-con-jwt)
    - [Testing](#testing)
    - [Despliegue](#despliegue)
      - [JAR](#jar)
      - [Aplicación](#aplicación)
      - [Docker](#docker)
    - [Documentación](#documentación)
  - [Reactividad](#reactividad)
  - [Inmutabilidad](#inmutabilidad)
  - [Caché](#caché)
  - [Notificaciones en tiempo real](#notificaciones-en-tiempo-real)
  - [Proveedor de Dependencias](#proveedor-de-dependencias)
  - [Seguridad de las comunicaciones](#seguridad-de-las-comunicaciones)
    - [SSL/TLS](#ssltls)
    - [Autenticación y Autorización con JWT](#autenticación-y-autorización-con-jwt-1)
    - [CORS](#cors-1)
    - [BCrypt](#bcrypt)
  - [Testing](#testing-1)
    - [Postman](#postman)
  - [Distribución y Despliegue](#distribución-y-despliegue)
  - [Documentación](#documentación-1)
  - [Recursos](#recursos)
  - [Autor](#autor)
    - [Contacto](#contacto)
    - [¿Un café?](#un-café)
  - [Licencia de uso](#licencia-de-uso)

## Descripción

El siguiente proyecto es una API REST de Tenistas con Spring Boot para Acceso a Datos de 2º de DAM. Curso
2022/2023. En ella se pretende crear un servicio completo para la gestión de tenistas, raquetas y representantes de
marcas de raquetas.

El objetivo es que el alumnado aprenda a crear un servicio REST con Spring Boot, con las operaciones CRUD, securizar el
servicio con JWT y SSL y usar un cliente para consumir el servicio. Se pretende que el servicio completo sea asíncrono y
reactivo en lo máximo posible agilizando el servicio mediante una caché.

Además que permita escuchar cambios en tiempo real usando websocket

Se realizará inyección de dependencias y un sistema de logging.

Tendrá una página web de presentación como devolución de recursos estáticos.

Este proyecto tiene a su "gemelo" implementando en Ktor: [tenistas-rest-ktor-2022-2023](https://github.com/joseluisgs/tenistas-rest-ktor-2022-2023)

### Advertencia

Esta API REST no está pensada para ser usada en producción. Es un proyecto de aprendizaje y por tanto algunas cosas no
se profundizan y otras están pensadas para poder realizarlas en clase de una manera más simple con el objetivo que el
alumnado pueda entenderlas mejor. No se trata de montar la mejor arquitectura o el mejor servicio, sino de aprender a
crear un servicio REST en el tiempo exigido por el calendario escolar.

Este proyecto está en constante evolución y se irán añadiendo nuevas funcionalidades y mejoras para el alumnado. De la
misma manera se irá completando la documentación asociada.

Si quieres colaborar, puedes hacerlo contactando [conmigo](#contacto).

### Tecnologías

- Servidor Web: [Spring Boot](https://spring.io/projects/spring-boot) - Framework para crear servicios usando Kotlin y
  Java como lenguaje.
- Autenticación: [JWT](https://jwt.io/) - JSON Web Token para la autenticación y autorización.
- Encriptado: [Bcrypt](https://en.wikipedia.org/wiki/Bcrypt) - Algoritmo de hash para encriptar contraseñas.
- Asincronía: [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) - Librería de Kotlin para la
  programación asíncrona.
- Logger: [Kotlin Logging](https://github.com/MicroUtils/kotlin-logging) - Framework para la gestión de logs.
<<<<<<< HEAD
- Caché: Sistema de [Cache](https://www.baeldung.com/spring-cache-tutorial) de Spring Boot.
=======
- Caché: Sistema de [caché](https://www.baeldung.com/spring-cache-tutorial) de Spring Boot.
>>>>>>> develop
- Base de datos: [H2](https://www.h2database.com/) - Base de datos relacional que te permite trabajar en memoria,
  fichero y servidor.
- Testing: [JUnit 5](https://junit.org/junit5/) - Framework para la realización de tests
  unitarios, [Mockk](https://mockk.io/) librería de Mocks para Kotlin, así como las propias herramientas de Spring Boot.

## Dominio

Gestionar tenistas, raquetas y representantes de marcas de raquetas. Sabemos que:

- Una raqueta tiene un representante y el representante es solo de una marca de raqueta (1-1). No puede haber raquetas
  sin representante y no puede haber representantes sin raquetas.
- Un tenista solo puede o no tener contrato con una raqueta y una raqueta o modelo de raqueta puede ser usada por varios
  tenistas (1-N). Puede haber tenistas sin raqueta y puede haber raquetas sin tenistas.
- Por otro lado tenemos usuarios con roles de administrador y usuarios que se pueden registrar, loguear consultar los
  datos y acceder a los datos de los usuarios (solo administradores).

### Representante

| Campo  | Tipo   | Descripción              |
|--------|--------|--------------------------|
| id     | UUID   | Identificador único      |
| nombre | String | Nombre del representante |
| email  | String | Email del representante  |

### Raqueta

| Campo         | Tipo          | Descripción                           |
|---------------|---------------|---------------------------------------|
| id            | UUID          | Identificador único                   |
| marca         | String        | Marca de la raqueta                   |
| precio        | Double        | Precio de la raqueta                  |
| representante | Representante | Representante de la raqueta (no nulo) |

### Tenista

| Campo           | Tipo      | Descripción                                    |
|-----------------|-----------|------------------------------------------------|
| id              | UUID      | Identificador único                            |
| nombre          | String    | Nombre del tenista                             |
| ranking         | Int       | Ranking del tenista                            |
| fechaNacimiento | LocalDate | Fecha de nacimiento del tenista                |
| añoProfesional  | Int       | Año en el que se convirtió en profesional      |
| altura          | Double    | Altura del tenista                             |
| peso            | Double    | Peso del tenista                               |
| manoDominante   | String    | Mano dominante del tenista (DERECHA/IZQUIERDA) |
| tipoReves       | String    | Tipo de revés del tenista (UNA_MANO/DOS_MANOS) |
| puntos          | Int       | Puntos del tenista                             |
| pais            | String    | País del tenista                               |
| raquetaID       | UUID      | Identificador de la raqueta (puede ser nulo)   |

### Usuario

| Campo    | Tipo   | Descripción                    |
|----------|--------|--------------------------------|
| id       | UUID   | Identificador único            |
| nombre   | String | Nombre del usuario             |
| email    | String | Email del usuario              |
| username | String | Rol del usuario                |
| password | String | Contraseña del usuario         |
| avatar   | String | Avatar del usuario             |
| rol      | Rol    | Rol del usuario (ADMIN o USER) |

## Proyectos y documentación anteriores

Parte de los contenidos a desarrollar en este proyecto se han desarrollado en proyectos anteriores. En este caso:

- [Kotlin-Ktor-REST-Service](https://github.com/joseluisgs/Kotlin-Ktor-REST-Service)
- [SpringBoot-Productos-REST-DAM-2021-2022](https://github.com/joseluisgs/SpringBoot-Productos-REST-DAM-2021-2022)

Para la parte de reactividad te recomiendo
leer: ["Ya no sé programar si no es reactivo"](https://joseluisgs.dev/blogs/2022/2022-12-06-ya-no-se-programar-sin-reactividad.html)

## Arquitectura

Nos centraremos en la arquitectura de la API REST. Para ello, usaremos el patrón de diseño MVC (Modelo Vista
Controlador) en capas.

![img_1.png](./images/layers.png)

![img_2.png](./images/expla.png)

## Endpoints

Recuerda que puedes conectarte de forma segura:

- Para la API REST: http://localhost:6969/api y https://localhost:6963/api
- Para la página web estática: http://localhost:6969/web y https://localhost:6963/web

Los endpoints que vamos a usar a nivel de api, parten de /api/ y puedes usarlos con tu cliente favorito. En este caso,
usaremos Postman:

### Representantes

| Método | Endpoint (/api)                        | Auth | Descripción                                             | Status Code (OK) | Content    |
|--------|----------------------------------------|------|---------------------------------------------------------|------------------|------------|
| GET    | /representantes                        | No   | Devuelve todos los representantes                       | 200              | JSON       |
| GET    | /representantes?page=X&size=Y&sortBy=Z | No   | Devuelve representantes paginados y ordenados por campo | 200              | JSON       |
| GET    | /representantes/{id}                   | No   | Devuelve un representante por su id                     | 200              | JSON       |
| POST   | /representantes                        | No   | Crea un nuevo representante                             | 201              | JSON       |
| PUT    | /representantes/{id}                   | No   | Actualiza un representante por su id                    | 200              | JSON       |
| DELETE | /representantes/{id}                   | No   | Elimina un representante por su id                      | 204              | No Content |
| GET    | /representantes/find?nombre=X          | No   | Devuelve los representantes con nombre X                | 200              | JSON       |
| WS     | /updates                               | No   | Devuelve los cambios en representantes en tiempo real   | ---              | ---        |

### Raquetas

| Método | Endpoint (/api)                  | Auth | Descripción                                                              | Status Code (OK) | Content    |
|--------|----------------------------------|------|--------------------------------------------------------------------------|------------------|------------|
| GET    | /raquetas                        | No   | Devuelve todas las raquetas                                              | 200              | JSON       |
| GET    | /raquetas?page=X&size=Y&sortBy=Z | No   | Devuelve raquetas paginadas y ordenadas por campo                        | 200              | JSON       |
| GET    | /raquetas/{id}                   | No   | Devuelve una raqueta por su id                                           | 200              | JSON       |
| POST   | /raquetas                        | No   | Crea una nueva raqueta                                                   | 201              | JSON       |
| PUT    | /raquetas/{id}                   | No   | Actualiza una raqueta por su id                                          | 200              | JSON       |
| DELETE | /raquetas/{id}                   | No   | Elimina una raqueta por su id                                            | 204              | No Content |
| GET    | /raquetas/find?marca=X           | No   | Devuelve las raquetas con marca X                                        | 200              | JSON       |
| GET    | /raquetas/{id}/representante     | No   | Devuelve el representante de la raqueta dado su id                       | 200              | JSON       |
| WS     | /updates                         | No   | Websocket para notificaciones los cambios en las raquetas en tiempo real | ---              | JSON       |

### Tenistas

| Método | Endpoint (/api)             | Auth | Descripción                                                              | Status Code (OK) | Content    |
|--------|-----------------------------|------|--------------------------------------------------------------------------|------------------|------------|
| GET    | /tenistas                   | No   | Devuelve todos los tenistas                                              | 200              | JSON       |
| GET    | /tenistas?page=X&size=Y&sortBy=Z  | No   | Devuelve tenistas paginados y ordenadas por campo                                              | 200              | JSON       |
| GET    | /tenistas/{id}              | No   | Devuelve un tenista por su id                                            | 200              | JSON       |
| POST   | /tenistas                   | No   | Crea un nuevo tenista                                                    | 201              | JSON       |
| PUT    | /tenistas/{id}              | No   | Actualiza un tenista por su id                                           | 200              | JSON       |
| DELETE | /tenistas/{id}              | No   | Elimina un tenista por su id                                             | 204              | No Content |
| GET    | /tenistas/find?nombre=X     | No   | Devuelve los tenistas con nombre X                                       | 200              | JSON       |
| GET    | /tenistas/{id}/raqueta      | No   | Devuelve la raqueta del tenista dado su id                               | 200              | JSON       |
| GET    | /tenistas/ranking/{ranking} | No   | Devuelve el tenista con ranking X                                        | 200              | JSON       |
| WS     | /updates           | No   | Websocket para notificaciones los cambios en los tenistas en tiempo real | ---              | JSON       |

### Test

| Método | Endpoint (/api) | Auth | Descripción                                                       | Status Code (OK) | Content    |
|--------|-----------------|------|-------------------------------------------------------------------|------------------|------------|
| GET    | /test?texto     | No   | Devuelve un JSON con datos de prueba y el texto de query opcional | 200              | JSON       |
| GET    | /test/{id}      | No   | Devuelve un JSON con datos de prueba por su id                    | 200              | JSON       |
| POST   | /test           | No   | Crea un nuevo JSON con datos de prueba                            | 201              | JSON       |
| PUT    | /test/{id}      | No   | Actualiza un JSON con datos de prueba por su id                   | 200              | JSON       |
| PATCH  | /test/{id}      | No   | Actualiza un JSON con datos de prueba por su id                   | 200              | JSON       |
| DELETE | /test/{id}      | No   | Elimina un JSON con datos de prueba por su id                     | 204              | No Content |

## Spring Boot

[Spring](https://spring.io/) es un framework de Java VM que nos permite crear aplicaciones web de forma rápida y
sencilla. En este caso, usaremos [Spring Boot](https://spring.io/projects/spring-boot), que es una versión simplificada
de Spring que nos ayuda en la configuración de sus elementos.

Se caracteriza por implementar el Contenedor
de [inversión de control](https://es.wikipedia.org/wiki/Inversi%C3%B3n_de_control): permite la configuración de los
componentes de aplicación y la administración del ciclo de vida de los objetos Java, se lleva a cabo principalmente a
través de la inyección de dependencias
y [programación orientada a aspectos](https://es.wikipedia.org/wiki/Programaci%C3%B3n_orientada_a_aspectos): habilita la
implementación de rutinas transversales.

![img_3.png](./images/springboot.jpeg)

### Creando un proyecto

Podemos crear un proyecto Spring Boot usando el plugin IntelliJ, desde su web. Con
estos [asistentes](https://start.spring.io/) podemos crear un proyecto Ktor con las opciones que queramos (plugins),
destacamos el routing, el uso de json, etc.

### Punto de Entrada

El servidor tiene su entrada y configuración en la clase Application. Esta lee la configuración en base
al [fichero de configuración](./src/main/resources/application.properties) y a partir de aquí se crea una instancia de
la
clase principal etiquetada con @SpringBootApplication

### Parametrizando la aplicación

La aplicación está parametrizada en el fichero de
configuración [application.properties](./src/main/resources/application.properties) que se encuentra en el directorio
resources. En este fichero podemos configurar el puerto, el modo de ejecución, etc.

Podemos tener distintos ficheros por ejemplo para desarrollo y producción.

Propiedades globales: src/main/resources/application.properties
Propiedades de producción: src/main/resources/application-prod.properties
Propiedades de desarrollo: src/main/resources/application-dev.properties
Y luego desde la línea de comandos podemos cargar un perfil concreto de la siguiente manera:

```bash
java -jar -Dspring.profiles.active=prod demo-0.0.1-SNAPSHOT.jar
```

```properties
server.port=${PORT:6963}
# Compresion de datos
server.compression.enabled=${COMPRESS_ENABLED:true}
server.compression.mime-types=text/html,text/xml,text/plain,text/css,application/json,application/javascript
server.compression.min-response-size=1024
# Configuramos el locale en España
spring.web.locale=es_ES
spring.web.locale-resolver=fixed
# directorio de almacenamiento
upload.root-location=uploads
#Indicamos el perfil por defecto (Base de datos y otros)
# dev: developmet. application-dev.properties
# prod: production. application-prod.properties
spring.profiles.active=dev
```

### Componentes de Spring Boot

Spring Boot nos ofrece una serie de componentes que nos ayudan a crear aplicaciones web de forma rápida y sencilla.
Nuestros componentes principales se etiquetarán con @ para que el framework Spring lo reconozca (módulo de inversión de
control y posterior inyección de dependencias). Cada uno tiene una misión en nuestra arquitectura:

![img_4.png](./images/components.png)

- Controladores: Se etiquetan como *@Controller* o en nuestro caso al ser una API REST como @RestController. Estos son
  los controladores que se encargan de recibir las peticiones de los usuarios y devolver respuestas.

- Servicios: Se etiquetan como *@Service*. Se encargan de implementar la parte de negocio o infraestructura. En nuestro
  caso puede ser el sistema de almacenamiento o parte de la seguridad y perfiles de usuario.

- Repositorios: Se etiquetan como *@Repository* e implementan la interfaz y operaciones de persistencia de la
  información. En nuestro caso, puede ser una base de datos o una API externa. Podemos extender de repositorios pre
  establecidos o diseñar el nuestro propio.

- Configuración: Se etiquetan como *@Configuration*. Se encargan de configurar los componentes de la aplicación. Se se
  suelen iniciar al comienzo de nuestra aplicación.

- Bean: La anotación *@Bean*, nos sirve para indicar que este bean será administrado por Spring Boot (Spring Container).
  La administración de estos beans se realiza mediante a anotaciones como @Configuration.

### IoC y DI en SpringBoot

La Inversión de control (Inversion of Control en inglés, IoC) es un principio de diseño de software en el que el flujo
de ejecución de un programa se invierte respecto a los métodos de programación tradicionales. En su lugar, en la
inversión de control se especifican respuestas deseadas a sucesos o solicitudes de datos concretas, dejando que algún
tipo de entidad o arquitectura externa lleve a cabo las acciones de control que se requieran en el orden necesario y
para el conjunto de sucesos que tengan que ocurrir.

La inyección de dependencias (en inglés Dependency Injection, DI) es un patrón de diseño orientado a objetos, en el que
se suministran objetos a una clase en lugar de ser la propia clase la que cree dichos objetos. Esos objetos cumplen
contratos que necesitan nuestras clases para poder funcionar (de ahí el concepto de dependencia). Nuestras clases no
crean los objetos que necesitan, sino que se los suministra otra clase 'contenedora' que inyectará la implementación
deseada a nuestro contrato.

El contenedor Spring IoC lee el elemento de configuración durante el tiempo de ejecución y luego ensambla el Bean a
través de la configuración. La inyección de dependencia de Spring se puede lograr a través del constructor, el método
Setter y el dominio de entidad. Podemos hacer uso de la anotación *@Autowired* para inyectar la dependencia en el
contexto requerido.

El contenedor llamará al constructor con parámetros al instanciar el bean, y cada parámetro representa la dependencia
que queremos establecer. Spring analizará cada parámetro, primero lo analizará por tipo, pero cuando sea incierto, luego
lo analizará de acuerdo con el nombre del parámetro (obtenga el nombre del parámetro a través de
ParameterNameDiscoverer, implementado por ASM).

```kotlin
class ProductosRestController
@Autowired constructor(
    private val productosRepository: ProductosRepository,

    ) {
    @GetMapping("/productos")
    fun getProducts(): List<Producto> {
        return productosRepository.findAll()
    }
}
```

A nivel de setter
Spring primero instancia el Bean y luego llama al método Setter que debe inyectarse para lograr la inyección de
dependencia. No recomendado

```kotlin
class ProductosRestController {
    private lateinit var productosRepository: ProductosRepository

    @Autowired
    fun setProductosRepository(productosRepository: ProductosRepository) {
        this.productosRepository = productosRepository
    }
}
```

### Spring Data JPA

[Spring Data](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#preface) es una librería de
persistencia que nos permite acceder a bases de datos relacionales y no relacionales de forma sencilla gracias
a [JPA](https://spring.io/projects/spring-data-jpa). Para ello podemos extender de la clase JpaRepository, que es una
clase de repositorio de Spring Data con más funcionalidades, como pueden ser las operaciones de consulta, inserción,
actualización y eliminación, así como las de paginación, ordenación o búsquedas.

Los principales son:

- CrudRepository: tiene las mayoría de las funcionalidades CRUD.
- PagingAndSortingRepository: ofrece mecanismos de paginación, ordenación y búsqueda.
- JpaRepository: proporciona algunos métodos relacionados con JPA, como vaciar el contexto de persistencia y eliminar
  registros en un lote.
- CoroutinesRepository: proporciona métodos de suspensión para usar con Kotlin Coroutines.
- MongoRepository: proporciona funcionalidades específicas de MongoDB.

Usaremos las anotaciones de JPA para definir entidades o colecciones, sus atributos y características de los mismos, así
como las relacionales existentes.

Podemos definir consultas personalizadas para las entidades de la aplicación. Para ello podemos usar la anotación @Query
con JPQL o @NativeQuery y usar el lenguaje del motor de Base de Datos.

Por otro lado, también podemos definir las consultas en base del nombre del método. Si lo definimos con una signatura
determinada con ellos se generará la consulta automáticamente.

### Creando rutas

Para crear las rutas vamos a usar on controlador de tipo RestController. Este controlador se encargará de recibir las
peticiones y devolver las respuestas. Para ello vamos a usar las anotaciones de Spring Web.

Las peticiones que vamos a recibir serán de tipo GET (GetMapping), POST (PostMapping), PUT (PutMapping), PATCH (
PatchMapping) y/o DELETE (DeleteMapping).

Además, podemos usar ResponseEntity para devolver el código de estado de la respuesta, así como el cuerpo de la misma.

```kotlin
@RestController
class ProductosRestController
@Autowired constructor(
    private val productosRepository: ProductosRepository,

    ) {
    @GetMapping("/productos")
    fun getProducts(): List<Producto> {
        return productosRepository.findAll()
    }
}
```

#### Comprensión de contenido

Podemos activar la comprensión de contenido para las peticiones y respuestas desde nuestro fichero de propiedades. Para
ello debemos añadir la siguiente propiedad:

```properties
# Compresion de datos
server.compression.enabled=${COMPRESS_ENABLED:true}
server.compression.mime-types=text/html,text/xml,text/plain,text/css,application/json,application/javascript
server.compression.min-response-size=1024
```

#### CORS

Si se supone que su servidor debe manejar solicitudes de origen cruzado (CORS),
debe [instalar y configurar](https://www.baeldung.com/spring-cors) el complemento CORS. Este complemento le permite
configurar hosts permitidos, métodos HTTP, encabezados establecidos por el cliente, etc. Para ello lo vamos a hacer en
un bean de tipo CorsConfiguration

```kotlin
@Configuration
class CorsConfig {
  //	@Bean
  // Cors para permitir cualquier petición
  public WebMvcConfigurer corsConfigurer()
  {
      return new WebMvcConfigurer () {
          @Override
          public void addCorsMappings(CorsRegistry registry) {
              registry.addMapping("/ **")
          }
      }
  }
}
```

### Responses

Para devolver las respuestas vamos a usar la clase ResponseEntity. Esta clase nos permite devolver el código de estado
de la respuesta, así como el cuerpo de la misma.

```kotlin
@GetMapping("/productos")
fun getProducts(): ResponseEntity<List<Producto>> {
    return ResponseEntity.ok(productosRepository.findAll())
}

@GetMapping("/productos/{id}")
fun getProduct(@PathVariable id: Long): ResponseEntity<Producto> {
    return ResponseEntity.ok(productosRepository.findById(id).get())
}

@PostMapping("/productos")
fun createProduct(@RequestBody producto: Producto): ResponseEntity<Producto> {
    return ResponseEntity.status(HttpStatus.CREATED).body(productosRepository.save(producto))
}

@PutMapping("/productos/{id}")
fun updateProduct(@PathVariable id: Long, @RequestBody producto: Producto): ResponseEntity<Producto> {
    return ResponseEntity.ok(productosRepository.save(producto))
}

@DeleteMapping("/productos/{id}")
fun deleteProduct(@PathVariable id: Long): ResponseEntity<Void> {
    productosRepository.deleteById(id)
    return ResponseEntity.noContent().build()
}
```

#### Paginación y ordenamiento

En Spring Data podemos hacer la paginación de las respuestas de las consultas y su ordenamiento. Para ello debemos usar
la clase [Pageable](https://www.baeldung.com/spring-data-jpa-pagination-sorting) siempre que estemos en un
JPARepository.

Pero en otros repositorios debemos adaptarnos a su filosofía de trabajo. Por ejemplo, en MongoDB podemos usar la
clase [PageRequest](https://www.baeldung.com/queries-in-spring-data-mongodb) para hacer la paginación. De la misma
debemos hacerlo con [Spring Data Reactive](https://www.vinsguru.com/r2dbc-pagination/), luego ajustando la respuesta.

```kotlin
suspend fun findAllPage(pageRequest: PageRequest): Flow<Page<Representante>> {
    return representantRepository.findAllBy(pageRequest)
    toList()
        .windowed(pageRequest.pageSize, pageRequest.pageSize, true)
        .map { PageImpl(it, pageRequest, representanteRepository.count()) }
        .asFlow()
}
```

### Requests

Las peticiones podemos hacerlas con usando los verbos http, y las anotaciones de Spring Web: GetMapping, PostMapping,
PutMapping, PatchMapping y DeleteMapping...

#### Parámetros de ruta

Podemos usar los parámetros de ruta para obtener información de la petición. Para ello debemos usar la anotación
@PathVariable

```kotlin
@GetMapping("/productos/{id}")
fun getById(@PathVariable id: Long): ResponseEntity<Producto> {
    return ResponseEntity.ok(productosRepository.findById(id).get())
}
```

#### Parámetros de consulta

Podemos usar los parámetros de consulta para obtener información de la petición. Para ello debemos usar la anotación
@RequestParam, si la tipamos como nula, o indicamos que no es requerida, podremos usarla como opcional.

```kotlin
@GetMapping("/productos")
fun getProducts(@RequestParam(required = false) nombre: String?): ResponseEntity<List<Producto>> {
    return ResponseEntity.ok(productosRepository.findByNombre(nombre))
}
```

#### Peticiones datos serializados

Podemos enviar datos serializados en el cuerpo de la petición. Para ello debemos usar la anotación @RequestBody

```kotlin
@PostMapping("/productos")
fun createProduct(@RequestBody producto: Producto): ResponseEntity<Producto> {
    return ResponseEntity.status(HttpStatus.CREATED).body(productosRepository.save(producto))
}
```

#### Peticiones con formularios

Podemos obtener los datos de un [formulario](https://www.baeldung.com/spring-url-encoded-form-data) con
MediaType.APPLICATION_FORM_URLENCODED_VALUE y aplicarlos a un mapa de datos.

```kotlin
@PostMapping(
    path = "/feedback",
    consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE]
)
fun handleNonBrowserSubmissions(@RequestParam paramMap MultiValueMap<String, String>): ResponseEntity<String> {
    return ResponseEntity.ok("Thanks for your feedback!")
}
```

#### Peticiones multiparte

Podemos obtener los datos de una [petición multiparte](https://www.baeldung.com/sprint-boot-multipart-requests) con
MediaType.MULTIPART_FORM_DATA_VALUE y aplicarlos a un mapa de datos.

```kotlin
@PostMapping(
    value = ["/create"],
    consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
)
fun createWithImage(
    @RequestPart("producto") productoDTO: ProductoCreateDTO,
    @RequestPart("file") file: MultipartFile
): ResponseEntity<ProductoDTO> {
    // ....
}
```

#### Request validation

Podemos usar la [validación](https://www.baeldung.com/spring-boot-bean-validation) usando la anotación @Valid. Para ello
podemos usar las anotaciones de restricción de [javax.validation.constraints](https://www.baeldung.com/javax-validation)

```kotlin
@PostMapping("/productos")
fun createProduct(@Valid @RequestBody producto: Producto): ResponseEntity<Producto> {
    return ResponseEntity.status(HttpStatus.CREATED).body(productosRepository.save(producto))
}
``` 

### WebSockets

En Spring podemos usar WebSockets para crear servicios de comunicación en tiempo real, gracias al starter:
org.springframework.boot:spring-boot-starter-websocket.

Tenemos dons formas de usarlo, con SockJS y [STOMP](https://www.baeldung.com/websockets-spring), o
con [WebSockets puros](https://www.baeldung.com/postman-websocket-apis).

Se ha dejado la configuración de ambos y un cliente para STOMP, pero nos hemos decantado por usar WebSockets puros para
poder seguir usando el cliente de Postman.

De hecho en la clase Handler del WS se ha implementado un patron observador.

```kotlin
override fun sendMessage(message: String) {
    logger.info { "Enviar mensaje: $message" }
    for (session in sessions) {
        if (session.isOpen) {
            logger.info { "Servidor envía: $message" }
            session.sendMessage(TextMessage(message))
        }
    }
}
```


### SSL y Certificados

Para trabajar con los certificados, los hemos creado y guardado en l carpeta cert de resources.Para ello hemos usado el
comando keytool de Java . Además hemos creado nuestra configuración es properties para poder usarlos en el código .

```properties
server.port = ${ PORT: 6963 }
# SSL
server.ssl.key - store - type = PKCS12
server.ssl.key - store = classpath:cert / server_keystore.p12
# The password used to generate the certificate
server.ssl.key - store - password = 1234567
# The alias mapped to the certificate
        server.ssl.key - alias = serverKeyPair
server.ssl.enabled = true
```

Además, hemos configurado nuestro servicio para que ademas responda a peticiones http, y que redirija a https en
SSConfig.

```kotlin
@Configuration
class SSLConfig {
    // (User-defined Property)
    @Value("\${server.http.port}")
    private val httpPort = "6969"

    // Creamos un bean que nos permita configurar el puerto de conexión sin SSL
    @Bean
    fun servletContainer(): ServletWebServerFactory {
        val connector = Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL)
        connector.port = httpPort.toInt()
        val tomcat = TomcatServletWebServerFactory()
        tomcat.addAdditionalTomcatConnectors(connector)
        return tomcat
    }
}
```

### Autenticación y Autorización con JWT

### Testing

### Despliegue

#### JAR

#### Aplicación

#### Docker

### Documentación

## Reactividad

Como todo concepto que aunque complicado de conseguir implica una serie de condiciones. La primera de ellas es asegurar
la asincronía en todo momento. Cosa que se ha hecho mediante Ktor y el uso de corrutinas.

Por otro lado el acceso de la base de datos no debe ser bloqueante, por lo que no se ha usado la librería Exposed de
Kotlin para acceder a la base de datos y que trabaja por debajo con el driver JDBC. Sabemos que esto se puede podemos
acercarnos a la Asincronía pura usando corrutinas y el manejo
de [contexto de transacción asíncrono](https://github.com/JetBrains/Exposed/wiki/Transactions).

En cualquier caso, hemos decidido usar el driver R2DBC con el objetivo que el acceso a la base de datos sea no
bloqueante y así poder aprovechar el uso de Flows en Kotlin y así poder usar la reactividad total en la base de datos
con las corrutinas y Ktor.

![reactividad](./images/reactive.gif)


> **Programación reactiva: programación asíncrona de flujos observables**
>
> Programar reactivamente una api comienza desde observar y procesar las colecciones existentes de manera asíncrona
> desde la base de datos hasta la respuesta que se ofrezca.

## Inmutabilidad

Es importante que los datos sean inmutables, es decir, que no se puedan modificar una vez creados en todo el proceso de
las capas de nuestra arquitectura. Esto nos permite tener un código más seguro y predecible. En Kotlin, por defecto,
podemos hacer que una clase sea inmutable, añadiendo el modificador val a sus propiedades.

Para los POKOS (Plain Old Kotlin Objects) usaremos Data Classes, que son clases inmutables por defecto y crearemos
objetos nuevos con las modificaciones que necesitemos con la función copy().

## Caché

La [caché](https://es.wikipedia.org/wiki/Cach%C3%A9_(inform%C3%A1tica)) es una forma de almacenar datos en memoria/disco
para que se puedan recuperar rápidamente. Además de ser una forma de optimizar el rendimiento, también es una forma de
reducir el coste de almacenamiento de datos y tiempo de respuesta pues los datos se almacenan en memoria y no en disco o
base de datos que pueden estar en otro servidor y con ello aumentar el tiempo de respuesta.

Además la caché nos ofrece automáticamente distintos mecanismos de actuación, como por ejemplo, que los elementos en
cache tenga un tiempo de vida máximo y se eliminen automáticamente cuando se cumpla. Lo que nos permite tener datos
actualizados Y/o los más usados en memoria y eliminar los que no se usan.

En nuestro proyecto tenemos dos repositorios, uno para la caché y otro para la base de datos. Para ello todas las
consultas usamos la caché y si no está, se consulta a la base de datos y se guarda en la caché. Además, podemos tener un
proceso en background que actualice la caché cada cierto tiempo solo si así lo configuramos, de la misma manera que el
tiempo de refresco.

Además, hemos optimizado las operaciones con corrutinas para que se ejecuten en paralelo actualizando la caché y la base
de datos.

El diagrama seguido es el siguiente

![cache](./images/cache.jpg)

Por otro lado también podemos configurar la Caché de Header a nivel de rutas o tipo de ficheros como se ha indicado.

Para este proyecto hemos usado [Cache4K](https://reactivecircus.github.io/cache4k/). Cache4k proporciona un caché de
clave-valor en memoria simple para Kotlin Multiplatform, con soporte para ivalidar items basados ​​en el tiempo (
caducidad) y en el tamaño.

## Notificaciones en tiempo real

Las notificaciones en tiempo real son una forma de comunicación entre el servidor y el cliente que permite que el
servidor envíe información al cliente sin que el cliente tenga que solicitarla. Esto permite que el servidor pueda
enviar información al cliente cuando se produzca un evento sin que el cliente tenga que estar constantemente consultando
al servidor.

Para ello usaremos [WebSockets](https://developer.mozilla.org/es/docs/Web/API/WebSockets_API) junto al
patrón [Observer](https://refactoring.guru/es/design-patterns/observer) para que el servidor pueda enviar información al
cliente cuando se produzca un evento sin que el cliente tenga que estar constantemente consultando al servidor.

Para ello, una vez el cliente se conecta al servidor, se le asigna un ID de sesión y se guarda en una lista de clientes
conectados. Cuando se produce un evento, se recorre la lista de clientes conectados y se envía la información a cada uno
de ellos, ejecutando la función de callback que se le ha pasado al servidor.

Además, podemos hacer uso de las funciones de serialización para enviar objetos complejos como JSON.

![observer](./images/observer.png)

## Proveedor de Dependencias

Usaremos el propio [Autowired](https://www.baeldung.com/spring-autowire) de Spring para inyectar las dependencias en las
clases que las necesiten. De esta manera, no tendremos que crear objetos de las clases que necesitemos, sino que Spring
se encargará de crearlos y de inyectarlos en las clases que las necesiten.

## Seguridad de las comunicaciones

### SSL/TLS

Para la seguridad de las comunicaciones
usaremos [SSL/TLS](https://es.wikipedia.org/wiki/Seguridad_de_la_capa_de_transporte) que es un protocolo de seguridad
que permite cifrar las comunicaciones entre el cliente y el servidor. Para ello usaremos un certificado SSL que nos
permitirá cifrar las comunicaciones entre el cliente y el servidor.

De esta manera, conseguiremos que los datos viajen cifrados entre el cliente y el servidor y que no puedan ser
interceptados por terceros de una manera sencilla.

Esto nos ayudará, a la hora de hacer el login de un usuario, a que la contraseña no pueda ser interceptada por terceros
y que el usuario pueda estar seguro de que sus datos están protegidos.

![tsl](./images/tsl.jpg)

### Autenticación y Autorización con JWT

Para la seguridad de las comunicaciones usaremos [JWT](https://jwt.io/) que es un estándar abierto (RFC 7519) que define
una forma compacta y autónoma de transmitir información entre partes como un objeto JSON. Esta información puede ser
verificada y confiada porque está firmada digitalmente. Las firmas también se pueden usar para asegurar la integridad de
los datos.

El funcionamiento de JWT es muy sencillo. El cliente hace una petición para autenticarse la primera vez. El servidor
genera un token que contiene la información del usuario y lo envía al cliente. El cliente lo guarda y lo envía en cada
petición al servidor. El servidor verifica el token y si es correcto, permite la petición al recurso.

![jwt](./images/tokens.png)

### CORS

Para la seguridad de las comunicaciones usaremos [CORS](https://developer.mozilla.org/es/docs/Web/HTTP/CORS) que es un
mecanismo que usa cabeceras HTTP adicionales para permitir que un user agent obtenga permiso para acceder a recursos
seleccionados desde un servidor, en un origen distinto (dominio) al que pertenece.

![cors](./images/cors.png)

### BCrypt

Para la seguridad de las comunicaciones usaremos [Bcrypt](https://en.wikipedia.org/wiki/Bcrypt) que es un algoritmo de
hash de contraseñas diseñado por Niels Provos y David Mazières, destinado a ser un método de protección contra ataques
de fuerza bruta. Con este algoritmo, se puede almacenar una contraseña en la base de datos de forma segura, ya que no se
puede obtener la contraseña original a partir de la contraseña almacenada.

![bcrypt](./images/bcrypt.png)

## Testing

Para testear se ha usado JUnit y MocKK como librerías de apoyo. Además, Hemos usado la propia api de Ktor para testear
las peticiones. Con ello podemos simular un Postman para testear las peticiones de manera local, con una instancia de
prueba de nuestro servicio.
![testear](./images/testing.png)

### Postman

Para probar con un cliente nuestro servicio usaremos [Postman](https://www.postman.com/) que es una herramienta de
colaboración para el desarrollo de APIs. Permite a los usuarios crear y compartir colecciones de peticiones HTTP, así
como documentar y probar sus APIs.

El fichero para probar nuestra api lo tienes en la carpera [postman](./postman) y puedes importarlo en tu Postman para
probar el resultado.

![postman](./images/postman.png)

## Distribución y Despliegue

Para la distribución de la aplicación usaremos [Docker](https://www.docker.com/) con su [Dockerfile](./Dockerfile).
Además, podemos usar [Docker Compose](https://docs.docker.com/compose/) con [docker-compose.yml](./docker-compose.yml)
que es una herramienta para definir y ejecutar aplicaciones Docker de múltiples contenedores.

![docker](./images/docker.jpg)

Por otro lado, podemos usar JAR o Aplicaciones de sistema tal y como hemos descrito en el apartado
de [Despliegue](#despliegue).

**Recuerda**: Si haces una imagen Docker mete todos los certificados y recursos que necesites que use adicionalmente
nuestra aplicación (directorios), si no no funcionará, pues así los usas en tu fichero de configuración. Recuerda lo que
usa tu fichero de [configuración](./src/main/kotlin/../resources/application.conf) para incluirlo.

## Documentación

La documentación sobre los métodos se pueden consultar en HTML realizada con Dokka.

La documentación de los endpoints se puede consultar en HTML realizada con Swagger.

![swagger](./images/swagger.png)

## Recursos

- Twitter: https://twitter.com/joseluisgonsan
- GitHub: https://github.com/joseluisgs
- Web: https://joseluisgs.github.io
- Discord del módulo: https://discord.gg/RRGsXfFDya
- Aula DAMnificad@s: https://discord.gg/XT8G5rRySU

## Autor

Codificado con :sparkling_heart: por [José Luis González Sánchez](https://twitter.com/joseluisgonsan)

[![Twitter](https://img.shields.io/twitter/follow/JoseLuisGS_?style=social)](https://twitter.com/joseluisgonsan)
[![GitHub](https://img.shields.io/github/followers/joseluisgs?style=social)](https://github.com/joseluisgs)
[![GitHub](https://img.shields.io/github/stars/joseluisgs?style=social)](https://github.com/joseluisgs)

### Contacto

<p>
  Cualquier cosa que necesites házmelo saber por si puedo ayudarte 💬.
</p>
<p>
 <a href="https://joseluisgs.github.io/" target="_blank">
        <img src="https://joseluisgs.github.io/img/favicon.png" 
    height="30">
    </a>  &nbsp;&nbsp;
    <a href="https://github.com/joseluisgs" target="_blank">
        <img src="https://distreau.com/github.svg" 
    height="30">
    </a> &nbsp;&nbsp;
        <a href="https://twitter.com/joseluisgonsan" target="_blank">
        <img src="https://i.imgur.com/U4Uiaef.png" 
    height="30">
    </a> &nbsp;&nbsp;
    <a href="https://www.linkedin.com/in/joseluisgonsan" target="_blank">
        <img src="https://upload.wikimedia.org/wikipedia/commons/thumb/c/ca/LinkedIn_logo_initials.png/768px-LinkedIn_logo_initials.png" 
    height="30">
    </a>  &nbsp;&nbsp;
    <a href="https://discordapp.com/users/joseluisgs#3560" target="_blank">
        <img src="https://logodownload.org/wp-content/uploads/2017/11/discord-logo-4-1.png" 
    height="30">
    </a> &nbsp;&nbsp;
    <a href="https://g.dev/joseluisgs" target="_blank">
        <img loading="lazy" src="https://googlediscovery.com/wp-content/uploads/google-developers.png" 
    height="30">
    </a>  &nbsp;&nbsp;
<a href="https://www.youtube.com/@joseluisgs" target="_blank">
        <img loading="lazy" src="https://upload.wikimedia.org/wikipedia/commons/e/ef/Youtube_logo.png" 
    height="30">
    </a>  
</p>

### ¿Un café?

<p><a href="https://www.buymeacoffee.com/joseluisgs"> <img align="left" src="https://cdn.buymeacoffee.com/buttons/v2/default-blue.png" height="50" alt="joseluisgs" /></a></p><br><br><br>

## Licencia de uso

Este repositorio y todo su contenido está licenciado bajo licencia **Creative Commons**, si desea saber más, vea
la [LICENSE](https://joseluisgs.dev/docs/license/). Por favor si compartes, usas o modificas este proyecto cita a su
autor, y usa las mismas condiciones para su uso docente, formativo o educativo y no comercial.

<a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/"><img alt="Licencia de Creative Commons" style="border-width:0" src="https://i.creativecommons.org/l/by-nc-sa/4.0/88x31.png" /></a><br /><span xmlns:dct="http://purl.org/dc/terms/" property="dct:title">
JoseLuisGS</span>
by <a xmlns:cc="http://creativecommons.org/ns#" href="https://joseluisgs.dev/" property="cc:attributionName" rel="cc:attributionURL">
José Luis González Sánchez</a> is licensed under
a <a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/">Creative Commons
Reconocimiento-NoComercial-CompartirIgual 4.0 Internacional License</a>.<br />Creado a partir de la obra
en <a xmlns:dct="http://purl.org/dc/terms/" href="https://github.com/joseluisgs" rel="dct:source">https://github.com/joseluisgs</a>.
