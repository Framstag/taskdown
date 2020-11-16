package com.framstag.taskdown.domain

import com.github.ajalt.mordant.TermColors

data class Task(val filename: String, val title : String, val attributes : TaskAttributes) {
    fun withTitle(title : String):Task {
        return this.copy(title = title)
    }

    fun withFilename(filename : String):Task {
        return this.copy(filename = filename)
    }

    fun withAttributes(attributes : TaskAttributes):Task {
        return this.copy(attributes = attributes)
    }

    private fun getTagString(tags : Set<String>):String {
        return if (tags.isNotEmpty()) {
            tags.joinToString(" ", "[", "]") {
                "#$it"
            }
        } else {
            ""
        }
    }

    fun toFormattedString():String {
        val formattedText = FORMAT.format(attributes.id, attributes.priority, title, getTagString(attributes.tags))
        val t = TermColors()

        return when (attributes.priority) {
            Priority.A -> t.bold(t.red(formattedText))
            Priority.B -> t.yellow(formattedText)
            else -> formattedText
        }
    }

    companion object {
        const val FORMAT = "%3d %1s %s %s"

        fun getNextFreeTaskId(tasks : List<Task>):Int {
            val idMap = tasks.associateBy { it.attributes.id }

            var possibleId=1

            while (idMap.containsKey(possibleId)) {
                possibleId++
            }

            return possibleId
        }
    }
}

