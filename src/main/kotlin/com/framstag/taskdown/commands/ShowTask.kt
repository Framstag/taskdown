package com.framstag.taskdown.commands

import com.framstag.taskdown.database.Database
import com.framstag.taskdown.domain.Task
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.mordant.TermColors
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit

class ShowTask : CliktCommand(name="show", help="show an existing task") {
    // Arguments
    private val id : Int by argument(help="Id of the task").int()

    private val database by requireObject<Database>()

    private fun showTask(task : Task) {
        val t = TermColors()

        println(t.red(t.bold("# ${task.title}")))
        println()
        println("Id:       ${t.yellow(task.attributes.id.toString())}")
        println("Priority: ${t.yellow(task.attributes.priority.toString())}")

        task.attributes.creationDate?.let {
            println("Creation: ${t.yellow(it.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)))}")
        }

        task.attributes.dueDate?.let {
            println("Due:      ${t.yellow(it.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)))}")
        }

        println("Tags:     ${t.yellow(task.attributes.tagString())}")
        println()

        if (task.history.logs.isNotEmpty()) {
            println("Logs:")
            println()

            task.history.logs.sortedBy {
                it.dateTime
            }.forEach {
                val dateTimeString = it.dateTime.truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM))
                println("* $dateTimeString: ${t.yellow(it.description)}")
            }
        }

        println(task.body)
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