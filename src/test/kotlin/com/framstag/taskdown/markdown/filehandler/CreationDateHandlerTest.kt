package com.framstag.taskdown.markdown.filehandler

import com.framstag.taskdown.database.FileFormatException
import com.framstag.taskdown.domain.TaskAttributes
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.nio.file.Path
import java.time.LocalDate

class CreationDateHandlerTest {
    @Test
    fun creationDateMustBeISOODate() {
        val handler = CreationDateHandler()
        var attributes = TaskAttributes(1)

        attributes = handler.fileValueToAttribute(Path.of("test.md"),"2020-10-01",attributes)

        Assertions.assertEquals(LocalDate.of(2020, 10, 1), attributes.creationDate)
    }

    @Test
    fun anyOtherCreationDateFormatIsFailure() {
        val handler = CreationDateHandler()
        var attributes = TaskAttributes(1)

        Assertions.assertThrows(FileFormatException::class.java) {
            attributes = handler.fileValueToAttribute(Path.of("test.md"), "20201001", attributes)
        }
    }

    @Test
    fun emptyCreationDateIsNotAFailure() {
        val handler = CreationDateHandler()
        var attributes = TaskAttributes(1)

        attributes = handler.fileValueToAttribute(Path.of("test.md"), "", attributes)

        Assertions.assertEquals(null, attributes.creationDate)
    }

    @Test
    fun emptyCreationDateResultsInEmptyString() {
        val handler = CreationDateHandler()
        val attributes = TaskAttributes(1)

        val fileEntry = handler.attributeToKeyValue(attributes)

        Assertions.assertEquals(Pair(CreationDateHandler.NAME,""), fileEntry)
    }

    @Test
    fun setCreationDateResultsInIsoDateString() {
        val handler = CreationDateHandler()
        val attributes = TaskAttributes(1).withCreationDate(LocalDate.of(2020, 10, 1))

        val fileEntry = handler.attributeToKeyValue(attributes)

        Assertions.assertEquals(Pair(CreationDateHandler.NAME,"2020-10-01"), fileEntry)
    }
}