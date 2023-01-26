import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.0.3-SNAPSHOT"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.graalvm.buildtools.native") version "0.9.18"
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.spring") version "1.7.22"
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
    // implementation("org.springframework.boot:spring-boot-starter-security")

    // Validaciones de Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-validation")
    // Webflux y Reactividad
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    // Websocket
    implementation("org.springframework.boot:spring-boot-starter-websocket")

    // Jackson y serialización
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor") // Corutinas
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")

    // Mejoras de desarrollo
    // developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Bases de datos!!
    // runtimeOnly("com.h2database:h2")
    runtimeOnly("io.r2dbc:r2dbc-h2")

    // Para mis logs
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.4")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.security:spring-security-test")
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
