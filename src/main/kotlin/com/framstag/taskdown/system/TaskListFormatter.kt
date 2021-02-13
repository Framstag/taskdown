package com.framstag.taskdown.system

import com.framstag.taskdown.domain.Priority
import com.framstag.taskdown.domain.Task
import com.github.ajalt.mordant.TermColors
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TaskListFormatter {
    fun format(task: Task):String {
        val t = TermColors()

        val today = LocalDate.now()

        val creationDays = if (task.attributes.creationDate != null) {
            today.toEpochDay()-task.attributes.creationDate.toEpochDay()
        }
        else {
            0
        }

        val idString = t.bold(ID_FORMAT.format(task.attributes.id))
        val priorityString = PRIORITY_FORMAT.format(task.attributes.priority)
        val creationDateString = CREATION_DATE_FORMAT.format(creationDays)
        val titleString = TITLE_FORMAT.format(task.title)
        val tagString = t.gray(TAG_FORMAT.format(task.attributes.tagString()))

        val coloredPriorityString = when (task.attributes.priority) {
            Priority.A -> t.bold(t.red(priorityString))
            Priority.B -> t.yellow(priorityString)
            else -> priorityString
        }

        val coloredTitleString = when (task.attributes.priority) {
            Priority.A -> t.bold(t.red(titleString))
            Priority.B -> t.yellow(titleString)
            else -> titleString
        }

        val coloredDueDateString =
            if (task.attributes.dueDate != null) {
                val dueDateString = task.attributes.dueDate.format(DateTimeFormatter.ISO_LOCAL_DATE)

                when {
                    task.attributes.dueDate.isEqual(today) -> {
                        t.red(dueDateString)
                    }
                    task.attributes.dueDate.isBefore(today) -> {
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
    }
}