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
    alias(libs.plugins.skie)
    id("maven-publish")
}

group = rootProject.group
version = rootProject.version

kotlin {
    explicitApi()

    compilerOptions {
        optIn.add("kotlin.js.ExperimentalJsExport")
        optIn.add("kotlin.time.ExperimentalTime")
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

    val xcFramework = XCFramework("StytchConsumerSDK")
    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = "StytchConsumerSDK"
            xcFramework.add(this)
            linkerOpts.add("-L${rootProject.rootDir.parent}/stytch-multiplatform-shared/sdk/shared/src/iosMain/interop")
            if (target.name == "iosArm64") {
                linkerOpts.add("-lStytchIos")
            }
            if (target.name == "iosSimulatorArm64") {
                linkerOpts.add("-lStytchSimulator")
            }
            export("com.stytch.sdk:shared:$version")
        }
    }

    js {
        browser()
        outputModuleName = "@stytch/react-native-consumer"
        binaries.library()
        generateTypeScriptDefinitions()
        compilerOptions {
            target = "es2015"
            useEsClasses = true
        }
        useEsModules()
    }

    jvm()

    sourceSets {
        commonMain.dependencies {
            api("com.stytch.sdk:shared:$version")
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.ktorfit.lib.light)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.skie.configuration.annotations)
        }
    }
}

ktorfit {
    compilerPluginVersion.set("2.3.3")
}

tasks.named("sourcesJar").configure {
    setDependsOn(listOf("kspCommonMainKotlinMetadata"))
}

skie {
    isEnabled = true
}
