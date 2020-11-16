package com.framstag.taskdown.commands

import com.framstag.taskdown.database.Database
import com.framstag.taskdown.domain.Priority
import com.framstag.taskdown.domain.Task
import com.framstag.taskdown.domain.TaskAttributes
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.unique
import com.github.ajalt.clikt.parameters.types.choice

class AddTask : CliktCommand(name = "add", help = "Add a new task") {
    private val database by requireObject<Database>()

    // Options
    private val priority by option().choice("A", "B", "C")
    private val tag by option().multiple().unique()

    // Arguments
    private val title by argument(help = "Task title")

    override fun run() {
        val tasks = database.loadTasks().sortedBy { it.attributes.id }

        val taskId = Task.getNextFreeTaskId(tasks)

        var attributes = TaskAttributes(taskId)

        if (priority != null) {
            val parsedPriority = Priority.valueOf(priority!!)

            attributes = attributes.withPriority(parsedPriority)
        }

        attributes = attributes.withTags(tag)

        val task = database.createTask(Task("",title,attributes))

        println(task.toFormattedString())
    }
}
