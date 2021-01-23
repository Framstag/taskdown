package com.framstag.taskdown.system

fun filterFilenameCharacters(filename: String): String {
    if (filename.isEmpty()) {
        return "_"
    }

    return filename.replace("""[\\/.:<>*|"'? ]+""".toRegex(), "_")
}
