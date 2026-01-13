import com.android.build.api.dsl.androidLibrary
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import kotlin.collections.listOf

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.skie)
    id("maven-publish")
}

group = rootProject.group
version = rootProject.version

kotlin {
    explicitApi()

    compilerOptions {
        optIn.add("kotlin.js.ExperimentalJsExport")
        freeCompilerArgs.addAll("-Xenable-suspend-function-exporting", "-Xexpect-actual-classes")
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

    val xcFramework = XCFramework("StytchConsumerExtensionsSDK")
    val interopDirectory = project.layout.projectDirectory.dir("../shared/src/iosMain/interop/")
    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = "StytchConsumerExtensionsSDK"
            if (target.name == "iosArm64") {
                linkerOpts.addAll(
                    listOf(
                        "-L$interopDirectory",
                        "-lStytchIos",
                    ),
                )
            }
            if (target.name == "iosSimulatorArm64") {
                linkerOpts.addAll(
                    listOf(
                        "-L$interopDirectory",
                        "-lStytchSimulator",
                    ),
                )
            }
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
            implementation(projects.sdk.consumerHeadless)
            implementation(libs.kotlinx.coroutines.core)
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

skie {
    isEnabled = true
}
