package com.framstag.taskdown.domain

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TaskAttributesTest {

    // tagString()

    @Test
    fun testTagStringDefault() {
        val attributes = TaskAttributes(1)

        Assertions.assertEquals("",attributes.tagString())
    }

    @Test
    fun testTagStringOneTag() {
        val attributes = TaskAttributes(1).withTag("bla")

        Assertions.assertEquals("#bla",attributes.tagString())
    }

    @Test
    fun testTagStringTwoTags() {
        val attributes = TaskAttributes(1).withTag("bla").withTag("blub")

        Assertions.assertEquals("#bla #blub",attributes.tagString())
    }

    @Test
    fun testTagStringThreeTags() {
        val attributes = TaskAttributes(1).withTag("one").withTag("two").withTag("three")

        Assertions.assertEquals("#one #three #two", attributes.tagString())
    }

    @Test
    fun testTagStringThreeTagsOrdered1() {
        val attributes = TaskAttributes(1).withTag("three").withTag("two").withTag("one")

        Assertions.assertEquals("#one #three #two", attributes.tagString())
    }

    @Test
    fun testTagStringThreeTagsOrdered2() {
        val attributes = TaskAttributes(1).withTag("two").withTag("three").withTag("one")

        Assertions.assertEquals("#one #three #two", attributes.tagString())
    }
}