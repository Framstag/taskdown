package com.framstag.taskdown.commands

import com.framstag.taskdown.database.Database
import com.framstag.taskdown.domain.TaskByAgeComparator
import com.framstag.taskdown.domain.TaskByIdComparator
import com.framstag.taskdown.domain.TaskByPriorityComparator
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.unique
import com.github.ajalt.clikt.parameters.types.choice

class ListTasks : CliktCommand(name = "list", help = "List existing tasks") {
    private val database by requireObject<Database>()

    private val tag by option(help = "Tags to filter the tasks").multiple().unique()
    private val priority by option(help = "Priority to filter the tasks").choice("A", "B", "C").multiple().unique()

    override fun run() {
        var tasks = database.loadTasks()

        tag.forEach { tag ->
            tasks = tasks.filter { task ->
                task.attributes.tags.contains(tag)
            }
        }

        if (priority.isNotEmpty()) {
            tasks = tasks.filter { task ->
                priority.contains(task.attributes.priority.name)
            }
        }

        tasks = tasks.sortedWith(
            TaskByPriorityComparator()
                .then(TaskByAgeComparator())
                .then(TaskByIdComparator())
        )

        tasks.forEach { task ->
            println(task.toFormattedString())
        }
    }
}
