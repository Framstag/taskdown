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

## Setup

Taskdown needs a configuration file named `taskdown.json`

The taskdown.json file is search for at the following places:

|OS     |Path|
|-------|----|
|Linux  |$HOME/.config/taskdown.json|
|Windows|%HOME%/taskdown.json|

The file contains the path to two existing and read/write accessible directories.

```json
{
  "databaseDir": "/home/tim/projects/taskdown/database",
  "archiveDir": "/home/tim/projects/taskdown/archive"
}
```

### Configuration variables

|Variable   |Description|
|-----------|-----------|
|databaseDir|Directory where the task files (and their backup) are stored|
|archiveDir |Directory where the archived tasks files are stored|

### Environment Variables

Taskdown also evaluates the following environment variables:

|Variable|Description|
|--------|-----------|
|VISUAL  |Name of the editor to use|
|EDITOR  |Name of the editor to use (fallback)|

## Attributes of tasks

A task has the following principle attributes:

|Attribute   |Mandatory|Description|
|------------|---------|-----------|
|id          |x        |A small numerical value, the id is unique and stable but might be reused if the task is archived|
|title       |x        |A one line descriptive title of the task|
|priority    |x        |A priority with one of the following values: A, B, C|
|tags        |         |A possible empty list of tags. Tags are printed with a leading '#'|
|creationDate|x        |The date and time the task was created|
|dueDate     |         |The date and time the task should be fulfilled|
|log         |         |A List of free form log-like one line descriptions with a date and time stamp|
|body        |         |A descriptive text in markdown syntax|

## Walk through example

(We are using the td.sh script to call taskdown under Linux for the following
examples)

Show the empty database:

```bash
./td.sh l
```

Add a new task with priority A:

```bash
./td.sh a -p A "My first task"
  1 A              0 My first task 
```

The created task is shown. It has the id '1', is 0 days old and has the title 'My first task'.

Show the database again:

```bash
./td.sh l                     
  1 A              0 My first task 
```

Add a tag to the task and change priority to priority B:

```bash
./td.sh u -p B -t "zoe" 1
  1 B              0 My first task                            #zoe
```

Tags can be anything, in this case it is a hint, that we have to speak
with Zoe in context of fulfilling the task.

Create another task:

```bash
./td.sh a "Another task"  
  2 C              0 Another task
```

Show the database again:

```bash
./td.sh l                     
  1 B              0 My first task                            #zoe
  2 C              0 Another task
```

As you can see, task a sorted by priority (and age).

Now add a log entry for the first task:

```bash
./td.sh log 1 "Spoke with Zoe"
  1 B              0 My first task
```

Show the task:

```bash
 ./td.sh s 1
# My first task

Id:       1
Priority: B
Creation: 13.10.2021
Tags:     #zoe

Logs:

* 13.10.2021, 08:48:30: Spoke with Zoe
```

Now edit the task:

```bash
 ./td.sh e 1 
  1 B              0 My first task  
```

In turn your editor is called with the database file for the task.
It is a simple Markdown file, so you just add a chapter with notes
regarding the talk with Zoe.

Show the task:

```bash
 ./td.sh s 1
# My first task

Id:       1
Priority: B
Creation: 13.10.2021
Tags:     #zoe

Logs:

* 13.10.2021, 08:48:30: Spoke with Zoe

─────────────────────────── Result of talk with Zoe ───────────────────────────


Do the following:

 • One
 • Two
```

Search for 'talk':

```bash
/td.sh search talk
  1 B              0 My first task
```

Archive the other task, to remove it from the list of open tasks:

```bash
./td.sh archive 2
./td.sh l
  1 B              0 My first task 
```

## License

Apache License 2.0
