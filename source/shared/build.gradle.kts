plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.ktorfit) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.android.lint) apply false
    alias(libs.plugins.buildconfig) apply false
    alias(libs.plugins.skie) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.kover) apply false
    alias(libs.plugins.mavenPublish) apply false
}

group = "com.stytch.sdk"
version = file("../../version.txt").readText().trim()

subprojects {
    plugins.withId("com.vanniktech.maven.publish") {
        configure<com.vanniktech.maven.publish.MavenPublishBaseExtension> {
            publishToMavenCentral()
            signAllPublications()
            pom {
                url.set("https://github.com/stytchauth/stytch-mobile")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("stytchauth")
                        name.set("Stytch")
                        url.set("https://github.com/stytchauth")
                    }
                }
                scm {
                    url.set("https://github.com/stytchauth/stytch-mobile")
                    connection.set("scm:git:git://github.com/stytchauth/stytch-mobile.git")
                    developerConnection.set("scm:git:ssh://git@github.com/stytchauth/stytch-mobile.git")
                }
            }
        }
    }
}

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
        tasks.matching { it.name.startsWith("kover") }.configureEach {
            mustRunAfter(tasks.matching { it.name.startsWith("ksp") })
        }
    }
}
