package com.framstag.taskdown.config

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.nio.file.Files
import java.nio.file.Path

fun getConfigPath(): Path {
    return when (System.getProperty("os.name")) {
        "Linux" -> Path.of(System.getProperty("user.home"), ".config", "taskdown.json")
        "Windows 10" -> Path.of(System.getProperty("user.home"), "taskdown.json")
        else -> Path.of("taskdown.json")
    }
}

fun loadConfig(configPath: Path): Config {
    val configData = Files.readString(configPath)

    val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()
    val adapter = moshi.adapter(Config::class.java)
    val config = adapter.fromJson(configData)

    return if (config!=null) return config else Config()
}
