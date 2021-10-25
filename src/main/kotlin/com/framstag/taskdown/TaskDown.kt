package com.framstag.taskdown

import com.framstag.taskdown.commands.*
import com.github.ajalt.clikt.core.*
import io.github.alexarchambault.windowsansi.WindowsAnsi

fun main(args: Array<String>) {
     val isWindows = System.getProperty("os.name")
        .lowercase()
        .contains("windows")

    if (isWindows) {
      WindowsAnsi.setup()        
    }

    TaskDown()
        .subcommands(
            AddTask(),
            ArchiveTask(),
            DeleteTask(),
            EditTask(),
            SearchTasks(),
            ShowTags(),
            ListTasks(),
            LogTask(),
            ShowTask(),
            UpdateTask(),
            UndoTask()
        )
        .main(args)
}