package com.framstag.taskdown.database

import com.framstag.taskdown.domain.Task
import com.framstag.taskdown.domain.TaskAttributes
import java.nio.file.Files
import java.nio.file.Path

const val ATTRIBUTE_SECTION_TASK = "## Task"
const val ATTRIBUTE_SECTION_TASK_PROPERTIES = "### Properties"
const val ATTRIBUTE_SECTION_2 = "## "

var ATTRIBUTE_SECTION_TASK_START =
    """
    $ATTRIBUTE_SECTION_TASK
    
    $ATTRIBUTE_SECTION_TASK_PROPERTIES
    
    |Key     |Value|
    |--------|-----|
    
    """.trimIndent()

private fun loadFromFilenameToFileContent(filename: Path): FileContent {
    return FileContent(filename, Files.readString(filename))
}

private fun storeFromFileContentToFile(fileContent : FileContent) {
    Files.writeString(fileContent.filename,fileContent.content,Charsets.UTF_8)
}

private fun fileContentToTextBlock(content: FileContent): TextBlock {
    val titleStart = content.content.indexOf("#")

    if (titleStart < 0) {
        throw FileFormatException(content.filename,"Cannot find top level title")
    }

    var titleEnd = content.content.indexOf(System.lineSeparator(), titleStart + 1)

    if (titleEnd<0) {
        titleEnd = content.content.length-1
    }

    val taskStart = content.content.indexOf(ATTRIBUTE_SECTION_TASK, titleEnd + 1)

    if (taskStart < 0) {
        throw FileFormatException(content.filename,"Cannot find 'Task' sub-section")
    }

    val propertiesStart = content.content.indexOf(ATTRIBUTE_SECTION_TASK_PROPERTIES,taskStart + ATTRIBUTE_SECTION_TASK.length)
    var taskEnd = content.content.indexOf(ATTRIBUTE_SECTION_2, propertiesStart+ ATTRIBUTE_SECTION_TASK_PROPERTIES.length)

    if (taskEnd < 0) {
        taskEnd = content.content.length-1
    }

    return TextBlock(
        content.filename,
        content.content.substring(titleStart, taskStart),
        content.content.substring(taskStart, taskEnd),
        content.content.substring(taskEnd, content.content.length)
    )
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

private fun titleToHeader(title: String): String {
    return "# $title"+System.lineSeparator()+System.lineSeparator()
}

private fun attributesToTaskSection(attributes : TaskAttributes,handlerMap : Map<String,AttributeFileHandler>):String {
    var section = ATTRIBUTE_SECTION_TASK_START

    val handler = handlerMap.entries.sortedBy {
        it.key
    }.map {
        it.value
    }

    handler.forEach {
        val keyAndValue = it.attributeToKeyValue(attributes)
        val row = "|" + keyAndValue.first + "|" + keyAndValue.second + "|"+System.lineSeparator()

        section += row
    }

    section += System.lineSeparator()

    return section
}

private fun taskSectionToTaskAttributes(
    filename: Path,
    section: String,
    handlerMap: Map<String, AttributeFileHandler>
): TaskAttributes {

    var attributes = TaskAttributes(0)

    val propertiesStart = section.indexOf(ATTRIBUTE_SECTION_TASK_PROPERTIES)

    if (propertiesStart < 0) {
        throw FileFormatException(filename,"Cannot find 'Task' sub-section 'Properties'")
    }

    val tableStart = section.indexOf("|",propertiesStart+ ATTRIBUTE_SECTION_TASK_PROPERTIES.length)

    if (tableStart < 0) {
        throw FileFormatException(filename,"Cannot find 'Task' attribute table")
    }

    // Table header
    var tableRowStart = section.indexOf(System.lineSeparator(),tableStart+1)

    // Table header divider
    tableRowStart = section.indexOf(System.lineSeparator(),tableRowStart+1)
    // Start of first value row
    tableRowStart = section.indexOf("|", tableRowStart+1)

    while (tableRowStart >= 0) {
        val column1Start = tableRowStart
        val column1End = section.indexOf("|", column1Start + 1)

        val keyName = section.substring(column1Start + 1, column1End).trim()

        val column2Start = column1End
        val column2End = section.indexOf("|", column2Start + 1)

        val value = section.substring(column2Start + 1, column2End).trim()

        val handler = handlerMap[keyName]

        if (handler != null) {
            attributes = handler.fileValueToAttribute(filename,value,attributes)
        }
        else {
            throw FileFormatException(filename,"Unknown task attribute '$keyName'")
        }

        tableRowStart = section.indexOf("|", column2End+1)
    }

    return attributes
}

private fun textBlockToTask(textBlock: TextBlock, handlerMap: Map<String, AttributeFileHandler>): Task {
    val title = headerToTitle(textBlock.header)
    val attributes = taskSectionToTaskAttributes(textBlock.filename, textBlock.taskDescription, handlerMap)

    return Task(textBlock.filename, title, attributes)
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
    fun getPath(): Path {
        return databaseDir
    }

    fun isValid(): Boolean {
        val file = databaseDir.toFile()

        return file.exists() && file.isDirectory && file.canRead() && file.canWrite()
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
            fileContentToTextBlock(it)
        }.mapCatching {
            textBlockToTask(it,handlerMap)
        }.getOrThrow()
    }

    fun createTask(task : Task):Task {
        val filename = getPathForActiveTask(task)

        val taskOnDisk = task.withFilename(filename)

        Result.runCatching {
            TextBlock(filename)
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
        val filename = task.filename?.fileName.toString();

        val postfixStart = filename.lastIndexOf('.')

        if (postfixStart>=0) {
            val backupFilename=filename.replaceRange(postfixStart,filename.length,".bak")
            val backupFilePath= databaseDir.resolve(backupFilename)

            copyFile(task.filename!!,backupFilePath)
        }
    }

    fun updateTask(task : Task):Task {
        assert (task.filename != null)

        // TODO: Use some else than assert + check
        if (task.filename == null) {
            throw AssertionError()
        }

        if (Files.notExists(task.filename)) {
            throw FileDoesNotExistException(task.filename)
        }

        Result.runCatching {
            loadFromFilenameToFileContent(task.filename)
        }.mapCatching {
            fileContentToTextBlock(it)
        }.mapCatching {
            updateTextBlock(it,task,handlerMap)
        }.mapCatching {
            texBlockToContent(it)
        }.mapCatching {
            storeFromFileContentToFile(it)
        }

        return task
    }

    fun deleteTask(filename : Path) {
        Files.delete(filename)
    }

    fun archiveTask(task : Task) {
        val archiveFilename= archiveDir.resolve(task.filename!!.fileName)

        copyFile(task.filename,archiveFilename)

        Files.delete(task.filename)
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
                loadTask(Path.of(databaseDir.toString(),it))
            }
        }.partition {
            it.isSuccess
        }

        printFailureResults(failureResults)

        return successResults.mapNotNull {
            it.getOrNull()
        }
    }

    private fun getPathForActiveTask(task : Task):Path {
        val filename = "${task.attributes.id} "+task.title.replace(" ","_")+".md"

        return Path.of(databaseDir.toString(),filename)
    }
}