plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.ktorfit) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.android.lint) apply false
    alias(libs.plugins.buildconfig) apply false
    alias(libs.plugins.skie) apply false
}

group = "com.stytch.sdk"
version = "0.0.1"
