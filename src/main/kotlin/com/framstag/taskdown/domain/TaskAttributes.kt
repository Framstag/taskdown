package com.framstag.taskdown.domain

import java.time.LocalDate

data class TaskAttributes(
    val id: Int,
    val priority: Priority = Priority.C,
    val tags: Set<String> = setOf(),
    val creationDate: LocalDate? = null
) {

    fun withPriority(priority: Priority): TaskAttributes {
        return this.copy(priority = priority)
    }

    fun withTags(tags: Set<String>): TaskAttributes {
        return this.copy(tags = tags)
    }

    fun withTag(tag: String): TaskAttributes {
        return this.copy(tags = tags.plus(tag))
    }

    fun withoutTag(tag: String): TaskAttributes {
        return this.copy(tags = tags.minus(tag))
    }

    fun withCreationDate(creationDate: LocalDate): TaskAttributes {
        return this.copy(creationDate = creationDate)
    }

    fun tagString():String {
        return tags.joinToString(" ") {
            "#$it"
        }
    }
}