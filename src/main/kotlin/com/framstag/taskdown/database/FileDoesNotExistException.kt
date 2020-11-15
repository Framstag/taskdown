package com.framstag.taskdown.database

import java.lang.RuntimeException
import java.nio.file.Path

class FileDoesNotExistException(val filename : Path) : RuntimeException("$filename does not exist")