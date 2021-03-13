package com.framstag.taskdown.database

import com.framstag.taskdown.system.FileSystem
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.nio.file.Path

class DatabaseTest {
    @Test
    fun legalDirectoriesTest() {
        val fileSystem = mockk<FileSystem>()
        val databaseDirectory = Path.of("database")
        val archiveDirectory = Path.of("archive")

        every {fileSystem.exists(databaseDirectory)} returns true
        every {fileSystem.isDirectory(databaseDirectory)} returns true
        every {fileSystem.isReadable(databaseDirectory)} returns true
        every {fileSystem.isWritable(databaseDirectory)} returns true

        every {fileSystem.exists(archiveDirectory)} returns true
        every {fileSystem.isDirectory(archiveDirectory)} returns true
        every {fileSystem.isReadable(archiveDirectory)} returns true
        every {fileSystem.isWritable(archiveDirectory)} returns true

        val database =
            Database(fileSystem, databaseDirectory, archiveDirectory, mapOf(), mapOf())

        database.validate()

        verifyAll {
            fileSystem.exists(databaseDirectory)
            fileSystem.isDirectory(databaseDirectory)
            fileSystem.isReadable(databaseDirectory)
            fileSystem.isWritable(databaseDirectory)

            fileSystem.exists(archiveDirectory)
            fileSystem.isDirectory(archiveDirectory)
            fileSystem.isReadable(archiveDirectory)
            fileSystem.isWritable(archiveDirectory)
        }
    }

    @Test
    fun databaseDirectoryDoesNotExistTest() {
        val fileSystem = mockk<FileSystem>()
        val databaseDirectory = Path.of("database")
        val archiveDirectory = Path.of("archive")

        every {fileSystem.exists(databaseDirectory)} returns false

        val database =
            Database(fileSystem, databaseDirectory, archiveDirectory, mapOf(), mapOf())

        val exception = Assertions.assertThrows(NoValidDirectoryException::class.java) {
            database.validate()
        }

        Assertions.assertEquals(databaseDirectory,exception.filename)
    }

    @Test
    fun databaseDirectoryIsNoDirectoryTest() {
        val fileSystem = mockk<FileSystem>()
        val databaseDirectory = Path.of("database")
        val archiveDirectory = Path.of("archive")

        every {fileSystem.exists(databaseDirectory)} returns true
        every {fileSystem.isDirectory(databaseDirectory)} returns false

        val database =
            Database(fileSystem, databaseDirectory, archiveDirectory, mapOf(), mapOf())

        val exception = Assertions.assertThrows(NoValidDirectoryException::class.java) {
            database.validate()
        }

        Assertions.assertEquals(databaseDirectory,exception.filename)
    }

    @Test
    fun databaseDirectoryIsNotReadableTest() {
        val fileSystem = mockk<FileSystem>()
        val databaseDirectory = Path.of("database")
        val archiveDirectory = Path.of("archive")

        every {fileSystem.exists(databaseDirectory)} returns true
        every {fileSystem.isDirectory(databaseDirectory)} returns true
        every {fileSystem.isReadable(databaseDirectory)} returns false

        val database =
            Database(fileSystem, databaseDirectory, archiveDirectory, mapOf(), mapOf())

        val exception = Assertions.assertThrows(NoValidDirectoryException::class.java) {
            database.validate()
        }

        Assertions.assertEquals(databaseDirectory,exception.filename)
    }

    @Test
    fun databaseDirectoryIsNotWritableTest() {
        val fileSystem = mockk<FileSystem>()
        val databaseDirectory = Path.of("database")
        val archiveDirectory = Path.of("archive")

        every {fileSystem.exists(databaseDirectory)} returns true
        every {fileSystem.isDirectory(databaseDirectory)} returns true
        every {fileSystem.isReadable(databaseDirectory)} returns true
        every {fileSystem.isWritable(databaseDirectory)} returns false

        val database =
            Database(fileSystem, databaseDirectory, archiveDirectory, mapOf(), mapOf())

        val exception = Assertions.assertThrows(NoValidDirectoryException::class.java) {
            database.validate()
        }

        Assertions.assertEquals(databaseDirectory,exception.filename)
    }

    @Test
    fun archiveDirectoryDoesNotExistTest() {
        val fileSystem = mockk<FileSystem>()
        val databaseDirectory = Path.of("database")
        val archiveDirectory = Path.of("archive")

        every {fileSystem.exists(databaseDirectory)} returns true
        every {fileSystem.isDirectory(databaseDirectory)} returns true
        every {fileSystem.isReadable(databaseDirectory)} returns true
        every {fileSystem.isWritable(databaseDirectory)} returns true

        every {fileSystem.exists(archiveDirectory)} returns false

        val database =
            Database(fileSystem, databaseDirectory, archiveDirectory, mapOf(), mapOf())

        val exception = Assertions.assertThrows(NoValidDirectoryException::class.java) {
            database.validate()
        }

        Assertions.assertEquals(archiveDirectory,exception.filename)
    }

    @Test
    fun archiveDirectoryIsNoDirectoryTest() {
        val fileSystem = mockk<FileSystem>()
        val databaseDirectory = Path.of("database")
        val archiveDirectory = Path.of("archive")

        every {fileSystem.exists(databaseDirectory)} returns true
        every {fileSystem.isDirectory(databaseDirectory)} returns true
        every {fileSystem.isReadable(databaseDirectory)} returns true
        every {fileSystem.isWritable(databaseDirectory)} returns true

        every {fileSystem.exists(archiveDirectory)} returns true
        every {fileSystem.isDirectory(archiveDirectory)} returns false

        val database =
            Database(fileSystem, databaseDirectory, archiveDirectory, mapOf(), mapOf())

        val exception = Assertions.assertThrows(NoValidDirectoryException::class.java) {
            database.validate()
        }

        Assertions.assertEquals(archiveDirectory,exception.filename)
    }

    @Test
    fun archiveDirectoryIsNotReadableTest() {
        val fileSystem = mockk<FileSystem>()
        val databaseDirectory = Path.of("database")
        val archiveDirectory = Path.of("archive")

        every {fileSystem.exists(databaseDirectory)} returns true
        every {fileSystem.isDirectory(databaseDirectory)} returns true
        every {fileSystem.isReadable(databaseDirectory)} returns true
        every {fileSystem.isWritable(databaseDirectory)} returns true

        every {fileSystem.exists(archiveDirectory)} returns true
        every {fileSystem.isDirectory(archiveDirectory)} returns true
        every {fileSystem.isReadable(archiveDirectory)} returns false

        val database =
            Database(fileSystem, databaseDirectory, archiveDirectory, mapOf(), mapOf())

        val exception = Assertions.assertThrows(NoValidDirectoryException::class.java) {
            database.validate()
        }

        Assertions.assertEquals(archiveDirectory,exception.filename)
    }

    @Test
    fun archiveDirectoryIsNotWritableTest() {
        val fileSystem = mockk<FileSystem>()
        val databaseDirectory = Path.of("database")
        val archiveDirectory = Path.of("archive")

        every {fileSystem.exists(databaseDirectory)} returns true
        every {fileSystem.isDirectory(databaseDirectory)} returns true
        every {fileSystem.isReadable(databaseDirectory)} returns true
        every {fileSystem.isWritable(databaseDirectory)} returns true

        every {fileSystem.exists(archiveDirectory)} returns true
        every {fileSystem.isDirectory(archiveDirectory)} returns true
        every {fileSystem.isReadable(archiveDirectory)} returns true
        every {fileSystem.isWritable(archiveDirectory)} returns false

        val database =
            Database(fileSystem, databaseDirectory, archiveDirectory, mapOf(), mapOf())

        val exception = Assertions.assertThrows(NoValidDirectoryException::class.java) {
            database.validate()
        }

        Assertions.assertEquals(archiveDirectory,exception.filename)
    }

}