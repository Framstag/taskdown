package com.framstag.taskdown

import com.framstag.taskdown.commands.*
import com.framstag.taskdown.system.PerformanceConsoleDebugger
import com.framstag.taskdown.system.performanceDebugger
import com.github.ajalt.clikt.core.*
import io.github.alexarchambault.windowsansi.WindowsAnsi

fun main(args: Array<String>) {
     val isWindows = System.getProperty("os.name")
        .lowercase()
        .contains("windows")

    if (isWindows) {
      WindowsAnsi.setup()        
    }

    if (args.isNotEmpty() && args[0] == "--debugPerformance") {
        println("Activated performance debugging...")
        performanceDebugger = PerformanceConsoleDebugger()
    }

    performanceDebugger.step("Initialising Clikt...")

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