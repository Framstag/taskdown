package com.framstag.taskdown.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject

class ShowTags : CliktCommand(name="tags", help="List all tags") {
    private val context by requireObject<Context>()

    override fun run() {
        val tags = mutableSetOf<String>()
        val tasks = context.database.loadTasks()

        tasks.forEach {
            tags.addAll(it.attributes.tags)
        }

        tags.toList().sorted().forEach {
            println("* $it")
        }
    }
}
