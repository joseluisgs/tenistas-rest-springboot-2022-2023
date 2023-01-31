package es.joseluisgs.tenistasrestspringboot.controllers

/**
 * Y así lo podemos testear con JUnit 5 y Spring Boot
 * Usando un lciente con reactividad
 */
/*
@ExtendWith(SpringExtension::class) //  We create a `@SpringBootTest`, starting an actual server on a `RANDOM_PORT`
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class GreetingRouterTest {
    // Spring Boot will create a `WebTestClient` for you,
    // already configure and ready to issue requests against "localhost:RANDOM_PORT"

    val sslContext = SslContextBuilder
        .forClient().trustManager(InsecureTrustManagerFactory.INSTANCE)
        .build()
    val httpClient = HttpClient.create().secure { spec: SslProvider.SslContextSpec -> spec.sslContext(sslContext) }
    final val connector = ReactorClientHttpConnector(httpClient)
    var client: WebTestClient = WebTestClient
        .bindToServer(connector)
        .baseUrl("https://localhost:6969/api")
        .build()

    @Test
    fun testHello() {
        client // Create a GET request to test an endpoint
            .get().uri("/api/test")
            .accept(MediaType.APPLICATION_JSON)
            .exchange() // and use the dedicated DSL to test assertions against the response
            .expectStatus().isOk
            .expectBody(TestDto::class.java)
            .value { greeting -> assertThat(greeting.message).isEqualTo("Hello, Spring!") }
    }
}
*/
