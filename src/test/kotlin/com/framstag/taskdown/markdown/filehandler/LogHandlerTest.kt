package com.framstag.taskdown.markdown.filehandler

import com.framstag.taskdown.markdown.filehandler.*
import com.framstag.taskdown.domain.TaskHistory
import com.framstag.taskdown.domain.TaskLog
import com.framstag.taskdown.markdown.HistoryFileEntry
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.nio.file.Path
import java.time.LocalDateTime

class LogHandlerTest {
    @Test
    fun statusLineWithValueIsOK() {
        val handler = LogHandler()
        val dateTime = LocalDateTime.of(2020, 10, 1, 13, 0, 0)
        var history = TaskHistory()

        history = handler.fileEntryToHistory(
            history, Path.of("test.md"), HistoryFileEntry(dateTime, LogHandler.NAME, "This is a test")
        )

        Assertions.assertEquals(
            listOf(TaskLog(dateTime, "This is a test")), history.logs
        )
    }

    @Test
    fun statusLineWithoutValueIsOK() {
        val handler = LogHandler()
        val dateTime = LocalDateTime.of(2020, 10, 1, 13, 0, 0)
        var history = TaskHistory()

        history = handler.fileEntryToHistory(
            history, Path.of("test.md"), HistoryFileEntry(dateTime, LogHandler.NAME, "")
        )

        Assertions.assertEquals(
            listOf(
                TaskLog(
                    dateTime, ""
                )
            ), history.logs
        )
    }

    @Test
    fun oneStatusEntryToFile() {
        val handler = LogHandler()
        val dateTime = LocalDateTime.of(2020, 10, 1, 13, 0, 0)
        val history = TaskHistory().withLog(dateTime, "This is a test")

        val list = handler.historyToFileEntryList(history)

        Assertions.assertEquals(listOf(HistoryFileEntry(dateTime, LogHandler.NAME, "This is a test")), list)
    }

    @Test
    fun multipleStatusEntriesSortedToFile() {
        val handler = LogHandler()
        val dateTime1 = LocalDateTime.of(2020, 10, 1, 13, 0, 0)
        val dateTime2 = LocalDateTime.of(2020, 10, 2, 13, 0, 0)
        val history = TaskHistory().withLog(dateTime2, "Second test").withLog(dateTime1, "First test")

        val list = handler.historyToFileEntryList(history)

        Assertions.assertEquals(
            listOf(
                HistoryFileEntry(dateTime1, LogHandler.NAME, "First test"),
                HistoryFileEntry(dateTime2, LogHandler.NAME, "Second test")
            ), list
        )
    }
}