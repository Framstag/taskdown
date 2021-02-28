package com.framstag.taskdown.database

import com.framstag.taskdown.database.filehandler.PriorityFileHandler
import com.framstag.taskdown.domain.Priority
import com.framstag.taskdown.domain.TaskAttributes
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.nio.file.Path

class FileHandlerTest {

    @Test
    fun parsePriorityA() {
        val handler = PriorityFileHandler()
        var attributes = TaskAttributes(1).withPriority(Priority.C)

        attributes = handler.fileValueToAttribute(Path.of("test.md"),"A",attributes)

        Assertions.assertEquals(Priority.A,attributes.priority)
    }

    @Test
    fun parsePriorityB() {
        val handler = PriorityFileHandler()
        var attributes = TaskAttributes(1).withPriority(Priority.A)

        attributes = handler.fileValueToAttribute(Path.of("test.md"),"B",attributes)

        Assertions.assertEquals(Priority.B,attributes.priority)
    }

    @Test
    fun parsePriorityC() {
        val handler = PriorityFileHandler()
        var attributes = TaskAttributes(1).withPriority(Priority.A)

        attributes = handler.fileValueToAttribute(Path.of("test.md"),"C",attributes)

        Assertions.assertEquals(Priority.C,attributes.priority)
    }

    @Test
    fun anyOtherPriorityIsFailure() {
        val handler = PriorityFileHandler()
        val attributes = TaskAttributes(1).withPriority(Priority.A)

        Assertions.assertThrows(FileFormatException::class.java) {
            handler.fileValueToAttribute(Path.of("test.md"), "X", attributes)
        }
    }

    @Test
    fun emptyPriorityIsNotAllowed() {
        val handler = PriorityFileHandler()
        val attributes = TaskAttributes(1).withPriority(Priority.A)

        Assertions.assertThrows(FileFormatException::class.java) {
            handler.fileValueToAttribute(Path.of("test.md"), "", attributes)
        }
    }

    @Test
    fun defaultPriorityToValue() {
        val handler = PriorityFileHandler()
        val attributes = TaskAttributes(1)

        val value = handler.attributeToKeyValue(attributes)

        Assertions.assertEquals("C",value.second)
    }

    @Test
    fun priorityAToValue() {
        val handler = PriorityFileHandler()
        val attributes = TaskAttributes(1).withPriority(Priority.A)

        val value = handler.attributeToKeyValue(attributes)

        Assertions.assertEquals("A",value.second)
    }

    @Test
    fun priorityBToValue() {
        val handler = PriorityFileHandler()
        val attributes = TaskAttributes(1).withPriority(Priority.B)

        val value = handler.attributeToKeyValue(attributes)

        Assertions.assertEquals("B",value.second)
    }

    @Test
    fun priorityCToValue() {
        val handler = PriorityFileHandler()
        val attributes = TaskAttributes(1).withPriority(Priority.C)

        val value = handler.attributeToKeyValue(attributes)

        Assertions.assertEquals("C",value.second)
    }
}