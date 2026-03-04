plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.ktorfit) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.android.lint) apply false
    alias(libs.plugins.buildconfig) apply false
    alias(libs.plugins.skie) apply false
    alias(libs.plugins.openapi) apply false
    alias(libs.plugins.ktlint) apply false
}

group = "com.stytch.sdk"
version = "0.0.1"

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    extensions.configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        version.set("1.5.0")
        filter {
            exclude { it.file.absolutePath.contains("/build/generated/") }
        }
    }
    afterEvaluate {
        tasks.matching { it.name.lowercase().contains("ktlint") }.configureEach {
            mustRunAfter(tasks.matching { it.name.startsWith("ksp") })
        }
    }
}
