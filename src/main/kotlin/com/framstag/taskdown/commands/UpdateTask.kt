package com.framstag.taskdown.commands

import com.framstag.taskdown.database.Database
import com.framstag.taskdown.domain.Priority
import com.framstag.taskdown.domain.Task
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.int
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class UpdateTask : CliktCommand(name="update", help="Update an existing task") {
    // Options
    private val title by option(help ="Title of the task")
    private val priority by option(help = "Priority of the task, either 'A', 'B' or (default) 'C'").choice("A", "B", "C").default("C")
    private val tag by option(help="List of tags to assign to the task").multiple().unique()
    private val due : LocalDate? by option(help="Due date").convert {
        LocalDate.parse(it, DateTimeFormatter.BASIC_ISO_DATE)
    }
    // TODO: Make this an mutual exclusive option group to due
    private val nodue by option(help="Clear due date").flag()

    // Arguments
    private val id : Int by argument(help="Id of the task").int()

    private val database by requireObject<Database>()

    private fun updateTask(task : Task):Task {
        var updatedTask = task

        if (!title.isNullOrBlank()) {
            updatedTask = task.withTitle(title!!)
        }

        val parsedPriority = Priority.valueOf(priority)

        updatedTask = updatedTask.withAttributes(updatedTask.attributes.withPriority(parsedPriority))

        tag.forEach {
            updatedTask = if (it=="-") {
                updatedTask.withAttributes(updatedTask.attributes.withTags(setOf()))
            } else if (it.isNotEmpty() && it[0]=='-') {
                updatedTask.withAttributes(updatedTask.attributes.withoutTag(it.substring(1)))
            } else {
                updatedTask.withAttributes(updatedTask.attributes.withTag(it))
            }
        }

        if (nodue) {
            updatedTask = updatedTask.withAttributes(updatedTask.attributes.withNoDueDate())
        }
        else {
            due?.let {
                updatedTask = updatedTask.withAttributes(updatedTask.attributes.withDueDate(it))
            }
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

        println(updatedTask.toFormattedString())
    }
}
