package com.framstag.taskdown.database.filehandler

import com.framstag.taskdown.markdown.AttributeFileHandler
import com.framstag.taskdown.domain.TaskAttributes
import java.nio.file.Path

class TagsHandler : AttributeFileHandler {
    override fun fileValueToAttribute(file: Path, value: String, current: TaskAttributes): TaskAttributes {
        val tags = value.split("#").map {
            it.trim()
        }.filter {
            it.isNotEmpty()
        }.toSet()

        return current.withTags(tags)
    }

    override fun attributeToKeyValue(current: TaskAttributes): Pair<String, String> {
        val tagString = current.tags.joinToString(" ") {
            "#$it"
        }

        return Pair(NAME, tagString)
    }

    companion object {
        const val NAME = "Tags"
    }
}