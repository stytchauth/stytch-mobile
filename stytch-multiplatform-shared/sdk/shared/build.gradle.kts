import com.android.build.api.dsl.androidLibrary
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.buildconfig)
    alias(libs.plugins.skie)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
    id("maven-publish")
}

group = rootProject.group
version = rootProject.version

kotlin {
    explicitApi()

    compilerOptions {
        optIn.add("kotlin.js.ExperimentalJsExport")
        optIn.add("kotlin.time.ExperimentalTime")
        freeCompilerArgs.addAll("-Xexpect-actual-classes", "-Xbinary=bundleId=$group.shared")
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

    val xcFramework = XCFramework("StytchSharedSDK")
    val interopDirectory = project.layout.projectDirectory.dir("src/iosMain/interop")
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { target ->
        target.compilations["main"].cinterops {
            val stytchSwiftUtils by creating {
                definitionFile.set(interopDirectory.file("StytchSwiftUtils.def"))
                if (target.name == "iosArm64") {
                    headers(interopDirectory.file("StytchSwiftUtils-device.h"))
                }
                if (target.name == "iosX64" || target.name == "iosSimulatorArm64") {
                    headers(interopDirectory.file("StytchSwiftUtils-simulator.h"))
                }
                packageName("com.stytch.sdk")
            }
        }
        target.binaries.framework {
            baseName = "StytchSharedSDK"
            xcFramework.add(this)
            if (target.name == "iosArm64") {
                linkerOpts("-F$interopDirectory/StytchSwiftUtils.xcframework/ios-arm64")
            }
            if (target.name == "iosX64" || target.name == "iosSimulatorArm64") {
                linkerOpts("-F$interopDirectory/StytchSwiftUtils.xcframework/ios-arm64_x86_64-simulator")
            }
            linkerOpts("-framework", "StytchSwiftUtils")
        }
    }

    js {
        browser()
        generateTypeScriptDefinitions()
        compilerOptions { target = "es2015" }
    }

    jvm()

    sourceSets {
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.dfp.android)
            implementation(libs.recaptcha)
            implementation(libs.credentials)
            implementation(libs.credentials.play.services.auth)
            implementation(libs.googleid)
            implementation(libs.activity)
            implementation(libs.activity.ktx)
            implementation(libs.browser)
            implementation(libs.biometric.ktx)
            implementation(libs.bcprov.jdk18on)
        }
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.ktor.client.auth)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktorfit.lib.light)
            implementation(libs.napier)
            implementation(libs.skie.configuration.annotations)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        jvmMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.bcprov.jdk18on)
        }
        jsMain.dependencies {
            implementation(libs.ktor.client.js)
        }
    }
}

buildConfig {
    useKotlinOutput()
    buildConfigField("SDK_NAME", "stytch-multiplatform")
    buildConfigField("SDK_VERSION", version.toString())
}

skie {
    isEnabled = true
}

ktorfit {
    compilerPluginVersion.set("2.3.3")
}

tasks.named("sourcesJar").configure {
    setDependsOn(listOf("kspCommonMainKotlinMetadata"))
}
