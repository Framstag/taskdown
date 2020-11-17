package com.framstag.taskdown.system

import java.nio.file.Path

fun callEditorForFile(filePath: Path):Boolean {
    val visual=System.getenv("VISUAL")
    val editor=System.getenv("EDITOR")

    val program = if (!visual.isNullOrBlank()) {
        visual
    }
    else if (!editor.isNullOrBlank()){
        editor
    }
    else {
        return false
    }

    val p = ProcessBuilder(program, filePath.toString())
        .inheritIO()

    val process = p.start()

    process.waitFor()

    return true
}