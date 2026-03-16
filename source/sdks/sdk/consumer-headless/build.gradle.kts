@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import com.android.build.api.dsl.androidLibrary
import com.android.build.gradle.tasks.ProcessLibraryArtProfileTask
import com.google.devtools.ksp.gradle.KspAATask
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import org.jetbrains.kotlin.gradle.tasks.KotlinCompileCommon

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.skie)
    alias(libs.plugins.kover)
    id("maven-publish")
    alias(libs.plugins.openapi)
}

group = rootProject.group
version = rootProject.version

kotlin {
    explicitApi()

    compilerOptions {
        optIn.add("kotlin.js.ExperimentalJsExport")
        optIn.add("kotlin.time.ExperimentalTime")
        freeCompilerArgs.addAll("-Xenable-suspend-function-exporting", "-Xexpect-actual-classes", "-Xbinary=bundleId=$group.consumer")
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
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = "StytchConsumerSDK"
            xcFramework.add(this)
            val sharedIosProjectDirectory = "${rootProject.layout.projectDirectory.dir("../shared/sdk/shared")}"
            if (target.name == "iosArm64") {
                linkerOpts("-F$sharedIosProjectDirectory/build/XCFrameworks/release/StytchSharedSDK.xcframework/ios-arm64")
                linkerOpts("-F$sharedIosProjectDirectory/src/iosMain/interop/StytchSwiftUtils.xcframework/ios-arm64")
            }
            if (target.name == "iosX64" || target.name == "iosSimulatorArm64") {
                linkerOpts("-F$sharedIosProjectDirectory/build/XCFrameworks/release/StytchSharedSDK.xcframework/ios-arm64_x86_64-simulator")
                linkerOpts("-F$sharedIosProjectDirectory/src/iosMain/interop/StytchSwiftUtils.xcframework/ios-arm64_x86_64-simulator")
            }
            linkerOpts("-framework", "StytchSharedSDK")
            linkerOpts("-framework", "StytchSwiftUtils")
            export("com.stytch.sdk:shared:$version")
        }
    }

    js {
        browser()
        outputModuleName = "consumer-headless"
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
        commonMain {
            kotlin.srcDir(tasks.named("openApiGenerate").map { layout.buildDirectory.dir("generated/openapi/src/main/kotlin") })
            dependencies {
                api("com.stytch.sdk:shared:$version")
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.datetime)
                implementation(libs.ktorfit.lib.light)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.skie.configuration.annotations)
            }
        }
        jvmTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.mockk)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

dependencies {
    add("kspCommonMainMetadata", project(":buildSrc"))
    add("kspAndroid", project(":buildSrc"))
    add("kspIosArm64", project(":buildSrc"))
    add("kspIosX64", project(":buildSrc"))
    add("kspIosSimulatorArm64", project(":buildSrc"))
    add("kspJs", project(":buildSrc"))
    add("kspJvm", project(":buildSrc"))
}

ksp {
    arg(
        "stytchCallbackOutputDir",
        layout.buildDirectory
            .dir("generated/callbacks/commonMain/kotlin")
            .get()
            .asFile.absolutePath,
    )
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

kover {
    reports {
        filters {
            excludes {
                packages(
                    "com.stytch.sdk.consumer.networking.models",
                    "com.stytch.sdk.consumer.networking.api",
                )
                classes("com.stytch.sdk.consumer.BuildConfig")
            }
        }
    }
}

val generatedSourcesPath = "${layout.buildDirectory.dir("generated/openapi").get()}"
val resourcesDir = layout.projectDirectory.dir("../resources")
val apiDescriptionFile = "${resourcesDir.file("openapi.yml")}"
val templatesDir = resourcesDir.dir("templates")
openApiGenerate {
    verbose.set(false)
    validateSpec.set(false)
    skipValidateSpec.set(true)
    generateApiTests.set(false)
    generateModelTests.set(false)
    generateApiDocumentation.set(false)
    generateModelDocumentation.set(false)
    generatorName.set("kotlin")
    inputSpec.set(apiDescriptionFile)
    outputDir.set(generatedSourcesPath)
    apiPackage.set("com.stytch.sdk.consumer.networking.api")
    modelPackage.set("com.stytch.sdk.consumer.networking.models")
    templateDir.set("$templatesDir")
    configOptions.set(
        mapOf(
            "library" to "jvm-retrofit2",
            "explicitApi" to "true",
            "sortParamsByRequiredFlag" to "true",
            "omitGradleWrapper" to "true",
        ),
    )
    additionalProperties.set(
        mapOf(
            "dateLibrary" to "kotlinx-datetime",
            "serializationLibrary" to "kotlinx_serialization",
            "useCoroutines" to "true",
        ),
    )
    openapiNormalizer.set(
        mapOf(
            "NORMALIZER_CLASS" to "com.stytch.sdk.utils.StytchOpenAPINormalizer",
            "FILTER" to "path:!/b2b/",
        ),
    )
    globalProperties.set(
        mapOf(
            "models" to "",
            "apis" to "",
        ),
    )
    typeMappings.set(mapOf("AnyType" to "JsonElement"))
    importMappings.set(mapOf("JsonElement" to "kotlinx.serialization.json.JsonElement"))
}

tasks.withType<KspAATask>().configureEach {
    if (name != "openApiGenerate") {
        dependsOn("openApiGenerate")
        outputs.upToDateWhen { false }
    }
}
tasks.withType<KotlinCompileCommon>().configureEach {
    dependsOn("openApiGenerate")
}
tasks.withType<ProcessLibraryArtProfileTask>().configureEach {
    dependsOn("openApiGenerate")
}
tasks.named("compileKotlinMetadata") {
    dependsOn("openApiGenerate")
    dependsOn("kspCommonMainKotlinMetadata")
}
