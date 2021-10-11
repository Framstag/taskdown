package com.framstag.taskdown.system

import com.framstag.taskdown.domain.Priority
import com.framstag.taskdown.domain.Task
import com.github.ajalt.mordant.terminal.Terminal
import com.github.ajalt.mordant.rendering.TextColors.*
import com.github.ajalt.mordant.rendering.TextStyles.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TaskListFormatter {
    fun format(task: Task):String {
        val today = LocalDate.now()

        val creationDays = if (task.attributes.creationDate != null) {
            today.toEpochDay()-task.attributes.creationDate.toEpochDay()
        }
        else {
            0
        }

        val idString = bold(ID_FORMAT.format(task.attributes.id))
        val priorityString = PRIORITY_FORMAT.format(task.attributes.priority)
        val creationDateString = CREATION_DATE_FORMAT.format(creationDays)
        val titleString = TITLE_FORMAT.format(task.title)
        val tagString = gray(TAG_FORMAT.format(task.attributes.tagString()))

        val coloredPriorityString = when (task.attributes.priority) {
            Priority.A -> bold(red(priorityString))
            Priority.B -> yellow(priorityString)
            else -> priorityString
        }

        val coloredTitleString = when (task.attributes.priority) {
            Priority.A -> bold(red(titleString))
            Priority.B -> yellow(titleString)
            else -> titleString
        }

        val coloredDueDateString =
            if (task.attributes.dueDate != null) {
                val dueDateString = task.attributes.dueDate.format(DateTimeFormatter.ISO_LOCAL_DATE)

                when {
                    task.attributes.dueDate.isEqual(today) -> {
                        red(dueDateString)
                    }
                    task.attributes.dueDate.isBefore(today) -> {
                        bold(red(dueDateString))
                    }
                    else -> {
                        gray(dueDateString)
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