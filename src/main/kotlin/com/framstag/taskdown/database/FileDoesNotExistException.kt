package com.framstag.taskdown.database

import java.lang.RuntimeException
import java.nio.file.Path

class FileDoesNotExistException(filename : Path) : RuntimeException("$filename does not exist")