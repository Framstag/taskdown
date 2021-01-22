package com.framstag.taskdown.database

import java.nio.file.Path

data class TextBlock(val filename : Path, val title : String="", val taskDescription : String="", val body : String="") {

    fun withHeader(header : String):TextBlock {
        return this.copy(title = header)
    }

    fun withTaskDescription(taskDescription: String):TextBlock {
        return this.copy(taskDescription = taskDescription)
    }
}