package com.framstag.taskdown.commands

import com.framstag.taskdown.database.Database
import com.framstag.taskdown.domain.Task
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.unique
import com.github.ajalt.clikt.parameters.types.choice

class ListTasks : CliktCommand(name="list", help="List existing tasks") {
    private val database by requireObject<Database>()

    private val hash by option().multiple().unique()
    private val priority by option().choice("A","B","C").multiple().unique()

    override fun run() {
        var tasks = database.loadTasks()

        hash.forEach { hash ->
           tasks = tasks.filter {task ->
               task.attributes.hashes.contains(hash)
           }
        }

        if (priority.isNotEmpty()) {
            tasks = tasks.filter { task ->
                priority.contains(task.attributes.priority.name)
            }
        }

        tasks = tasks.sortedWith(compareBy<Task> {
            it.attributes.priority
        }.thenBy {
            it.attributes.id
        })

        tasks.forEach {task ->
            println(task.toFormattedString())
        }
    }
}
