plugins {
    id("org.jetbrains.kotlin.jvm") version "1.4.21"

    application
}

repositories {
    jcenter()
}

// compile bytecode to java 8 (default is java 6)
tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_15.toString()
    }

    compileTestKotlin {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_15.toString()
    }
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Logging
    implementation("io.github.microutils:kotlin-logging-jvm:2.0.4")
    implementation("ch.qos.logback:logback-classic:1.2.3")

    // Command line parsing
    implementation("com.github.ajalt.clikt:clikt-jvm:3.1.0")

    // Terminal colors
    implementation("com.github.ajalt:mordant:1.2.1")


    // JSON Config file loading
    //    See: https://github.com/cbeust/klaxon
    implementation("com.beust:klaxon:5.4")

    // Kotlin JUnit Tests
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.7.0")
}

application {
    mainClass.set("com.framstag.taskdown.TaskDownKt")
}
