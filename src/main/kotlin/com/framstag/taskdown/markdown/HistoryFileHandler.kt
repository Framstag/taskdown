package com.framstag.taskdown.markdown

import com.framstag.taskdown.domain.TaskHistory
import java.nio.file.Path

interface HistoryFileHandler {

    fun fileEntryToHistory(
        history: TaskHistory,
        file: Path,
        fileEntry: HistoryFileEntry
    ): TaskHistory

    fun historyToFileEntryList(history : TaskHistory):List<HistoryFileEntry>
}