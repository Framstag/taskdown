package com.framstag.taskdown.commands

import com.framstag.taskdown.config.getConfigPath
import com.framstag.taskdown.config.loadConfig
import com.framstag.taskdown.database.Database
import com.framstag.taskdown.database.NoValidDirectoryException
import com.framstag.taskdown.database.filehandler.fileHandlerMap
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.ProgramResult
import com.github.ajalt.clikt.core.findOrSetObject
import java.nio.file.Path

class TaskDown : CliktCommand() {

    private val config by findOrSetObject { loadConfig(getConfigPath()) }
    private val database by findOrSetObject {
        Database(
            Path.of(config.databaseDir),
            Path.of(config.archiveDir),
            fileHandlerMap
        )
    }

    override fun run() {
        try {
            database.validate()
        }
        catch (e : NoValidDirectoryException) {
            System.err.println("ERROR: ${e.message}")

            throw ProgramResult(1)
        }
    }
}
