package com.framstag.taskdown.commands

import com.framstag.taskdown.database.Database
import com.framstag.taskdown.domain.Task
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.unique
import com.github.ajalt.clikt.parameters.types.choice

class ShowHashes : CliktCommand(name="hashes", help="List all hashes") {
    private val database by requireObject<Database>()

    override fun run() {
        val hashes = mutableSetOf<String>()
        val tasks = database.loadTasks()

        tasks.forEach {
            hashes.addAll(it.attributes.hashes)
        }

        hashes.toList().sorted().forEach {
            println("* $it")
        }
    }
}
