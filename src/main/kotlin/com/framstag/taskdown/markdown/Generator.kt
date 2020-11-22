package com.framstag.taskdown.markdown

import com.framstag.taskdown.domain.TaskAttributes

private val ATTRIBUTE_SECTION_TASK_START =
    """
    $ATTRIBUTE_SECTION_TASK
    
    $ATTRIBUTE_SECTION_TASK_PROPERTIES
    
    |Key     |Value|
    |--------|-----|
    
    """.trimIndent().replace("\n", System.lineSeparator())

fun titleToHeader(title: String): String {
    return "# $title"+System.lineSeparator()+System.lineSeparator()
}

fun attributesToTaskSection(attributes : TaskAttributes, handlerMap : Map<String, AttributeFileHandler>):String {
    var section = ATTRIBUTE_SECTION_TASK_START

    val handler = handlerMap.entries.sortedBy {
        it.key
    }.map {
        it.value
    }

    handler.forEach {
        val keyAndValue = it.attributeToKeyValue(attributes)
        val row = "|" + keyAndValue.first + "|" + keyAndValue.second + "|"+System.lineSeparator()

        section += row
    }

    section += System.lineSeparator()

    return section
}
