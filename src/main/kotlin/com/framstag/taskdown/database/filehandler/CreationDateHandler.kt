package com.framstag.taskdown.database.filehandler

import com.framstag.taskdown.database.FileFormatException
import com.framstag.taskdown.markdown.AttributeFileHandler
import com.framstag.taskdown.domain.TaskAttributes
import java.lang.RuntimeException
import java.nio.file.Path
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CreationDateHandler : AttributeFileHandler {
    override fun fileValueToAttribute(file: Path, value: String, current: TaskAttributes): TaskAttributes {
        try {
            val creationDate = LocalDate.parse(value)

            return current.withCreationDate(creationDate)
        }
        catch (e: RuntimeException) {
            throw FileFormatException(file, "Attribute '${NAME}' must have a value in ISO date format, but has value '$value")
        }
    }

    override fun attributeToKeyValue(current: TaskAttributes): Pair<String, String> {

        return if (current.creationDate != null) {
            Pair(NAME, current.creationDate.format(DateTimeFormatter.ISO_LOCAL_DATE))
        } else {
            Pair(NAME, "")
        }
    }

    companion object {
        const val NAME = "CreationDate"
    }
}