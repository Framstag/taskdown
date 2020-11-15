package com.framstag.taskdown.config

import com.beust.klaxon.Klaxon
import java.nio.file.Files
import java.nio.file.Path

fun getConfigPath(): Path {
    return when (System.getProperty("os.name")) {
        "Linux" -> Path.of(System.getProperty("user.home"), ".config", "taskdown.json")
        else -> Path.of("taskdown.json")
    }
}

fun loadConfig(configPath: Path): Config {
    val configData = Files.readString(configPath)
    val config = Klaxon().parse<Config>(configData)

    return if (config!=null) return config else Config()
}
