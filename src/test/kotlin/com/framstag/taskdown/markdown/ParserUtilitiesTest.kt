package com.framstag.taskdown.markdown

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ParserUtilitiesTest {
    @Test
    fun emptyTextIsNoHeader1() {
        val line = ""

        val isHeader = isHeader1(line)

        Assertions.assertFalse(isHeader)
    }

    @Test
    fun emptyHeader1MarkdownIsNoHeader() {
        val line = "#"

        val isHeader = isHeader1(line)

        Assertions.assertFalse(isHeader)
    }

    @Test
    fun header1MarkdownWithJustSpaceIsHeader() {
        val line = "# "

        val isHeader = isHeader1(line)

        Assertions.assertTrue(isHeader)
    }

    @Test
    fun header1WithTextIsOk() {
        val line = "# Some Text"

        val isHeader = isHeader1(line)

        Assertions.assertTrue(isHeader)
    }

    @Test
    fun emptyTextIsNoHeader2() {
        val line = ""

        val isHeader = isHeader2(line)

        Assertions.assertFalse(isHeader)
    }

    @Test
    fun emptyHeader2MarkdownIsNoHeader() {
        val line = "##"

        val isHeader = isHeader2(line)

        Assertions.assertFalse(isHeader)
    }

    @Test
    fun header2MarkdownWithJustSpaceIsHeader() {
        val line = "## "

        val isHeader = isHeader2(line)

        Assertions.assertTrue(isHeader)
    }

    @Test
    fun header2WithTextIsOk() {
        val line = "## Some Text"

        val isHeader = isHeader2(line)

        Assertions.assertTrue(isHeader)
    }

    @Test
    fun emptyTextIsNoHeader3() {
        val line = ""

        val isHeader = isHeader3(line)

        Assertions.assertFalse(isHeader)
    }

    @Test
    fun emptyHeader3MarkdownIsNoHeader() {
        val line = "###"

        val isHeader = isHeader3(line)

        Assertions.assertFalse(isHeader)
    }

    @Test
    fun header3MarkdownWithJustSpaceIsHeader() {
        val line = "### "

        val isHeader = isHeader3(line)

        Assertions.assertTrue(isHeader)
    }

    @Test
    fun header3WithTextIsOk() {
        val line = "### Some Text"

        val isHeader = isHeader3(line)

        Assertions.assertTrue(isHeader)
    }

    @Test
    fun extractEmptyTextAsHeaderResultsInEmptyText() {
        val line = ""

        val headerValue = extractHeaderValue(line)

        Assertions.assertEquals("",headerValue)
    }

    @Test
    fun nonHeaderResultsInCompleteLine() {
        val line = "+++ Some Text"

        val headerValue = extractHeaderValue(line)

        Assertions.assertEquals(line,headerValue)
    }

    @Test
    fun header1WithSomeTextIsOK() {
        val line = "# Some Text"

        val headerValue = extractHeaderValue(line)

        Assertions.assertEquals("Some Text",headerValue)
    }

    @Test
    fun header2WithSomeTextIsOK() {
        val line = "## Some Text"

        val headerValue = extractHeaderValue(line)

        Assertions.assertEquals("Some Text",headerValue)
    }

    @Test
    fun header3WithSomeTextIsOK() {
        val line = "### Some Text"

        val headerValue = extractHeaderValue(line)

        Assertions.assertEquals("Some Text",headerValue)
    }
}