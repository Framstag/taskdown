package com.framstag.taskdown.database

import com.framstag.taskdown.domain.Task
import com.framstag.taskdown.markdown.*
import com.framstag.taskdown.system.FileSystem
import com.framstag.taskdown.system.filterFilenameCharacters
import java.nio.file.Path
import java.util.*
import kotlin.jvm.Throws

private fun loadFromFilenameToFileContent(fileSystem : FileSystem, filename: Path): FileContent {
    return FileContent(filename, fileSystem.readFile(filename))
}

private fun storeFromFileContentToFile(fileSystem : FileSystem, fileContent : FileContent) {
    fileSystem.writeFile(fileContent.filename,fileContent.content)
}

private fun texBlockToContent(document: TaskDocument): FileContent {
    return FileContent(
        document.filename,
        taskDocumentToFileContent(document)
    )
}

private fun updateTextBlock(
    document: TaskDocument,
    task: Task,
    handlerMap: Map<String, AttributeFileHandler>
): TaskDocument {
    return document
        .withHeader(task.title)
        .withTaskDescription(attributesToTaskSection(task.attributes, handlerMap))
}

class Database(
    private val fileSystem : FileSystem,
    private val databaseDir: Path,
    private val archiveDir: Path,
    private val handlerMap: Map<String, AttributeFileHandler>
) {
    @Throws(NoValidDirectoryException::class)
    private fun validateDirectory(directory : Path) {

        if (!fileSystem.exists(directory)) {
            throw NoValidDirectoryException(directory,"Does not exist")
        }

        if (!fileSystem.isDirectory(directory)) {
            throw NoValidDirectoryException(directory,"Is not a directory")
        }

        if (!fileSystem.isReadable(directory)) {
            throw NoValidDirectoryException(directory,"Directory is not readable")
        }

        if (!fileSystem.isWritable(directory)) {
            throw NoValidDirectoryException(directory,"Directory is not writeable")
        }
    }

    @Throws(NoValidDirectoryException::class)
    fun validate() {
        validateDirectory(databaseDir)
        validateDirectory(archiveDir)
    }

    private fun printFailureResults(failureResults : List<Result<Task>>) {
        failureResults.forEach {result ->
            result.onFailure {
                if (it is FileFormatException) {
                    println("ERROR: ${it.filename}: ${it.errorDescription}")
                }
                else {
                    println("ERROR: ${it.message}")
                }
            }
        }
    }

    private fun loadTask(filename : Path):Task {
        return Result.runCatching {
            loadFromFilenameToFileContent(fileSystem,filename)
        }.mapCatching {
            parseTask(it.filename,it.content, handlerMap)
        }.getOrThrow()
    }

    fun createTask(task : Task):Task {
        val filename = getFilenameForActiveTask(task)
        val path = databaseDir.resolve(filename)

        val taskOnDisk = task.withFilename(filename)

        Result.runCatching {
            TaskDocument(path)
        }.mapCatching {
            updateTextBlock(it, task, handlerMap)
        }.mapCatching {
            texBlockToContent(it)
        }.mapCatching {
            storeFromFileContentToFile(fileSystem,it)
        }

        return taskOnDisk
    }

    fun backupTask(task : Task) {
        val filename = task.filename
        val databaseFilePath = databaseDir.resolve(filename)
        val postfixStart = filename.lastIndexOf('.')

        if (postfixStart>=0) {
            val backupFilename=filename.replaceRange(postfixStart,filename.length,".bak")
            val backupFilePath= databaseDir.resolve(backupFilename)

            fileSystem.copyFile(databaseFilePath,backupFilePath)
        }
    }

    fun updateTask(task : Task):Task {
        val databaseFilePath = databaseDir.resolve(task.filename)

        if (!fileSystem.exists(databaseFilePath)) {
            throw FileDoesNotExistException(databaseFilePath)
        }

        Result.runCatching {
            loadFromFilenameToFileContent(fileSystem, databaseFilePath)
        }.mapCatching {
            fileContentToTaskDocument(it.filename,it.content)
        }.mapCatching {
            updateTextBlock(it,task,handlerMap)
        }.mapCatching {
            texBlockToContent(it)
        }.mapCatching {
            storeFromFileContentToFile(fileSystem, it)
        }

        return task
    }

    fun deleteTask(task : Task) {
        val databaseFilePath = databaseDir.resolve(task.filename)

        // TODO: Delete possible backup, too
        fileSystem.deleteFile(databaseFilePath)
    }

    fun archiveTask(task : Task) {
        val databaseFilePath = databaseDir.resolve(task.filename)
        val archiveFilePath= archiveDir.resolve(getFilenameForArchiveTask(task))

        fileSystem.copyFile(databaseFilePath,archiveFilePath)

        fileSystem.deleteFile(databaseFilePath)
    }

    fun reloadTask(task : Task):Task {
        val databaseFilePath = databaseDir.resolve(task.filename)

        return loadTask(databaseFilePath)
    }

    fun getPathForActiveTask(task : Task):Path {
        return databaseDir.resolve(task.filename)
    }

    fun loadTasks(): List<Task> {
        val fileNames = databaseDir.toFile().list { _, name ->
            name.toLowerCase().endsWith(".md")
        }

        if (fileNames == null) {
            return listOf()
        }

        val (successResults, failureResults)= fileNames.map {
            Result.runCatching {
                loadTask(databaseDir.resolve(it))
            }
        }.partition {
            it.isSuccess
        }

        printFailureResults(failureResults)

        return successResults.mapNotNull {
            it.getOrNull()
        }
    }

    private fun getFilenameForActiveTask(task : Task):String {
        val format = "%03d_%.20s.md"
        val filename = filterFilenameCharacters(task.title)

        return format.format(task.attributes.id,filename)
    }

    private fun getFilenameForArchiveTask(task : Task):String {
        val format = "%tY%tm%td_%.20s.md"
        val now= Calendar.getInstance()
        val filename = filterFilenameCharacters(task.title)

        return format.format(now,now,now,filename)
    }
}