package com.framstag.taskdown.markdown

import com.framstag.taskdown.domain.TaskAttributes

private val ATTRIBUTE_SECTION_TASK_START =
    """
    $ATTRIBUTE_SECTION_TASK
    
    $ATTRIBUTE_SECTION_TASK_PROPERTIES
    
    |Key     |Value|
    |--------|-----|
    """.trimIndent()

fun titleToHeader(title: String): String {
    return "# $title"+System.lineSeparator()+System.lineSeparator()
}

fun attributesToTaskSection(attributes : TaskAttributes, handlerMap : Map<String, AttributeFileHandler>):List<String> {
    val stringList = mutableListOf<String>()

    stringList.addAll(ATTRIBUTE_SECTION_TASK_START.split("\n"))

    handlerMap.entries.sortedBy {
        it.key
    }.map {
        it.value
    }.forEach {
        val keyAndValue = it.attributeToKeyValue(attributes)
        val row = "|" + keyAndValue.first + "|" + keyAndValue.second + "|"

        stringList.add(row)
    }

    return stringList
}
