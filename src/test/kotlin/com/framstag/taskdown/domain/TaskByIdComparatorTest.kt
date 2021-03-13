package com.framstag.taskdown.domain

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TaskByIdComparatorTest {

    @Test
    fun compare1_2() {
        val task1 = Task("Task1.md", "Task 1", TaskAttributes(1))
        val task2 = Task("Task2.md", "Task 2", TaskAttributes(2))

        val comparator = TaskByIdComparator()

        Assertions.assertTrue(comparator.compare(task1, task2) > 0)
        Assertions.assertTrue(comparator.compare(task2, task1) < 0)
    }

    @Test
    fun compare1_1() {
        val task1 = Task("Task1.md", "Task 1", TaskAttributes(1))
        val task2 = Task("Task1.md", "Task 1", TaskAttributes(1))

        val comparator = TaskByIdComparator()

        Assertions.assertTrue(comparator.compare(task1, task2) == 0)
        Assertions.assertTrue(comparator.compare(task2, task1) == 0)
    }

    @Test
    fun compareSame() {
        val task1 = Task("Task1.md", "Task 1", TaskAttributes(1))

        val comparator = TaskByIdComparator()

        Assertions.assertTrue(comparator.compare(task1, task1) == 0)
    }
}