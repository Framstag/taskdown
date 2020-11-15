package com.framstag.taskdown.commands

import com.framstag.taskdown.database.Database
import com.framstag.taskdown.domain.Task
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.int
import java.nio.file.Files

class ShowTask : CliktCommand(name="show", help="show an existing task") {
    // Arguments
    val id : Int by argument(help="Id of the task").int()

    private val database by requireObject<Database>()

    private fun showTask(task : Task) {
        println(Files.readString(task.filename))
    }

    override fun run() {

        val taskMap = database.loadTasks().associateBy {
            it.attributes.id
        }

        val task = taskMap.getOrElse(id) {
            System.err.println("Task with id $id not found")

            return
        }

        showTask(task)
    }

}