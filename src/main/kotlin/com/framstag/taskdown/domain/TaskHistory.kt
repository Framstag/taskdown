package com.framstag.taskdown.domain

import java.time.LocalDateTime

data class TaskHistory(val logs : List<TaskLog> = listOf()) {

    fun withLog(dateTime : LocalDateTime, description : String):TaskHistory {
        val taskStatus = TaskLog(dateTime,description)

        return this.copy(logs = logs.plus(taskStatus))
    }
}