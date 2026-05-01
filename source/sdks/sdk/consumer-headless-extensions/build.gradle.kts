@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import com.android.build.api.dsl.androidLibrary
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.mavenPublish)
}

group = rootProject.group
version = rootProject.version

mavenPublishing {
    pom {
        name.set("Stytch Consumer SDK - Callback Extensions")
        description.set("Callback-style extensions for the Stytch Consumer SDK (Android and JVM)")
    }
}

val consumerHeadless = project(":sdk:consumer-headless")

kotlin {
    explicitApi()

    androidLibrary {
        namespace = "com.stytch.sdk.consumer.extensions"
        compileSdk =
            libs.versions.android.compileSdk
                .get()
                .toInt()
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
        optimization {
            consumerKeepRules.files.add(rootProject.file("consumer-rules.pro"))
            consumerKeepRules.publish = true
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
        jvmTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.mockk)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

tasks.withType<KotlinCompilationTask<*>>().configureEach {
    dependsOn(":sdk:consumer-headless:kspCommonMainKotlinMetadata")
}
