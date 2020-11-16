package com.framstag.taskdown.domain

data class TaskAttributes(val id: Int, val priority: Priority = Priority.C, val tags : Set<String> = setOf()) {

    fun withPriority(priority : Priority):TaskAttributes {
        return this.copy(priority = priority)
    }

    fun withTags(tags : Set<String>):TaskAttributes {
        return this.copy(tags = tags)
    }

    fun withTag(tag : String):TaskAttributes {
        return this.copy(tags = tags.plus(tag))
    }

    fun withoutTag(tag : String):TaskAttributes {
        return this.copy(tags = tags.minus(tag))
    }
}