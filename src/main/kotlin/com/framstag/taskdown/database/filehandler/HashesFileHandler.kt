package com.framstag.taskdown.database.filehandler

import com.framstag.taskdown.database.AttributeFileHandler
import com.framstag.taskdown.domain.TaskAttributes
import java.nio.file.Path

class HashesFileHandler : AttributeFileHandler {
    override fun fileValueToAttribute(file: Path, value: String, current: TaskAttributes): TaskAttributes {
        val hashes = value.split("#").map {
            it.trim()
        }.filter {
            it.isNotEmpty()
        }.toSet()

        return current.withHashes(hashes)
    }

    override fun attributeToKeyValue(current: TaskAttributes): Pair<String,String> {
        val hashString= current.hashes.joinToString(" ") {
            "#$it"
        }

        return Pair(NAME,hashString)
    }

    companion object {
        const val NAME = "Hashes"
    }
}