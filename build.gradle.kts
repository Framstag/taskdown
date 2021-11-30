plugins {
    id("org.jetbrains.kotlin.jvm") version "1.6.0"

    application
}

repositories {
    mavenCentral()
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_16.toString()
    }

    compileTestKotlin {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_16.toString()
    }
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Logging
    implementation("io.github.microutils:kotlin-logging-jvm:2.0.11")
    implementation("ch.qos.logback:logback-classic:1.2.6")

    // Command line parsing
    implementation("com.github.ajalt.clikt:clikt-jvm:3.3.0")

    // Terminal colors
    implementation("io.github.alexarchambault.windows-ansi:windows-ansi:0.0.3")
    implementation("com.github.ajalt.mordant:mordant-jvm:2.0.0-beta2")


    // JSON Config file loading
    //    See: https://github.com/square/moshi
    implementation("com.squareup.moshi:moshi-kotlin:1.12.1")

    // Kotlin JUnit Tests
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")

    // JUnit 5
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.2")

    // Mockk Mock-Framework
    testImplementation("io.mockk:mockk:1.12.0")
}

tasks.test {
    useJUnitPlatform()

    testLogging {
        events("passed", "skipped", "failed")
    }
}
application {
    mainClass.set("com.framstag.taskdown.TaskDownKt")
}
