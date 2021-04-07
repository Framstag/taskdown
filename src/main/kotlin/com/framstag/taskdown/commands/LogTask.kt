package com.framstag.taskdown.commands

import com.framstag.taskdown.database.Database
import com.framstag.taskdown.domain.Task
import com.framstag.taskdown.system.TaskListFormatter
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.int
import java.time.LocalDateTime

class LogTask : CliktCommand(name="log", help="Add log entry to existing task") {
    // Options

    // Arguments
    private val id : Int by argument(help="Id of the task").int()
    private val log : String by argument(help="Log entry")

    private val database by requireObject<Database>()

    private fun updateTask(task : Task):Task {
        var updatedTask = task

        log.let {
            updatedTask = updatedTask.withHistory(updatedTask.history.withLog(LocalDateTime.now(),it))
        }

        database.backupTask(updatedTask)

        return database.updateTask(updatedTask)
    }

    override fun run() {
        val taskMap = database.loadTasks().associateBy {
            it.attributes.id
        }

        val task = taskMap.getOrElse(id) {
            System.err.println("Task with id $id not found")

            return
        }

        val updatedTask = updateTask(task)

        val formatter = TaskListFormatter()

        println(formatter.format(updatedTask))
    }
}
