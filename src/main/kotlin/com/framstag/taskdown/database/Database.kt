package com.framstag.taskdown.database

import com.framstag.taskdown.domain.Task
import com.framstag.taskdown.markdown.*
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.jvm.Throws

private fun loadFromFilenameToFileContent(filename: Path): FileContent {
    return FileContent(filename, Files.readString(filename))
}

private fun storeFromFileContentToFile(fileContent : FileContent) {
    Files.writeString(fileContent.filename,fileContent.content,Charsets.UTF_8)
}

private fun texBlockToContent(textBlock: TextBlock): FileContent {
    return FileContent(
        textBlock.filename,
        textBlock.header + textBlock.taskDescription + textBlock.footer
    )
}

private fun headerToTitle(header: String): String {
    return header.substring(1).trim()
}

private fun textBlockToTask(textBlock: TextBlock, handlerMap: Map<String, AttributeFileHandler>): Task {
    val title = headerToTitle(textBlock.header)
    val attributes = taskSectionToTaskAttributes(textBlock.filename, textBlock.taskDescription, handlerMap)

    return Task(textBlock.filename.fileName.toString(), title, attributes)
}

private fun updateTextBlock(
    textBlock: TextBlock,
    task: Task,
    handlerMap: Map<String, AttributeFileHandler>
): TextBlock {
    return textBlock
        .withHeader(titleToHeader(task.title))
        .withTaskDescription(attributesToTaskSection(task.attributes, handlerMap))
}

class Database(
    private val databaseDir: Path,
    private val archiveDir: Path,
    private val handlerMap: Map<String, AttributeFileHandler>
) {
    @Throws(NoValidDirectoryException::class)
    fun validateDirectory(path : Path) {
        val file = path.toFile()

        if (!file.exists()) {
            throw NoValidDirectoryException(path,"Does not exist")
        }


        if (!file.isDirectory) {
            throw NoValidDirectoryException(path,"Is not a directory")
        }

        if (!file.canRead()) {
            throw NoValidDirectoryException(path,"Directory is not readable")
        }

        if (!file.canWrite()) {
            throw NoValidDirectoryException(path,"Directory is not writeable")
        }
    }

    @Throws(NoValidDirectoryException::class)
    fun validate() {
        validateDirectory(databaseDir)
        validateDirectory(archiveDir)
    }

    private fun copyFile(from : Path, to : Path) {
        Files.writeString(to, Files.readString(from))
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
            loadFromFilenameToFileContent(filename)
        }.mapCatching {
            fileContentToTextBlock(it.filename,it.content)
        }.mapCatching {
            textBlockToTask(it,handlerMap)
        }.getOrThrow()
    }

    fun createTask(task : Task):Task {
        val filename = getFilenameForActiveTask(task)
        val path = databaseDir.resolve(filename)

        val taskOnDisk = task.withFilename(filename)

        Result.runCatching {
            TextBlock(path)
        }.mapCatching {
            updateTextBlock(it, task, handlerMap)
        }.mapCatching {
            texBlockToContent(it)
        }.mapCatching {
            storeFromFileContentToFile(it)
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

            copyFile(databaseFilePath,backupFilePath)
        }
    }

    fun updateTask(task : Task):Task {
        val databaseFilePath = databaseDir.resolve(task.filename)

        if (Files.notExists(databaseFilePath)) {
            throw FileDoesNotExistException(databaseFilePath)
        }

        Result.runCatching {
            loadFromFilenameToFileContent(databaseFilePath)
        }.mapCatching {
            fileContentToTextBlock(it.filename,it.content)
        }.mapCatching {
            updateTextBlock(it,task,handlerMap)
        }.mapCatching {
            texBlockToContent(it)
        }.mapCatching {
            storeFromFileContentToFile(it)
        }

        return task
    }

    fun deleteTask(task : Task) {
        val databaseFilePath = databaseDir.resolve(task.filename)

        // TODO: Delete possible backup, too
        Files.delete(databaseFilePath)
    }

    fun archiveTask(task : Task) {
        val databaseFilePath = databaseDir.resolve(task.filename)
        val archiveFilePath= archiveDir.resolve(getFilenameForArchiveTask(task))

        copyFile(databaseFilePath,archiveFilePath)

        Files.delete(databaseFilePath)
    }

    fun reloadTask(task : Task):Task {
        val databaseFilePath = databaseDir.resolve(task.filename)

        return loadTask(databaseFilePath)
    }

    fun loadTaskContent(task : Task):String {
        val databaseFilePath = databaseDir.resolve(task.filename)
        val fileContent = loadFromFilenameToFileContent(databaseFilePath)

        return fileContent.content
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
        val title = task.title.replace(" ","_")

        return format.format(task.attributes.id,title)
    }

    private fun getFilenameForArchiveTask(task : Task):String {
        val format = "%tY%tm%td_%.20s.md"
        val now= Calendar.getInstance()
        val title = task.title.replace(" ","_")

        return format.format(now,now,now,title)
    }
}