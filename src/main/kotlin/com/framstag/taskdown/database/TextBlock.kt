package com.framstag.taskdown.database

import java.nio.file.Path

data class TextBlock(val filename : Path, val header : String="", val taskDescription : String="", val footer : String="") {

    fun withHeader(header : String):TextBlock {
        return this.copy(header = header)
    }

    fun withTaskDescription(taskDescription: String):TextBlock {
        return this.copy(taskDescription = taskDescription)
    }
}