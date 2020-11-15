package com.framstag.taskdown.domain

import com.github.ajalt.mordant.TermColors
import java.nio.file.Path


data class Task(val filename: Path?, val title : String, val attributes : TaskAttributes) {
    fun withTitle(title : String):Task {
        return this.copy(title = title)
    }

    fun withFilename(filename : Path):Task {
        return this.copy(filename = filename)
    }

    fun withAttributes(attributes : TaskAttributes):Task {
        return this.copy(attributes = attributes)
    }

    private fun getHashString(hashes : Set<String>):String {
        return if (hashes.isNotEmpty()) {
            hashes.joinToString(" ", "[", "]") {
                "#$it"
            }
        } else {
            ""
        }
    }

    fun toFormattedString():String {
        val formattedText = FORMAT.format(attributes.id, attributes.priority, title, getHashString(attributes.hashes))
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

