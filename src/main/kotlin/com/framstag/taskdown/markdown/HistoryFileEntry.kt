package com.framstag.taskdown.markdown

import java.time.LocalDateTime

data class HistoryFileEntry(val dateTime : LocalDateTime,val key : String, val value : String)