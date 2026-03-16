@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import com.android.build.api.dsl.androidLibrary
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    id("maven-publish")
}

group = rootProject.group
version = rootProject.version

val consumerHeadless = project(":sdk:consumer-headless")

kotlin {
    explicitApi()

    androidLibrary {
        namespace = "com.stytch.sdk.consumer.extensions"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    jvm()

    sourceSets {
        commonMain {
            kotlin.srcDir(
                consumerHeadless.layout.buildDirectory.dir("generated/callbacks/commonMain/kotlin"),
            )
            dependencies {
                api(consumerHeadless)
                implementation(libs.kotlinx.coroutines.core)
            }
        }
    }
}

tasks.withType<KotlinCompilationTask<*>>().configureEach {
    dependsOn(":sdk:consumer-headless:kspCommonMainKotlinMetadata")
}
