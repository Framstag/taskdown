package com.framstag.taskdown.commands

import com.framstag.taskdown.database.Database
import com.framstag.taskdown.domain.Task
import com.framstag.taskdown.system.callEditorForFile
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.int

class EditTask : CliktCommand(name="edit", help="edit an existing task") {
    // Arguments
    private val id : Int by argument(help="Id of the task").int()

    private val database by requireObject<Database>()

    private fun editTask(task : Task) {
        val filenamePath = database.getPathForActiveTask(task)

        if (callEditorForFile(filenamePath)) {
            val reloadedTask = database.reloadTask(task)

            println(reloadedTask.toFormattedString())
        }
        else {
            System.err.println("ERROR: Error while calling external editor")
        }
    }

    override fun run() {
        val taskMap = database.loadTasks().associateBy {
            it.attributes.id
        }

        val task = taskMap.getOrElse(id) {
            System.err.println("Task with id $id not found")

            return
        }

        editTask(task)
    }

}