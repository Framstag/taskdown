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
        return tags.joinToString(" ") {
            "#$it"
        }
    }

    fun toFormattedString():String {
        val t = TermColors()

        val idString = t.bold(ID_FORMAT.format(attributes.id))
        val priorityString = PRIORITY_FORMAT.format(attributes.priority)
        val titleString = TITLE_FORMAT.format(title)
        val tagString = t.gray(TAG_FORMAT.format(getTagString(attributes.tags)))

        val coloredPriorityString = when (attributes.priority) {
            Priority.A -> t.bold(t.red(priorityString))
            Priority.B -> t.yellow(priorityString)
            else -> priorityString
        }

        val coloredTitleString = when (attributes.priority) {
            Priority.A -> t.bold(t.red(titleString))
            Priority.B -> t.yellow(titleString)
            else -> titleString
        }

        return "$idString $coloredPriorityString $coloredTitleString $tagString"
    }

    companion object {
        const val ID_FORMAT="%3d"
        const val PRIORITY_FORMAT = "%1s"
        const val TITLE_FORMAT = "%-30s"
        const val TAG_FORMAT = "%s"

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

