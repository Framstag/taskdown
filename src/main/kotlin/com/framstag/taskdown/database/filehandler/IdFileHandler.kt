package com.framstag.taskdown.database.filehandler

import com.framstag.taskdown.markdown.AttributeFileHandler
import com.framstag.taskdown.database.FileFormatException
import com.framstag.taskdown.domain.TaskAttributes
import java.nio.file.Path

class IdFileHandler : AttributeFileHandler {
    override fun fileValueToAttribute(file: Path, value: String, current: TaskAttributes): TaskAttributes {
        try {
            val id = Integer.parseInt(value)

            if (id <= 0) {
                throw FileFormatException(file, "Attribute '$NAME' must be >0, but has value '$id")
            }

            return current.copy(id = id)
        }
        catch (e : NumberFormatException) {
            throw FileFormatException(file, "Value of attribute '$NAME' must be numeric")
        }
    }

    override fun attributeToKeyValue(current: TaskAttributes): Pair<String,String> {
        return Pair(NAME,current.id.toString())
    }

    companion object {
        const val NAME = "Id"
    }
}