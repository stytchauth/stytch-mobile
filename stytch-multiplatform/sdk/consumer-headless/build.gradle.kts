import com.android.build.api.dsl.androidLibrary
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import kotlin.collections.listOf

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
    alias(libs.plugins.kotlin.serialization)
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
        namespace = "com.stytch.sdk.consumer"
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

    val xcFramework = XCFramework("StytchConsumer")
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = "StytchConsumer"
            xcFramework.add(this)
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
            api(projects.sdk.shared)
            implementation(libs.kotlinx.datetime)
            implementation(libs.ktorfit.lib.light)
            implementation(libs.kotlinx.serialization.json)
        }
    }
}

ktorfit {
    compilerPluginVersion.set("2.3.3")
}

publishing {
    repositories {
        maven {
            name = "TESTING"
            url = uri(layout.buildDirectory.dir("../../../../artifacts"))
        }
    }
}

tasks.named("sourcesJar").configure {
    setDependsOn(listOf("kspCommonMainKotlinMetadata"))
}
