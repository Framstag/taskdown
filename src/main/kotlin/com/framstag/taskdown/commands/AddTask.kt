package com.framstag.taskdown.commands

import com.framstag.taskdown.database.Database
import com.framstag.taskdown.domain.Priority
import com.framstag.taskdown.domain.Task
import com.framstag.taskdown.domain.TaskAttributes
import com.framstag.taskdown.domain.TaskHistory
import com.framstag.taskdown.system.TaskListFormatter
import com.framstag.taskdown.system.callEditorForFile
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.choice
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AddTask : CliktCommand(name = "add", help = "Add a new task") {
    private val database by requireObject<Database>()

    // Options
    private val priority by option(help = "Priority of the task, either 'A', 'B' or 'C'").choice("A", "B", "C").default("C")
    private val tag by option(help="List of tags to assign to the task").multiple().unique()
    private val due : LocalDate? by option(help="Optional due date").convert {
        LocalDate.parse(it, DateTimeFormatter.BASIC_ISO_DATE)
    }
    private val log by option(help="Initial log description")
    private val edit by option("--edit", "-e", help="edit task after creation").flag()

    // Arguments
    private val title by argument(help = "Task title")

    private fun editTask(task : Task) {
        val filenamePath = database.getPathForActiveTask(task)

        if (callEditorForFile(filenamePath)) {
            val reloadedTask = database.reloadTask(task)

            val formatter = TaskListFormatter()

            println(formatter.format(reloadedTask))
        }
        else {
            System.err.println("ERROR: Error while calling external editor")
        }
    }

    override fun run() {
        val tasks = database.loadTasks().sortedBy { it.attributes.id }

        val taskId = Task.getNextFreeTaskId(tasks)

        var attributes = TaskAttributes(taskId)
        var history = TaskHistory()

        val parsedPriority = Priority.valueOf(priority)

        attributes = attributes.withPriority(parsedPriority)
        attributes = attributes.withTags(tag)
        attributes = attributes.withCreationDate(LocalDate.now())

        due?.let {
            attributes = attributes.withDueDate(it)
        }

        log?.let {
            history = history.withLog(LocalDateTime.now(),it)
        }

        val task = database.createTask(Task("",title,attributes, history, ""))

        val formatter = TaskListFormatter()

        println(formatter.format(task))

        if (edit) {
            editTask(task)
        }
    }
}
