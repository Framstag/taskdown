package com.framstag.taskdown.markdown

import com.framstag.taskdown.database.FileFormatException
import com.framstag.taskdown.domain.Task
import com.framstag.taskdown.domain.TaskAttributes
import com.framstag.taskdown.domain.TaskHistory
import java.nio.file.Path
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

fun contentToLines(content : String):List<String> {
    return content.split(System.lineSeparator())
}

fun fileContentToTaskDocument(filename: Path, content: String): TaskDocument {
    return fileContentToTaskDocument(filename, contentToLines(content))
}

fun isHeader1(line: String): Boolean {
    return line.length >= 2 && line[0] == '#' && line[1] != '#'
}

fun isHeader2(line: String): Boolean {
    return line.length >= 3 && line[0] == '#' && line[1] == '#' && line[2] != '#'
}

fun isHeader3(line: String): Boolean {
    return line.length >= 4 && line[0] == '#' && line[1] == '#' && line[2] == '#' && line[3] != '#'
}

fun extractHeaderValue(line : String):String {
    var pos = 0

    while (pos < line.length && line[pos]=='#') {
        pos++
    }

    return line.substring(pos).trim()
}

fun fileContentToTaskDocument(filename: Path, content: List<String>): TaskDocument {
    val lineIterator = content.iterator()
    var currentLine = lineIterator.next()

    // Even an empty string should result in an non-empty list
    assert(content.isNotEmpty())

    if (!isHeader1(currentLine)) {
        throw FileFormatException(filename,"Cannot find top level title")
    }

    val title = extractHeaderValue(currentLine)

    // Search for task section
    val taskSectionContent = mutableListOf<String>()
    while (lineIterator.hasNext()) {
        currentLine = lineIterator.next()

        if (isHeader2(currentLine) && extractHeaderValue(currentLine)=="Task") {
            taskSectionContent.add(currentLine)
            break
        }
    }

    if (taskSectionContent.isEmpty()) {
        throw FileFormatException(filename,"Cannot find 'Task' sub-section")
    }

    // Search for end or next sub-section
    val documentContent = mutableListOf<String>()
    while (lineIterator.hasNext()) {
        currentLine = lineIterator.next()

        if (isHeader2(currentLine)) {
            documentContent.add(currentLine)
            break
        }
        else if (isHeader3(currentLine)) {
            when (val header3Title = extractHeaderValue(currentLine)) {
                "Properties" -> {}
                "History" -> {}
                else -> {
                    throw FileFormatException(filename,"Unknown 'task' sub section '$header3Title'")
                }
            }

            taskSectionContent.add(currentLine)
        }
        else {
            taskSectionContent.add(currentLine)
        }
    }

    // Collect rest of body
    while (lineIterator.hasNext()) {
        currentLine = lineIterator.next()

        documentContent.add(currentLine)
    }

    return TaskDocument(
        filename,
        title,
        taskSectionContent,
        documentContent.joinToString(System.lineSeparator())
    )
}

private fun taskSectionToTaskAttributes(
    filename: Path,
    section: List<String>,
    handlerMap: Map<String, AttributeFileHandler>
): TaskAttributes {
    val lineIterator = section.iterator()
    var currentLine: String

    var attributes = TaskAttributes(0)

    // Search for properties section
    var foundProperties = false
    while (lineIterator.hasNext()) {
        currentLine = lineIterator.next()

        if (isHeader3(currentLine) && extractHeaderValue(currentLine)=="Properties") {
            foundProperties = true
            break
        }
    }

    if (!foundProperties) {
        throw FileFormatException(filename,"Cannot find 'Task' sub-section 'Properties'")
    }

    // Search for table start
    var foundTable = false
    while (lineIterator.hasNext()) {
        currentLine = lineIterator.next()

        if (currentLine.isNotEmpty() && currentLine[0]=='|') {
            foundTable = true
            break
        }
        else if (isHeader3(currentLine) || isHeader2(currentLine) || isHeader1(currentLine)) {
            break
        }
    }

    if (!foundTable) {
        throw FileFormatException(filename,"Cannot find 'Task' attribute table")
    }

    // Skip table header
    lineIterator.next()

    // Skip divider
    currentLine = lineIterator.next()

    while (currentLine.isNotEmpty() && currentLine[0]=='|') {
        val column1Start = 0 + 1
        val column1End = currentLine.indexOf("|", column1Start)

        val keyName = currentLine.substring(column1Start, column1End).trim()

        val column2Start = column1End + 1
        val column2End = currentLine.indexOf("|", column2Start)

        val value = currentLine.substring(column2Start, column2End).trim()

        val handler = handlerMap[keyName]

        if (handler != null) {
            attributes = handler.fileValueToAttribute(filename,value,attributes)
        }
        else {
            throw FileFormatException(filename,"Unknown task attribute '$keyName'")
        }

        if (lineIterator.hasNext()) {
            currentLine = lineIterator.next()
        }
        else {
            break
        }
    }

    return attributes
}

private fun taskSectionToTaskHistory(
    filename: Path,
    section: List<String>,
    handlerMap: Map<String, HistoryFileHandler>
): TaskHistory {
    val lineIterator = section.iterator()
    var currentLine: String

    var history = TaskHistory()

    // Search for properties section
    var foundHistory = false
    while (lineIterator.hasNext()) {
        currentLine = lineIterator.next()

        if (isHeader3(currentLine) && extractHeaderValue(currentLine)=="History") {
            foundHistory = true
            break
        }
    }

    if (!foundHistory) {
        return history
    }

    // Search for table start
    var foundTable = false
    while (lineIterator.hasNext()) {
        currentLine = lineIterator.next()

        if (currentLine.isNotEmpty() && currentLine[0]=='|') {
            foundTable = true
            break
        }
    }

    if (!foundTable) {
        throw FileFormatException(filename,"Cannot find 'Task' history table")
    }

    // Skip table header

    if (!lineIterator.hasNext()) {
        return history
    }

    lineIterator.next()

    // Skip divider

    if (!lineIterator.hasNext()) {
        return history
    }

    currentLine = lineIterator.next()

    while (currentLine.isNotEmpty() && currentLine[0]=='|') {
        val column1Start = 0 + 1
        val column1End = currentLine.indexOf("|", column1Start)

        val dateTimeString = currentLine.substring(column1Start, column1End).trim()

        val column2Start = column1End + 1
        val column2End = currentLine.indexOf("|", column2Start)

        val type = currentLine.substring(column2Start, column2End).trim()

        val column3Start = column2End + 1
        val column3End = currentLine.indexOf("|", column3Start)

        val value = currentLine.substring(column3Start, column3End).trim()

        val handler = handlerMap[type]

        if (handler != null) {
            val dateTime = try {
                LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            }
            catch (e : DateTimeParseException) {
                throw FileFormatException(filename,"History date cannot be parsed '$dateTimeString'")
            }

            val fileEntry = HistoryFileEntry(dateTime,type,value)
            history = handler.fileEntryToHistory(history, filename, fileEntry)
        }
        else {
            throw FileFormatException(filename,"Unknown task history type '$type'")
        }

        if (lineIterator.hasNext()) {
            currentLine = lineIterator.next()
        }
        else {
            break
        }
    }

    return history
}

private fun textBlockToTask(taskDocument: TaskDocument, attributeHandlerMap: Map<String, AttributeFileHandler>,
                            historyHandlerMap: Map<String, HistoryFileHandler>): Task {
    val title = taskDocument.title
    val attributes = taskSectionToTaskAttributes(taskDocument.filename, taskDocument.taskDescription, attributeHandlerMap)
    val history = taskSectionToTaskHistory(taskDocument.filename,taskDocument.taskDescription,historyHandlerMap)

    return Task(taskDocument.filename.fileName.toString(), title, attributes, history, taskDocument.body)
}

fun parseTask(filename: Path, fileContent: String, attributeHandlerMap: Map<String, AttributeFileHandler>,
              historyHandlerMap: Map<String, HistoryFileHandler>): Task {
    val textBlock = fileContentToTaskDocument(filename, fileContent)
    return textBlockToTask(textBlock, attributeHandlerMap,historyHandlerMap)
}
