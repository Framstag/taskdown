package com.framstag.taskdown.markdown.filehandler

val markdownPropertyHandlerMap = mapOf(
    TagsHandler.NAME to TagsHandler(),
    IdHandler.NAME to IdHandler(),
    PriorityHandler.NAME to PriorityHandler(),
    CreationDateHandler.NAME to CreationDateHandler(),
    DueDateHandler.NAME to DueDateHandler()
)
