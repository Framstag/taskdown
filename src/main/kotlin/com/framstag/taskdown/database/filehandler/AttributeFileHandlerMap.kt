package com.framstag.taskdown.database.filehandler

val fileHandlerMap = mapOf(
    TagsFileHandler.NAME to TagsFileHandler(),
    IdFileHandler.NAME to IdFileHandler(),
    PriorityFileHandler.NAME to PriorityFileHandler(),
    CreationDateHandler.NAME to CreationDateHandler(),
    DueDateHandler.NAME to DueDateHandler()
)
