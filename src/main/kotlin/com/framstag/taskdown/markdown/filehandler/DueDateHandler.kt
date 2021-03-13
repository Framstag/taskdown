package com.framstag.taskdown.markdown.filehandler

import com.framstag.taskdown.database.FileFormatException
import com.framstag.taskdown.markdown.AttributeFileHandler
import com.framstag.taskdown.domain.TaskAttributes
import java.lang.RuntimeException
import java.nio.file.Path
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DueDateHandler : AttributeFileHandler {
    override fun fileValueToAttribute(file: Path, value: String, current: TaskAttributes): TaskAttributes {
        try {
            if (value.isNotEmpty()) {
                val dueDate = LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE)

                return current.withDueDate(dueDate)
            }
            else {
                return current
            }
        }
        catch (e: RuntimeException) {
            throw FileFormatException(file, "Attribute '${NAME}' must have a value in ISO date format, but has value '$value")
        }
    }

    override fun attributeToKeyValue(current: TaskAttributes): Pair<String, String> {

        return if (current.dueDate != null) {
            Pair(NAME, current.dueDate.format(DateTimeFormatter.ISO_LOCAL_DATE))
        } else {
            Pair(NAME, "")
        }
    }

    companion object {
        const val NAME = "DueDate"
    }
}