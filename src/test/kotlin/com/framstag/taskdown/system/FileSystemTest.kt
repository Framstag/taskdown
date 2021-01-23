package com.framstag.taskdown.system

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class FileSystemTest {

    @Test
    fun testFilterFilenameCharacters_EmptyFilename() {
        val filtered = filterFilenameCharacters("")

        Assertions.assertEquals("_",filtered)
    }

    @Test
    fun testFilterFilenameCharacters_MultipleForbidden() {
        val filtered = filterFilenameCharacters("**")

        Assertions.assertEquals("_",filtered)
    }

    @Test
    fun testFilterFilenameCharacters_CompletelyForbiddenFilename() {
        val filtered = filterFilenameCharacters("/\\.:<>*|\"'? ")

        Assertions.assertEquals("_",filtered)
    }

    @Test
    fun testFilterFilenameCharacters_MixedFilename() {
        val filtered = filterFilenameCharacters("*Good?Bad:Ugly\\")

        Assertions.assertEquals("_Good_Bad_Ugly_",filtered)
    }
}