package com.framstag.taskdown.markdown

import com.framstag.taskdown.database.FileContent
import com.framstag.taskdown.database.FileFormatException
import com.framstag.taskdown.database.filehandler.markdownPropertyHandlerMap
import com.framstag.taskdown.database.filehandler.markdownHistoryHandlerMap
import com.framstag.taskdown.domain.Priority
import com.framstag.taskdown.domain.TaskLog
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.nio.file.Path
import java.time.LocalDateTime

class ParserTest {
    @Test
    fun emptyTaskContent() {
        val fileContent = ""

        val content = FileContent(Path.of("Test.md"), fileContent)

        val exception = Assertions.assertThrows(FileFormatException::class.java) {
            fileContentToTaskDocument(content.filename, content.content)
        }

        Assertions.assertEquals(content.filename, exception.filename)
        Assertions.assertEquals("Cannot find top level title", exception.errorDescription)
    }

    @Test
    fun emptyLineTaskContent() {
        val fileContent = """
        """.trimIndent().replace("\n", System.lineSeparator())

        val content = FileContent(Path.of("Test.md"), fileContent)

        val exception = Assertions.assertThrows(FileFormatException::class.java) {
            fileContentToTaskDocument(content.filename, content.content)
        }

        Assertions.assertEquals(content.filename, exception.filename)
        Assertions.assertEquals("Cannot find top level title", exception.errorDescription)
    }

    @Test
    fun contentDoesNotStartWithSection() {
        val fileContent = "***"

        val content = FileContent(Path.of("Test.md"), fileContent)

        val exception = Assertions.assertThrows(FileFormatException::class.java) {
            fileContentToTaskDocument(content.filename, content.content)
        }

        Assertions.assertEquals(content.filename, exception.filename)
        Assertions.assertEquals("Cannot find top level title", exception.errorDescription)
    }

    @Test
    fun contentDoesNotHaveTaskSection() {
        val fileContent = """
            # Test
        """.trimIndent().replace("\n", System.lineSeparator())

        val content = FileContent(Path.of("Test.md"), fileContent)

        val exception = Assertions.assertThrows(FileFormatException::class.java) {
            fileContentToTaskDocument(content.filename, content.content)
        }

        Assertions.assertEquals(content.filename, exception.filename)
        Assertions.assertEquals("Cannot find 'Task' sub-section", exception.errorDescription)
    }

    @Test
    fun taskSubSectionIsEmpty() {
        val fileContent = """
            # Test

            ## Task
        """.trimIndent().replace("\n", System.lineSeparator())

        val content = FileContent(Path.of("Test.md"), fileContent)

        val document = fileContentToTaskDocument(content.filename, content.content)

        Assertions.assertEquals(content.filename, document.filename)
        Assertions.assertEquals("Test", document.title)
        Assertions.assertEquals("## Task".split(System.lineSeparator()), document.taskDescription)
        Assertions.assertEquals("", document.body)
    }

    @Test
    fun taskSubSectionCanHaveAnyContent() {
        val fileHeader = """
            # Test

        """.trimIndent().replace("\n", System.lineSeparator())

        val taskSubSection = """
            ## Task

            Bla bla
        """.trimIndent().replace("\n", System.lineSeparator())

        val content = FileContent(Path.of("Test.md"), fileHeader + taskSubSection)

        val document = fileContentToTaskDocument(content.filename, content.content)

        Assertions.assertEquals(content.filename, document.filename)
        Assertions.assertEquals("Test", document.title)
        Assertions.assertEquals(taskSubSection.split(System.lineSeparator()), document.taskDescription)
        Assertions.assertEquals("", document.body)
    }

    @Test
    fun taskSubSectionCanHavePropertiesSubSubSection() {
        val fileHeader = """
            # Test

        """.trimIndent().replace("\n", System.lineSeparator())

        val taskSubSection = """
            ## Task

            ### Properties
        """.trimIndent().replace("\n", System.lineSeparator())

        val content = FileContent(Path.of("Test.md"), fileHeader + taskSubSection)

        val document = fileContentToTaskDocument(content.filename, content.content)

        Assertions.assertEquals(content.filename, document.filename)
        Assertions.assertEquals("Test", document.title)
        Assertions.assertEquals(taskSubSection.split(System.lineSeparator()), document.taskDescription)
        Assertions.assertEquals("", document.body)
    }

    @Test
    fun taskSubSectionMustNotHaveUnknownSubSubSection() {
        val fileContent = """
            # Test
            
            ## Task
            
            ### BlaBlaBla
        """.trimIndent().replace("\n", System.lineSeparator())

        val content = FileContent(Path.of("Test.md"), fileContent)

        val exception = Assertions.assertThrows(FileFormatException::class.java) {
            fileContentToTaskDocument(content.filename, content.content)
        }

        Assertions.assertEquals(content.filename, exception.filename)
        Assertions.assertEquals("Unknown 'task' sub section 'BlaBlaBla'", exception.errorDescription)
    }

    @Test
    fun attributeHandlerCallingDuringParsingWorks() {
        val fileContent = """
            # Test
            ## Task
            ### Properties
            
            |Key|Value|
            |---|-----|
            |Priority|A|
        """.trimIndent().replace("\n", System.lineSeparator())

        val content = FileContent(Path.of("Test.md"), fileContent)
        val task = parseTask(content.filename, content.content, markdownPropertyHandlerMap, markdownHistoryHandlerMap)

        Assertions.assertEquals(Priority.A, task.attributes.priority)
    }

    @Test
    fun historyHandlerCallingDuringParsingWorks() {
        val dateTime = LocalDateTime.of(2020, 10, 1, 13, 0, 0)

        val fileContent = """
            # Test
            
            ## Task
            
            ### Properties
            
            |Key|Value|
            |---|-----|

            ### History
            
            |Date|Type|Value|
            |----|----|-----|
            |2020-10-01T13:00:00|Log|This is a test|
        """.trimIndent().replace("\n", System.lineSeparator())

        val content = FileContent(Path.of("Test.md"), fileContent)
        val task = parseTask(content.filename, content.content, markdownPropertyHandlerMap, markdownHistoryHandlerMap)

        Assertions.assertEquals(listOf(TaskLog(dateTime,"This is a test")), task.history.logs)
    }

}
