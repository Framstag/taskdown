package com.framstag.taskdown.commands

import com.framstag.taskdown.config.getConfigPath
import com.framstag.taskdown.config.loadConfig
import com.framstag.taskdown.database.Database
import com.framstag.taskdown.database.NoValidDirectoryException
import com.framstag.taskdown.markdown.filehandler.markdownPropertyHandlerMap
import com.framstag.taskdown.markdown.filehandler.markdownHistoryHandlerMap
import com.framstag.taskdown.system.PhysicalFileSystem
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.ProgramResult
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.output.CliktHelpFormatter
import com.github.ajalt.mordant.terminal.Terminal
import java.nio.file.Path

class TaskDown : CliktCommand(name = "taskdown", printHelpOnEmptyArgs = true) {
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
            val config = loadConfig(getConfigPath())
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
