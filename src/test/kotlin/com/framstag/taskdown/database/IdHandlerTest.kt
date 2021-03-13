package com.framstag.taskdown.database

import com.framstag.taskdown.database.filehandler.IdHandler
import com.framstag.taskdown.domain.TaskAttributes
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.nio.file.Path

class IdHandlerTest {
    @Test
    fun successfullyParsesNumericalId() {
        val handler = IdHandler()
        var attributes = TaskAttributes(1)

        attributes = handler.fileValueToAttribute(Path.of("test.md"), "2", attributes)

        Assertions.assertEquals(2, attributes.id)
    }

    @Test
    fun idMustNotBeNegative() {
        val handler = IdHandler()
        var attributes = TaskAttributes(1)

        Assertions.assertThrows(FileFormatException::class.java) {
            attributes = handler.fileValueToAttribute(Path.of("test.md"), "-1", attributes)
        }
    }

    @Test
    fun idMustBeNumeric() {
        val handler = IdHandler()
        var attributes = TaskAttributes(1)

        Assertions.assertThrows(FileFormatException::class.java) {
            attributes = handler.fileValueToAttribute(Path.of("test.md"), "XXX", attributes)
        }
    }

    @Test
    fun idInMarkdownIsCorrectlyFormatted() {
        val handler = IdHandler()
        val attributes = TaskAttributes(1)

        val fileEntry = handler.attributeToKeyValue(attributes)

        Assertions.assertEquals(Pair(IdHandler.NAME,"1"), fileEntry)
    }
}