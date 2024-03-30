val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val wiremockVersion: String by project
val jsonSmartVersion: String by project
val guavaVersion: String by project

plugins {
    application
    kotlin("jvm") version "1.9.23"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}


group = "com.wmockserver"
version = "0.0.1"

application {
    mainClass.set("com.wmockserver.ApplicationKt")
}

repositories {
    mavenCentral()
}

tasks {
    shadowJar {
        manifest {
            attributes(Pair("Main-Class", "com.wmockserver.ApplicationKt"))
        }
    }
}

kotlin.sourceSets.all {
    languageSettings.optIn("kotlin.RequiresOptIn")
}

dependencies {
    // ktor-server
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-cors:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-sessions:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    // ktor-client
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-encoding:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    // ktor-utils
    implementation("io.ktor:ktor-serialization-gson:$ktorVersion")
    implementation("io.ktor:ktor-serialization-jackson:$ktorVersion")
    // other
    implementation("com.google.guava:guava:$guavaVersion")
    implementation("net.minidev:json-smart:$jsonSmartVersion")
    implementation("org.wiremock:wiremock:$wiremockVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    // test
    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
}