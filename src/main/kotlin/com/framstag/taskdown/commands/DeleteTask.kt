package com.framstag.taskdown.commands

import com.framstag.taskdown.database.Database
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.types.int

class DeleteTask : CliktCommand(name="delete", help="Delete an existing task") {
    private val id by argument(help="Id of the task").int().multiple()

    private val database by requireObject<Database>()

    override fun run() {
        val taskMap = database.loadTasks().associateBy {
            it.attributes.id
        }

        id.forEach {
            val task = taskMap.getOrElse(it) {
                System.err.println("Task with id $id not found")

                return
            }

            database.deleteTask(task)
        }
    }
}
