# Taskdown

## About

Taskdown is a simple terminal based application to manage
tasks.

Each task is represented by a markdown file in a database
directory. Taskdown uses some metadata in a special task section
to manage the task. Everything else is just a markdown file
you can edit as you like it.

Taskdown was created to manage my tasks at work. It is an
experiment to further focus structuring my work using simple
markdown files.

Markdown is writen in Kotlin.

## Status

This is very much work in progress. We can manage
tasks. Currently implemented commands are:

* add - adding a new task
* list - lists tasks (including some filtering)
* show - print a single task to the terminal
* update - change properties of a task
* delete - delete a task
* archive - archive a task

Smaller advanced features are:

* Simple color output
* Creation of backup files

Everything else is missing:

* Tests
* Error handling
* Advanced task properties
* Statistics
* ...

## Future

Further progress depends on me getting thing done in a better
way than before.

## Build

```bash
mvn package
```

builds a jar with dependencies than can be used as 'application binary':

```bash
java -jar target/taskdown-jar-with-dependencies.jar list
```

You need to create a configuration file stating the name of the database
and archive directories.

Further installation help on this will follow.

## License

Apache License 2.0
