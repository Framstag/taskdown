package com.framstag.taskdown.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.types.int

class DeleteTask : CliktCommand(name="delete", help="Delete an existing task", printHelpOnEmptyArgs = true) {
    private val context by requireObject<Context>()

    private val id by argument(help="Id of the task").int().multiple()

    override fun run() {
        val taskMap = context.database.loadTasks().associateBy {
            it.attributes.id
        }

        id.forEach {
            val task = taskMap.getOrElse(it) {
                System.err.println("Task with id $id not found")

                return
            }

            context.database.deleteTask(task)
        }
    }
}
