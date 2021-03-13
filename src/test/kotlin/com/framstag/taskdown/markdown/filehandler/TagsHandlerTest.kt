package com.framstag.taskdown.markdown.filehandler

import com.framstag.taskdown.markdown.filehandler.*
import com.framstag.taskdown.domain.TaskAttributes
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.nio.file.Path

class TagsHandlerTest {
    @Test
    fun oneTagValueIsOK() {
        val handler = TagsHandler()
        var attributes = TaskAttributes(1)

        attributes = handler.fileValueToAttribute(Path.of("test.md"), "#test1", attributes)

        Assertions.assertEquals(setOf("test1"), attributes.tags)
    }

    @Test
    fun multipleTagValuesAreOK() {
        val handler = TagsHandler()
        var attributes = TaskAttributes(1)

        attributes = handler.fileValueToAttribute(Path.of("test.md"), "#test1 #test2", attributes)

        Assertions.assertEquals(setOf("test1", "test2"), attributes.tags)
    }

    @Test
    fun additionalSpacesAreOK() {
        val handler = TagsHandler()
        var attributes = TaskAttributes(1)

        attributes = handler.fileValueToAttribute(Path.of("test.md"), "#test1   #test2   #test3", attributes)

        Assertions.assertEquals(setOf("test1", "test2", "test3"), attributes.tags)
    }

    @Test
    fun noTagsResultInEmptyString() {
        val handler = TagsHandler()
        val attributes = TaskAttributes(1)

        val fileEntry = handler.attributeToKeyValue(attributes)

        Assertions.assertEquals(Pair(TagsHandler.NAME,""), fileEntry)
    }

    @Test
    fun oneTagResultsInSimpleString() {
        val handler = TagsHandler()
        val attributes = TaskAttributes(1).withTag("test")

        val fileEntry = handler.attributeToKeyValue(attributes)

        Assertions.assertEquals(Pair(TagsHandler.NAME,"#test"), fileEntry)
    }

    @Test
    fun multipleTagsResultInSpacedString() {
        val handler = TagsHandler()
        val attributes = TaskAttributes(1).withTag("test1").withTag("test2")

        val fileEntry = handler.attributeToKeyValue(attributes)

        Assertions.assertEquals(Pair(TagsHandler.NAME,"#test1 #test2"), fileEntry)
    }
}