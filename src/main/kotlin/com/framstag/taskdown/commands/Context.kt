package com.framstag.taskdown.commands

import com.framstag.taskdown.config.Config
import com.framstag.taskdown.database.Database
import com.github.ajalt.mordant.terminal.Terminal

class Context(val config : Config, val database : Database, val t : Terminal)