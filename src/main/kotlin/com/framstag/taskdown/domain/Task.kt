package com.framstag.taskdown.domain

import com.github.ajalt.mordant.TermColors
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.Comparator

class TaskByPriorityComparator : Comparator<Task> {
    override fun compare(p1: Task, p2: Task): Int {
        return p1.attributes.priority.ordinal.compareTo(p2.attributes.priority.ordinal)
    }
}

class TaskByAgeComparator : Comparator<Task> {
    override fun compare(p1: Task, p2: Task): Int {
        val age1 = p1.attributes.creationDate?.toEpochDay() ?: Long.MAX_VALUE
        val age2 = p2.attributes.creationDate?.toEpochDay() ?: Long.MAX_VALUE

        return age1.compareTo(age2)
    }
}

class TaskByIdComparator : Comparator<Task> {
    override fun compare(p0: Task, p1: Task): Int {
        return p1.attributes.id - p0.attributes.id
    }
}

data class Task(val filename: String, val title : String, val attributes : TaskAttributes, val body : String) {
    fun withTitle(title : String):Task {
        return this.copy(title = title)
    }

    fun withFilename(filename : String):Task {
        return this.copy(filename = filename)
    }

    fun withAttributes(attributes : TaskAttributes):Task {
        return this.copy(attributes = attributes)
    }

    fun toFormattedString():String {
        val t = TermColors()

        val today = LocalDate.now()

        val creationDays = if (attributes.creationDate != null) {
            today.toEpochDay()-attributes.creationDate.toEpochDay()
        }
        else {
            0
        }

        val idString = t.bold(ID_FORMAT.format(attributes.id))
        val priorityString = PRIORITY_FORMAT.format(attributes.priority)
        val creationDateString = CREATION_DATE_FORMAT.format(creationDays)
        val titleString = TITLE_FORMAT.format(title)
        val tagString = t.gray(TAG_FORMAT.format(attributes.tagString()))

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

        val coloredDueDateString =
            if (attributes.dueDate != null) {
                val dueDateString = attributes.dueDate.format(DateTimeFormatter.ISO_LOCAL_DATE)

                when {
                    attributes.dueDate.isEqual(today) -> {
                        t.red(dueDateString)
                    }
                    attributes.dueDate.isBefore(today) -> {
                        t.bold(t.red(dueDateString))
                    }
                    else -> {
                        t.gray(dueDateString)
                    }
                }
            } else {
                "          "
            }

        return "$idString $coloredPriorityString $coloredDueDateString $creationDateString $coloredTitleString $tagString"
    }

    companion object {
        const val ID_FORMAT="%3d"
        const val PRIORITY_FORMAT = "%1s"
        const val TITLE_FORMAT = "%-40s"
        const val TAG_FORMAT = "%s"
        const val CREATION_DATE_FORMAT ="%3d"

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

