package com.framstag.taskdown.commands

import com.framstag.taskdown.domain.Task
import com.github.ajalt.mordant.terminal.Terminal
import com.github.ajalt.mordant.rendering.TextColors.*
import com.github.ajalt.mordant.rendering.TextStyles.*
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.mordant.markdown.Markdown
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit

class ShowTask : CliktCommand(name="show", help="show an existing task", printHelpOnEmptyArgs = true) {
    private val context by requireObject<Context>()

    // Arguments
    private val id : Int by argument(help="Id of the task").int()

    private fun showTask(task : Task) {
        val t = Terminal()

        t.println(red(bold("# ${task.title}")))
        t.println()
        t.println("Id:       ${yellow(task.attributes.id.toString())}")
        t.println("Priority: ${yellow(task.attributes.priority.toString())}")

        task.attributes.creationDate?.let {
            t.println("Creation: ${yellow(it.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)))}")
        }

        task.attributes.dueDate?.let {
            t.println("Due:      ${yellow(it.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)))}")
        }

        t.println("Tags:     ${yellow(task.attributes.tagString())}")
        t.println()

        if (task.history.logs.isNotEmpty()) {
            t.println("Logs:")
            t.println()

            task.history.logs.sortedBy {
                it.dateTime
            }.forEach {
                val dateTimeString = it.dateTime.truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM))
                t.println("* $dateTimeString: ${yellow(it.description)}")
            }
        }

        val widget = Markdown(task.body)
        t.println(widget)
    }

    override fun run() {
        val taskMap = context.database.loadTasks().associateBy {
            it.attributes.id
        }

        val task = taskMap.getOrElse(id) {
            System.err.println("Task with id $id not found")

            return
        }

        showTask(task)
    }
}