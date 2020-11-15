package com.framstag.taskdown.database.filehandler

val fileHandlerMap = mapOf(
    HashesFileHandler.NAME to HashesFileHandler(),
    IdFileHandler.NAME to IdFileHandler(),
    PriorityFileHandler.NAME to PriorityFileHandler()
)
