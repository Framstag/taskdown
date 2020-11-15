package com.framstag.taskdown.domain

data class TaskAttributes(val id: Int, val priority: Priority = Priority.C, val hashes : Set<String> = setOf()) {

    fun withPriority(priority : Priority):TaskAttributes {
        return this.copy(priority = priority)
    }

    fun withHashes(hashes : Set<String>):TaskAttributes {
        return this.copy(hashes = hashes)
    }

    fun withHash(hash : String):TaskAttributes {
        return this.copy(hashes = hashes.plus(hash))
    }

    fun withoutHash(hash : String):TaskAttributes {
        return this.copy(hashes = hashes.minus(hash))
    }
}