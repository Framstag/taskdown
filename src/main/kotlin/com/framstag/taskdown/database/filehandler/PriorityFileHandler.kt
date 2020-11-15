package com.framstag.taskdown.database.filehandler

import com.framstag.taskdown.database.AttributeFileHandler
import com.framstag.taskdown.database.FileFormatException
import com.framstag.taskdown.domain.Priority
import com.framstag.taskdown.domain.TaskAttributes
import java.nio.file.Path

class PriorityFileHandler : AttributeFileHandler {
    override fun fileValueToAttribute(file: Path, value: String, current: TaskAttributes): TaskAttributes {
        try {
            val priority = when (value) {
                "A" -> Priority.A
                "B" -> Priority.B
                "C" -> Priority.C
                else -> throw FileFormatException(file, "Attribute '$NAME' must have a value of 'A', 'B' or 'C', but has value '$value")
            }

            return current.withPriority(priority)
        }
        catch (e : NumberFormatException) {
            throw FileFormatException(file, "Value of attribute '$NAME' must be numeric")
        }
    }

    override fun attributeToKeyValue(current: TaskAttributes): Pair<String,String> {
        return Pair(NAME,current.priority.toString())
    }

    companion object {
        const val NAME = "Priority"
    }
}