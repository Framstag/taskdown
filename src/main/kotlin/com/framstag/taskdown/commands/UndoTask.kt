package com.framstag.taskdown.commands

import com.framstag.taskdown.database.Database
import com.framstag.taskdown.domain.Task
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.int

class UndoTask : CliktCommand(name="undo", help="Revert to task backup", printHelpOnEmptyArgs = true) {
    // Arguments
    private val id : Int by argument(help="Id of the task").int()

    private val database by requireObject<Database>()

    private fun undoTask(task : Task) {
        //println(Files.readString(task.filename))
    }

    override fun run() {

        val taskMap = database.loadTasks().associateBy {
            it.attributes.id
        }

        val task = taskMap.getOrElse(id) {
            System.err.println("Task with id $id not found")

            return
        }

        undoTask(task)
    }

}