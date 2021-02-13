package com.framstag.taskdown.domain

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

    companion object {
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

