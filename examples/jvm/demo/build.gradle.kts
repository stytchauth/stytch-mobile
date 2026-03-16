plugins {
    id("java")
    application
}

group = "com.stytch.mobile"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

application {
    mainClass.set("com.stytch.mobile.Main")
}

dependencies {
    // consumer-headless-extensions re-exports consumer-headless, so we only need one dependency.
    // It adds callback-style (onSuccess/onFailure) overloads for every suspend function, which
    // lets Java callers avoid raw Kotlin coroutine machinery.
    implementation("com.stytch.sdk:consumer-headless-extensions:0.0.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.3.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    // kotlinx-coroutines-swing makes Dispatchers.Main == the Swing EDT, which the SDK uses for
    // authenticationStateObserver callbacks — so those arrive on the right thread automatically.
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.10.2")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}