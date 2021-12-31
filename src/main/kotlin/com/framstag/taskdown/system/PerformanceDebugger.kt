package com.framstag.taskdown.system

import java.time.Duration
import java.time.LocalTime
import java.time.Period

interface PerformanceDebugger {
    fun step(description: String)
}

class PerformanceNoopDebugger : PerformanceDebugger {
    override fun step(description: String) {
        // no code
    }

}

class PerformanceConsoleDebugger : PerformanceDebugger {
    private val start = LocalTime.now()

    private fun toRelativeTime(now : LocalTime): Long {
        return Duration.between(start,now).toMillis()
    }

    init {
        val duration = toRelativeTime(start)

        println("$duration Start")

    }

    override fun step(description: String) {
        val duration = toRelativeTime(LocalTime.now())

        println("$duration $description")
    }
}

var performanceDebugger : PerformanceDebugger = PerformanceNoopDebugger()
