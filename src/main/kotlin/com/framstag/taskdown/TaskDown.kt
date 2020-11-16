package com.framstag.taskdown

import com.framstag.taskdown.commands.*
import com.github.ajalt.clikt.core.*

fun main(args: Array<String>) {
    TaskDown()
        .subcommands(
            AddTask(),
            ArchiveTask(),
            DeleteTask(),
            ShowHashes(),
            ListTasks(),
            ShowTask(),
            UpdateTask(),
            UndoTask()
        )
        .main(args)
}