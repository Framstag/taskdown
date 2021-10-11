package com.framstag.taskdown.commands

import com.framstag.taskdown.database.Database
import com.framstag.taskdown.domain.Task
import com.framstag.taskdown.system.TaskListFormatter
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.mordant.terminal.Terminal
import java.time.LocalDateTime

class LogTask : CliktCommand(name="log", help="Add log entry to existing task", printHelpOnEmptyArgs = true) {
    private val context by requireObject<Context>()

    // Options

    // Arguments
    private val id : Int by argument(help="Id of the task").int()
    private val log : String by argument(help="Log entry")

    private fun updateTask(database : Database, task : Task):Task {
        var updatedTask = task

        log.let {
            updatedTask = updatedTask.withHistory(updatedTask.history.withLog(LocalDateTime.now(),it))
        }

        database.backupTask(updatedTask)

        return database.updateTask(updatedTask)
    }

    override fun run() {
        val taskMap = context.database.loadTasks().associateBy {
            it.attributes.id
        }

        val task = taskMap.getOrElse(id) {
            System.err.println("Task with id $id not found")

            return
        }

        val updatedTask = updateTask(context.database, task)

        val t = Terminal()
        val formatter = TaskListFormatter()

        t.println(formatter.format(updatedTask))
    }
}
