package com.framstag.taskdown.system

import java.nio.file.Files
import java.nio.file.Path

interface FileSystem {
    fun exists(path : Path):Boolean
    fun readFile(path : Path):String
    fun writeFile(path: Path, content : String)
    fun deleteFile(path : Path)

    fun copyFile(from : Path, to : Path) {
        writeFile(to,readFile(from))
    }

    fun isDirectory(path : Path):Boolean

    fun isReadable(path : Path):Boolean
    fun isWritable(path : Path):Boolean
}

class PhysicalFileSystem : FileSystem {
    override fun exists(path: Path):Boolean {
        return Files.exists(path)
    }

    override fun readFile(path: Path): String {
        return Files.readString(path,Charsets.UTF_8)
    }

    override fun writeFile(path: Path, content: String) {
        Files.writeString(path,content,Charsets.UTF_8)
    }

    override fun deleteFile(path: Path) {
        Files.delete(path)
    }

    override fun isDirectory(path : Path):Boolean {
        return Files.isDirectory(path)
    }

    override fun isReadable(path : Path):Boolean {
        return Files.isReadable(path)
    }

    override fun isWritable(path : Path):Boolean {
        return Files.isWritable(path)
    }
}

fun filterFilenameCharacters(filename: String): String {
    if (filename.isEmpty()) {
        return "_"
    }

    return filename.replace("""[\\/.:<>*|"'? ]+""".toRegex(), "_")
}
