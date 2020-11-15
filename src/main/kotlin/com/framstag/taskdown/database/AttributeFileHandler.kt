package com.framstag.taskdown.database

import com.framstag.taskdown.domain.TaskAttributes
import java.nio.file.Path

interface AttributeFileHandler {

    fun fileValueToAttribute(file: Path, value : String, current : TaskAttributes):TaskAttributes
    fun attributeToKeyValue(current : TaskAttributes):Pair<String,String>
}