package com.framstag.taskdown.commands

import com.framstag.taskdown.database.Database
import com.framstag.taskdown.domain.Task
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument

class SearchTasks : CliktCommand(name="search", help="search all tasks that match search text") {
    // Arguments
    private val text : String by argument(help="Text to search for")

    private val database by requireObject<Database>()

    private fun showTask(task : Task) {
        println(task.toFormattedString())
    }

    override fun run() {
        database.loadTasks().sortedBy {
            it.attributes.id
        }.filter {
            it.title.contains(text, ignoreCase = true) || it.body.contains(text, ignoreCase = true)
        }.onEach {
            showTask(it)
        }
    }
}