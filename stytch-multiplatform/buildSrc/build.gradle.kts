plugins {
    `kotlin-dsl`
    kotlin("jvm") version "2.3.0" apply false
    id("org.openapi.generator") version ("7.19.0")
}

buildscript {
    dependencies {
        classpath(kotlin("gradle-plugin", version = "2.3.0"))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.openapitools:openapi-generator:7.19.0")
    implementation("com.squareup:kotlinpoet:2.2.0")
    implementation("com.squareup:kotlinpoet-ksp:2.2.0")
    implementation("com.google.devtools.ksp:symbol-processing-api:2.3.0")
}
