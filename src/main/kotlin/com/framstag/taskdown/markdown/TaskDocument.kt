package com.framstag.taskdown.markdown

import java.nio.file.Path

data class TaskDocument(
    val filename: Path,
    val title: String = "",
    val taskDescription: List<String> = listOf(),
    val body: String = ""
) {
    fun withHeader(header: String): TaskDocument {
        return this.copy(title = header)
    }

    fun withTaskDescription(taskDescription: List<String>): TaskDocument {
        return this.copy(taskDescription = taskDescription)
    }

    fun toFileContent():String {
        return title + taskDescription.joinToString(System.lineSeparator()) + body
    }
}
