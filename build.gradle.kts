import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    // Cuidado con la versión de Spring Boot que usamos que arrastra a todas las dependencias
    id("org.springframework.boot") version "3.0.3-SNAPSHOT"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.graalvm.buildtools.native") version "0.9.18"
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.spring") version "1.7.22"
    // Documentar con Dokka
    id("org.jetbrains.dokka") version "1.7.20"
}

group = "es.joseluisgs"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
    maven { url = uri("https://repo.spring.io/snapshot") }
}


dependencies {
    // Dependencias de Spring Boot y Spring Data Reactive
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    // Dependencias de Seguridad Lo usaremos más adelante
    implementation("org.springframework.boot:spring-boot-starter-security")
    // Validaciones de Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-validation")
    // Webflux y Reactividad
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    // Websocket
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    // Cache
    implementation("org.springframework.boot:spring-boot-starter-cache")

    // Jackson y serialización
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor") // Corutinas
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")

    // JWT
    implementation("com.auth0:java-jwt:4.2.1")

    // Para las excepciones con spring security nuevo
    // https://wimdeblauwe.github.io/error-handling-spring-boot-starter/current/#goal
    // implementation("io.github.wimdeblauwe:error-handling-spring-boot-starter:4.1.0")

    // Mejoras de desarrollo
    // developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Bases de datos!!
    // runtimeOnly("com.h2database:h2")
    runtimeOnly("io.r2dbc:r2dbc-h2")

    // Para mis logs
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.4")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        // Desactivamos el mockito-core para usar mockk
        exclude(module = "mockito-core")
    }
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.security:spring-security-test")
    // corrutinas
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
    // Mockk
    testImplementation("com.ninja-squad:springmockk:4.0.0")

    // Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.2")
}

// Fijamos la versión de Kotlin de destno
tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

// Fijamos la plataforma de test
tasks.withType<Test> {
    useJUnitPlatform()
}
