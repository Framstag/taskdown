package com.framstag.taskdown.database

import java.lang.RuntimeException
import java.nio.file.Path

class FileFormatException(val filename : Path, val errorDescription : String) : RuntimeException("$filename: $errorDescription")