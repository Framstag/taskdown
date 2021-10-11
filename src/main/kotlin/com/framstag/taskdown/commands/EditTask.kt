package com.framstag.taskdown.commands

import com.framstag.taskdown.database.Database
import com.framstag.taskdown.domain.Task
import com.framstag.taskdown.system.TaskListFormatter
import com.framstag.taskdown.system.callEditorForFile
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.mordant.terminal.Terminal

class EditTask : CliktCommand(name="edit", help="edit an existing task", printHelpOnEmptyArgs = true) {
    private val context by requireObject<Context>()

    // Arguments
    private val id : Int by argument(help="Id of the task").int()

    private fun editTask(database : Database, task : Task) {
        val filenamePath = database.getPathForActiveTask(task)

        if (callEditorForFile(filenamePath)) {
            val reloadedTask = database.reloadTask(task)

            val t = Terminal()
            val formatter = TaskListFormatter()

            t.println(formatter.format(reloadedTask))
        }
        else {
            System.err.println("ERROR: Error while calling external editor")
        }
    }

    override fun run() {
        val taskMap = context.database.loadTasks().associateBy {
            it.attributes.id
        }

        val task = taskMap.getOrElse(id) {
            System.err.println("Task with id $id not found")

            return
        }

        editTask(context.database, task)
    }

}