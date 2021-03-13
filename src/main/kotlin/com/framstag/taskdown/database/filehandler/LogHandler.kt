package com.framstag.taskdown.database.filehandler

import com.framstag.taskdown.domain.TaskHistory
import com.framstag.taskdown.markdown.HistoryFileEntry
import com.framstag.taskdown.markdown.HistoryFileHandler
import java.nio.file.Path

class LogHandler : HistoryFileHandler {
    override fun fileEntryToHistory(
        history: TaskHistory,
        file: Path,
        fileEntry: HistoryFileEntry
    ): TaskHistory {
        return history.withLog(fileEntry.dateTime, fileEntry.value)
    }

    override fun historyToFileEntryList(history: TaskHistory): List<HistoryFileEntry> {
        return history.logs.sortedBy {
            it.dateTime
        }.map {
            HistoryFileEntry(it.dateTime, NAME, it.description)
        }
    }

    companion object {
        const val NAME = "Log"
    }
}