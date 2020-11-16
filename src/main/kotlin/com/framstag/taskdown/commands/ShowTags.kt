package com.framstag.taskdown.commands

import com.framstag.taskdown.database.Database
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject

class ShowTags : CliktCommand(name="tags", help="List all tags") {
    private val database by requireObject<Database>()

    override fun run() {
        val tags = mutableSetOf<String>()
        val tasks = database.loadTasks()

        tasks.forEach {
            tags.addAll(it.attributes.tags)
        }

        tags.toList().sorted().forEach {
            println("* $it")
        }
    }
}
