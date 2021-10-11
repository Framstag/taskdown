package com.framstag.taskdown.commands

import com.framstag.taskdown.system.TaskListFormatter
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.mordant.terminal.Terminal

class SearchTasks : CliktCommand(name="search", help="search all tasks that match search text", printHelpOnEmptyArgs = true) {
    private val context by requireObject<Context>()

    // Arguments
    private val text : String by argument(help="Text to search for")

    override fun run() {
        val t = Terminal()
        val formatter = TaskListFormatter()

        context.database.loadTasks().sortedBy {
            it.attributes.id
        }.filter {
            it.title.contains(text, ignoreCase = true) || it.body.contains(text, ignoreCase = true)
        }.onEach {
            t.println(formatter.format(it))
        }
    }
}