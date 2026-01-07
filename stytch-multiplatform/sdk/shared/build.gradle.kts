import com.android.build.api.dsl.androidLibrary
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.buildconfig)
    id("maven-publish")
}

group = rootProject.group
version = rootProject.version

kotlin {
    explicitApi()

    compilerOptions {
        optIn.add("kotlin.js.ExperimentalJsExport")
        freeCompilerArgs.addAll("-Xenable-suspend-function-exporting", "-Xexpect-actual-classes")
        // languageVersion.set(KotlinVersion.KOTLIN_2_0)
        // apiVersion.set(KotlinVersion.KOTLIN_2_0)
    }

    androidLibrary {
        namespace = "com.stytch.sdk"
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
        withHostTest {
            enableCoverage = true
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = "StytchCore"
        }
    }

    js {
        browser()
        outputModuleName = "@stytch/react-native"
        binaries.library()
        generateTypeScriptDefinitions()
        compilerOptions { target = "es2015" }
    }

    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.ktor.client.auth)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.cio)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.ktor.client.logging)
            implementation(libs.napier)
        }
    }
}

publishing {
    repositories {
        maven {
            name = "TESTING"
            url = uri(layout.buildDirectory.dir("../../../../artifacts"))
        }
    }
}

buildConfig {
    useKotlinOutput()
    buildConfigField("SDK_NAME", "stytch-kmp")
    buildConfigField("SDK_VERSION", version.toString())
}
