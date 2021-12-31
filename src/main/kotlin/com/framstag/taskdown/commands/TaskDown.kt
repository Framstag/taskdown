package com.framstag.taskdown.commands

import com.framstag.taskdown.config.getConfigPath
import com.framstag.taskdown.config.loadConfig
import com.framstag.taskdown.database.Database
import com.framstag.taskdown.database.NoValidDirectoryException
import com.framstag.taskdown.markdown.filehandler.markdownPropertyHandlerMap
import com.framstag.taskdown.markdown.filehandler.markdownHistoryHandlerMap
import com.framstag.taskdown.system.PhysicalFileSystem
import com.framstag.taskdown.system.performanceDebugger
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.ProgramResult
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.output.CliktHelpFormatter
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.mordant.terminal.Terminal
import java.nio.file.Path

class TaskDown : CliktCommand(name = "taskdown", printHelpOnEmptyArgs = true) {
    // We parse this option directly from the main(args) parameters to start measuring before
    // Clikt gets initialized, but we have to make Clikt aware of this argument, so we define
    // but do not evaluate it here, too.
    private val debugPerformance by option("--debugPerformance", help="print performance information").flag()

    init {
        context { helpFormatter = CliktHelpFormatter(showDefaultValues = true, requiredOptionMarker ="*") }
    }

    override fun aliases(): Map<String, List<String>> {
        return mapOf(
            "a" to listOf("add"),
            "e" to listOf("edit"),
            "l" to listOf("list"),
            "rm" to listOf("delete"),
            "s" to listOf("show"),
            "t" to listOf("tags"),
            "u" to listOf("update"),
            "help" to listOf("","-h")
        )
    }

    override fun run() {
        try {
            performanceDebugger.step("Loading configuration...")
            val config = loadConfig(getConfigPath())
            performanceDebugger.step("Loading configuration done")
            val database = Database(
                PhysicalFileSystem(),
                Path.of(config.databaseDir),
                Path.of(config.archiveDir),
                markdownPropertyHandlerMap,
                markdownHistoryHandlerMap
            )

            database.validate()
            currentContext.findOrSetObject { Context(config,database, Terminal()) }
        } catch (e: NoValidDirectoryException) {
            System.err.println("ERROR: ${e.message}")

            throw ProgramResult(1)
        }
    }
}
