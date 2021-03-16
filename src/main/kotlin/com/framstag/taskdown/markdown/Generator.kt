package com.framstag.taskdown.markdown

import com.framstag.taskdown.domain.Task
import com.framstag.taskdown.domain.TaskAttributes
import com.framstag.taskdown.domain.TaskHistory
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

fun titleToHeader(title: String): String {
    return "# $title" + System.lineSeparator() + System.lineSeparator()
}

private fun attributesToPropertiesSection(
    attributes: TaskAttributes,
    handlerMap: Map<String, AttributeFileHandler>
): List<String> {
    val stringList = mutableListOf<String>()

    stringList.add(ATTRIBUTE_SECTION_TASK_PROPERTIES)
    stringList.add("")
    stringList.add("|Key         |Value|")
    stringList.add("|------------|-----|")

    handlerMap.entries.sortedBy {
        it.key
    }.map {
        it.value
    }.forEach {
        val keyAndValue = it.attributeToKeyValue(attributes)
        val row = "|" + keyAndValue.first.padEnd(12) + "|" + keyAndValue.second + "|"

        stringList.add(row)
    }

    return stringList
}

private fun historyToHistorySection(
    history: TaskHistory,
    handlerMap: Map<String, HistoryFileHandler>
): List<String> {
    val stringList = mutableListOf<String>()

    stringList.add(ATTRIBUTE_SECTION_TASK_HISTORY)
    stringList.add("")
    stringList.add("|Date               |Type  |Value|")
    stringList.add("|-------------------|------|-----|")

    val fileEntries = mutableListOf<HistoryFileEntry>()

    handlerMap.entries.forEach {
        fileEntries.addAll(it.value.historyToFileEntryList(history))
    }

    fileEntries.sortedBy {
        it.dateTime
    }.forEach {
        stringList.add(
            "|${
                it.dateTime.truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            }|${it.key}|${it.value}|"
        )
    }

    return stringList
}

fun taskToTaskDescription(
    task: Task,
    attributeHandlerMap: Map<String, AttributeFileHandler>,
    historyHandlerMap: Map<String, HistoryFileHandler>
): List<String> {
    return listOf(ATTRIBUTE_SECTION_TASK, "")
        .plus(attributesToPropertiesSection(task.attributes, attributeHandlerMap))
        .plus("")
        .plus(historyToHistorySection(task.history, historyHandlerMap))
}

fun taskDocumentToFileContent(document: TaskDocument): String {
    return titleToHeader(document.title) + document.taskDescription.joinToString(System.lineSeparator()) + document.body
}
