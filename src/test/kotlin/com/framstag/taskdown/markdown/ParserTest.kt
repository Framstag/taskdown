package com.framstag.taskdown.markdown

import com.framstag.taskdown.database.FileContent
import com.framstag.taskdown.database.FileFormatException
import com.framstag.taskdown.database.filehandler.fileHandlerMap
import com.framstag.taskdown.domain.Priority
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.nio.file.Path
import java.time.LocalDate

class ParserTest {
    @Test
    fun testEmptyTask() {
        val fileContent = ""

        val content = FileContent(Path.of("Test.md"), fileContent)

        val exception = Assertions.assertThrows(FileFormatException::class.java) {
            fileContentToTaskDocument(content.filename, content.content)
        }

        Assertions.assertEquals(content.filename, exception.filename)
        Assertions.assertEquals("Cannot find top level title", exception.errorDescription)
    }

    @Test
    fun testEmptyLineTask() {
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
    fun testBrokenMarkdownTask() {
        val fileContent = "***"

        val content = FileContent(Path.of("Test.md"), fileContent)

        val exception = Assertions.assertThrows(FileFormatException::class.java) {
            fileContentToTaskDocument(content.filename, content.content)
        }

        Assertions.assertEquals(content.filename, exception.filename)
        Assertions.assertEquals("Cannot find top level title", exception.errorDescription)
    }

    @Test
    fun testOnlyTitle() {
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
    fun testEmptyTaskSection() {
        val fileContent = """
            # Test

            ## Task
        """.trimIndent().replace("\n", System.lineSeparator())

        val content = FileContent(Path.of("Test.md"), fileContent)

        val document = fileContentToTaskDocument(content.filename, content.content)

        Assertions.assertEquals(content.filename, document.filename)
        Assertions.assertEquals("# Test", document.title)
        Assertions.assertEquals("## Task", document.taskDescription)
        Assertions.assertEquals("", document.body)
    }

    @Test
    fun testTaskSectionWithSomeContent() {
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
        Assertions.assertEquals("# Test", document.title)
        Assertions.assertEquals(taskSubSection, document.taskDescription)
        Assertions.assertEquals("", document.body)
    }

    @Test
    fun testTaskSectionWithPropertiesSubSubSection() {
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
        Assertions.assertEquals("# Test", document.title)
        Assertions.assertEquals(taskSubSection, document.taskDescription)
        Assertions.assertEquals("", document.body)
    }

    @Test
    fun parseIdTest() {
        val fileContent = """
            # Test
            ## Task
            ### Properties
            
            |Key|Value|
            |---|-----|
            |Id|1|
        """.trimIndent().replace("\n", System.lineSeparator())

        val content = FileContent(Path.of("Test.md"), fileContent)
        val task = parseTask(content.filename, content.content, fileHandlerMap)

        Assertions.assertEquals(1, task.attributes.id)
    }

    @Test
    fun parseNegativeIdTest() {
        val fileContent = """
            # Test
            ## Task
            ### Properties
            
            |Key|Value|
            |---|-----|
            |Id|-1|
        """.trimIndent().replace("\n", System.lineSeparator())

        val content = FileContent(Path.of("Test.md"), fileContent)

        Assertions.assertThrows(FileFormatException::class.java) {
            parseTask(content.filename, content.content, fileHandlerMap)
        }
    }

    @Test
    fun parseNonNumberIdTest() {
        val fileContent = """
            # Test
            ## Task
            ### Properties
            
            |Key|Value|
            |---|-----|
            |Id|XXX|
        """.trimIndent().replace("\n", System.lineSeparator())

        val content = FileContent(Path.of("Test.md"), fileContent)

        Assertions.assertThrows(FileFormatException::class.java) {
            parseTask(content.filename, content.content, fileHandlerMap)
        }
    }

    @Test
    fun parsePriorityATest() {
        val fileContent = """
            # Test
            ## Task
            ### Properties
            
            |Key|Value|
            |---|-----|
            |Priority|A|
        """.trimIndent().replace("\n", System.lineSeparator())

        val content = FileContent(Path.of("Test.md"), fileContent)
        val task = parseTask(content.filename, content.content, fileHandlerMap)

        Assertions.assertEquals(Priority.A, task.attributes.priority)
    }

    @Test
    fun parsePriorityBTest() {
        val fileContent = """
            # Test
            ## Task
            ### Properties
            
            |Key|Value|
            |---|-----|
            |Priority|B|
        """.trimIndent().replace("\n", System.lineSeparator())

        val content = FileContent(Path.of("Test.md"), fileContent)
        val task = parseTask(content.filename, content.content, fileHandlerMap)

        Assertions.assertEquals(Priority.B, task.attributes.priority)
    }

    @Test
    fun parsePriorityCTest() {
        val fileContent = """
            # Test
            ## Task
            ### Properties
            
            |Key|Value|
            |---|-----|
            |Priority|C|
        """.trimIndent().replace("\n", System.lineSeparator())

        val content = FileContent(Path.of("Test.md"), fileContent)
        val task = parseTask(content.filename, content.content, fileHandlerMap)

        Assertions.assertEquals(Priority.C, task.attributes.priority)
    }

    @Test
    fun parseUnknownPriorityTest() {
        val fileContent = """
            # Test
            ## Task
            ### Properties
            
            |Key|Value|
            |---|-----|
            |Priority|X|
        """.trimIndent().replace("\n", System.lineSeparator())

        val content = FileContent(Path.of("Test.md"), fileContent)

        Assertions.assertThrows(FileFormatException::class.java) {
            parseTask(content.filename, content.content, fileHandlerMap)
        }
    }

    @Test
    fun parseEmptyPriorityTest() {
        val fileContent = """
            # Test
            ## Task
            ### Properties
            
            |Key|Value|
            |---|-----|
            |Priority||
        """.trimIndent().replace("\n", System.lineSeparator())

        val content = FileContent(Path.of("Test.md"), fileContent)

        Assertions.assertThrows(FileFormatException::class.java) {
            parseTask(content.filename, content.content, fileHandlerMap)
        }
    }

    @Test
    fun parseCreationDateTest() {
        val fileContent = """
            # Test
            ## Task
            ### Properties
            
            |Key|Value|
            |---|-----|
            |CreationDate|2020-10-01|
        """.trimIndent().replace("\n", System.lineSeparator())

        val content = FileContent(Path.of("Test.md"), fileContent)
        val task = parseTask(content.filename, content.content, fileHandlerMap)

        Assertions.assertEquals(LocalDate.of(2020, 10, 1), task.attributes.creationDate)
    }

    @Test
    fun parseWronglyFormattedCreationDateTest() {
        val fileContent = """
            # Test
            ## Task
            ### Properties
            
            |Key|Value|
            |---|-----|
            |CreationDate|20201001|
        """.trimIndent().replace("\n", System.lineSeparator())

        val content = FileContent(Path.of("Test.md"), fileContent)

        Assertions.assertThrows(FileFormatException::class.java) {
            parseTask(content.filename, content.content, fileHandlerMap)
        }
    }

    @Test
    fun parseEmptyCreationDateTest() {
        val fileContent = """
            # Test
            ## Task
            ### Properties
            
            |Key|Value|
            |---|-----|
            |CreationDate||
        """.trimIndent().replace("\n", System.lineSeparator())

        val content = FileContent(Path.of("Test.md"), fileContent)
        val task = parseTask(content.filename, content.content, fileHandlerMap)

        Assertions.assertEquals(null, task.attributes.dueDate)
    }

    @Test
    fun parseDueDateTest() {
        val fileContent = """
            # Test
            ## Task
            ### Properties
            
            |Key|Value|
            |---|-----|
            |DueDate|2020-10-01|
        """.trimIndent().replace("\n", System.lineSeparator())

        val content = FileContent(Path.of("Test.md"), fileContent)
        val task = parseTask(content.filename, content.content, fileHandlerMap)

        Assertions.assertEquals(LocalDate.of(2020, 10, 1), task.attributes.dueDate)
    }

    @Test
    fun parseWronglyFormattedDueDateTest() {
        val fileContent = """
            # Test
            ## Task
            ### Properties
            
            |Key|Value|
            |---|-----|
            |DueDate|20201001|
        """.trimIndent().replace("\n", System.lineSeparator())

        val content = FileContent(Path.of("Test.md"), fileContent)

        Assertions.assertThrows(FileFormatException::class.java) {
            parseTask(content.filename, content.content, fileHandlerMap)
        }
    }

    @Test
    fun parseEmptyDueDateTest() {
        val fileContent = """
            # Test
            ## Task
            ### Properties
            
            |Key|Value|
            |---|-----|
            |DueDate||
        """.trimIndent().replace("\n", System.lineSeparator())

        val content = FileContent(Path.of("Test.md"), fileContent)
        val task = parseTask(content.filename, content.content, fileHandlerMap)

        Assertions.assertEquals(null, task.attributes.dueDate)
    }

    @Test
    fun parseOneTagTest() {
        val fileContent = """
            # Test
            ## Task
            ### Properties
            
            |Key|Value|
            |---|-----|
            |Tags|#test1|
        """.trimIndent().replace("\n", System.lineSeparator())

        val content = FileContent(Path.of("Test.md"), fileContent)
        val task = parseTask(content.filename, content.content, fileHandlerMap)

        Assertions.assertEquals(setOf("test1"), task.attributes.tags)
    }

    @Test
    fun parseMultipleTagsTest() {
        val fileContent = """
            # Test
            ## Task
            ### Properties
            
            |Key|Value|
            |---|-----|
            |Tags|#test1 #test2|
        """.trimIndent().replace("\n", System.lineSeparator())

        val content = FileContent(Path.of("Test.md"), fileContent)
        val task = parseTask(content.filename, content.content, fileHandlerMap)

        Assertions.assertEquals(setOf("test1", "test2"), task.attributes.tags)
    }
}
