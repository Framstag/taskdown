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
        Assertions.assertEquals("# Test", document.title)
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
        Assertions.assertEquals("# Test", document.title)
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
        Assertions.assertEquals("# Test", document.title)
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
    fun successfullyParsesNumericalId() {
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
    fun idMustNotBeNegative() {
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
    fun idMustBeNumeric() {
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
    fun priorityAIsOK() {
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
    fun priorityBIsOK() {
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
    fun priorityCIsOK() {
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
    fun anyOtherPriorityIsFailure() {
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
    fun emptyPriorityIsNotAllowed() {
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
    fun creationDateMustBeISOODate() {
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
    fun anyOtherCreationDateFormatIsFailure() {
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
    fun emptyCreationDateIsFailure() {
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
    fun dueDateMustBeISODate() {
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
    fun anyOtherDueDateFormatIsFailure() {
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
    fun emptyDueDateIsFailure() {
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
    fun oneTagValueIsOK() {
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
    fun multipleTagValuesAreOK() {
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
