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

val b2bHeadless = project(":sdk:b2b-headless")

kotlin {
    explicitApi()

    androidLibrary {
        namespace = "com.stytch.sdk.b2b.extensions"
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
                b2bHeadless.layout.buildDirectory.dir("generated/callbacks/commonMain/kotlin"),
            )
            dependencies {
                api(b2bHeadless)
                implementation(libs.kotlinx.coroutines.core)
            }
        }
        jvmTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.mockk)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

tasks.withType<KotlinCompilationTask<*>>().configureEach {
    dependsOn(":sdk:b2b-headless:kspCommonMainKotlinMetadata")
}
