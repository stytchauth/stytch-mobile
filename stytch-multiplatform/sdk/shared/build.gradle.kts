import com.android.build.api.dsl.androidLibrary
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
}

kotlin {
    explicitApi()

    androidLibrary {
        namespace = "com.stytch.sdk"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
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
        }
    }
}
