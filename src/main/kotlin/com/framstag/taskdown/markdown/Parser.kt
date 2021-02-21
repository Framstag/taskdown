package com.framstag.taskdown.markdown

import com.framstag.taskdown.database.FileFormatException
import com.framstag.taskdown.domain.Task
import com.framstag.taskdown.domain.TaskAttributes
import java.nio.file.Path

fun fileContentToTaskDocument(filename: Path, content: String): TaskDocument {
    // Find document title
    var titleStart = content.indexOf("#")

    if (titleStart < 0) {
        throw FileFormatException(filename,"Cannot find top level title")
    }

    // Advance behind the '#'
    titleStart += 1

    var titleEnd = content.indexOf(System.lineSeparator(), titleStart + 1)

    if (titleEnd<0) {
        titleEnd = content.length
    }

    // Try to find the task sub-section
    val taskStart = content.indexOf(ATTRIBUTE_SECTION_TASK, titleEnd + 1)

    if (taskStart < 0) {
        throw FileFormatException(filename,"Cannot find 'Task' sub-section")
    }

    // Now move through the task section to find its end
    var taskCursor = taskStart + ATTRIBUTE_SECTION_TASK.length

    // Try to find optional properties sub-sub-section
    val taskProperties = content.indexOf(ATTRIBUTE_SECTION_TASK_PROPERTIES,taskCursor)

    if (taskProperties >= 0) {
        taskCursor = taskProperties + ATTRIBUTE_SECTION_TASK_PROPERTIES.length
    }

    // Jump to next sub-section after task sub-section
    var taskEnd = content.indexOf(ATTRIBUTE_SECTION_2, taskCursor)

    if (taskEnd < 0) {
        taskEnd = content.length
    }

    return TaskDocument(
        filename,
        content.substring(titleStart, titleEnd).trim(),
        content.substring(taskStart, taskEnd),
        content.substring(taskEnd, content.length)
    )
}

private fun taskSectionToTaskAttributes(
    filename: Path,
    section: String,
    handlerMap: Map<String, AttributeFileHandler>
): TaskAttributes {

    var attributes = TaskAttributes(0)

    val propertiesStart = section.indexOf(ATTRIBUTE_SECTION_TASK_PROPERTIES)

    if (propertiesStart < 0) {
        throw FileFormatException(filename,"Cannot find 'Task' sub-section 'Properties'")
    }

    val tableStart = section.indexOf("|",propertiesStart+ ATTRIBUTE_SECTION_TASK_PROPERTIES.length)

    if (tableStart < 0) {
        throw FileFormatException(filename,"Cannot find 'Task' attribute table")
    }

    // Table header
    var tableRowStart = section.indexOf(System.lineSeparator(),tableStart)

    // Table header divider
    tableRowStart = section.indexOf(System.lineSeparator(),tableRowStart+System.lineSeparator().length)

    // Start of first value row
    tableRowStart = section.indexOf("|", tableRowStart+System.lineSeparator().length)

    while (tableRowStart >= 0) {
        val column1Start = tableRowStart
        val column1End = section.indexOf("|", column1Start + 1)

        val keyName = section.substring(column1Start + 1, column1End).trim()

        val column2Start = column1End
        val column2End = section.indexOf("|", column2Start + 1)

        val value = section.substring(column2Start + 1, column2End).trim()

        val handler = handlerMap[keyName]

        if (handler != null) {
            attributes = handler.fileValueToAttribute(filename,value,attributes)
        }
        else {
            throw FileFormatException(filename,"Unknown task attribute '$keyName'")
        }

        tableRowStart = section.indexOf("|", column2End+1)
    }

    return attributes
}

private fun headerToTitle(header: String): String {
    return header.substring(1).trim()
}

private fun textBlockToTask(taskDocument: TaskDocument, handlerMap: Map<String, AttributeFileHandler>): Task {
    val title = headerToTitle(taskDocument.title)
    val attributes = taskSectionToTaskAttributes(taskDocument.filename, taskDocument.taskDescription, handlerMap)

    return Task(taskDocument.filename.fileName.toString(), title, attributes,taskDocument.body)
}

fun parseTask(filename: Path, fileContent: String, handlerMap: Map<String, AttributeFileHandler>): Task {
    val textBlock = fileContentToTaskDocument(filename, fileContent)
    return textBlockToTask(textBlock, handlerMap)
}
