package com.framstag.taskdown.markdown

import java.nio.file.Path

data class TaskDocument(
    val filename: Path,
    val title: String = "",
    val taskDescription: String = "",
    val body: String = ""
) {
    fun withHeader(header: String): TaskDocument {
        return this.copy(title = header)
    }

    fun withTaskDescription(taskDescription: String): TaskDocument {
        return this.copy(taskDescription = taskDescription)
    }
}