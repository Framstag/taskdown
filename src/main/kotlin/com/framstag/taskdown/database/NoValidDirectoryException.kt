package com.framstag.taskdown.database

import java.lang.RuntimeException
import java.nio.file.Path

class NoValidDirectoryException(filename: Path, val errorDescription: String) :
    RuntimeException("$filename is not a valid directory: $errorDescription")