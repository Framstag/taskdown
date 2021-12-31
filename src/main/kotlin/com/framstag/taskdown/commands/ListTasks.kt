package com.framstag.taskdown.commands

import com.framstag.taskdown.domain.TaskByAgeComparator
import com.framstag.taskdown.domain.TaskByIdComparator
import com.framstag.taskdown.domain.TaskByPriorityComparator
import com.framstag.taskdown.system.TaskListFormatter
import com.framstag.taskdown.system.performanceDebugger
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.unique
import com.github.ajalt.clikt.parameters.types.choice

class ListTasks : CliktCommand(name = "list", help = "List existing tasks") {
    private val context by requireObject<Context>()

    private val tag by option("--tag", "-t", help = "Tags to filter the tasks").multiple().unique()
    private val priority by option("--priority", "-p", help = "Priority to filter the tasks").choice("A", "B", "C").multiple().unique()

    override fun run() {
        performanceDebugger.step("Loading tasks...")
        var tasks = context.database.loadTasks()
        performanceDebugger.step("Loading tasks... done")

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

        val formatter = TaskListFormatter()

        tasks.forEach { task ->
            context.t.println(formatter.format(task))
        }
    }
}
