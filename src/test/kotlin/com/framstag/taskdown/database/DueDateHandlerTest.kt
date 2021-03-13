package com.framstag.taskdown.database

import com.framstag.taskdown.database.filehandler.DueDateHandler
import com.framstag.taskdown.domain.TaskAttributes
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.nio.file.Path
import java.time.LocalDate

class DueDateHandlerTest {
    @Test
    fun dueDateMustBeISOODate() {
        val handler = DueDateHandler()
        var attributes = TaskAttributes(1)

        attributes = handler.fileValueToAttribute(Path.of("test.md"),"2020-10-01",attributes)

        Assertions.assertEquals(LocalDate.of(2020, 10, 1), attributes.dueDate)
    }

    @Test
    fun anyOtherDueDateFormatIsFailure() {
        val handler = DueDateHandler()
        var attributes = TaskAttributes(1)

        Assertions.assertThrows(FileFormatException::class.java) {
            attributes = handler.fileValueToAttribute(Path.of("test.md"), "20201001", attributes)
        }
    }

    @Test
    fun emptyDueDateIsNotAFailure() {
        val handler = DueDateHandler()
        var attributes = TaskAttributes(1)

        attributes = handler.fileValueToAttribute(Path.of("test.md"), "", attributes)

        Assertions.assertEquals(null, attributes.dueDate)
    }

    @Test
    fun emptyDueDateResultsInEmptyString() {
        val handler = DueDateHandler()
        val attributes = TaskAttributes(1).withNoDueDate()

        val fileEntry = handler.attributeToKeyValue(attributes)

        Assertions.assertEquals(Pair(DueDateHandler.NAME,""), fileEntry)
    }

    @Test
    fun setDueDateResultsInIsoDateString() {
        val handler = DueDateHandler()
        val attributes = TaskAttributes(1).withDueDate(LocalDate.of(2020, 10, 1))

        val fileEntry = handler.attributeToKeyValue(attributes)

        Assertions.assertEquals(Pair(DueDateHandler.NAME,"2020-10-01"), fileEntry)
    }
}