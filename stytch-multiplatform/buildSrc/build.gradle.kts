plugins {
    `kotlin-dsl`
    id("org.openapi.generator") version ("7.19.0")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.openapitools:openapi-generator:7.19.0")
}
