package com.framstag.taskdown.domain

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TaskByPriorityComparatorTest {

    @Test
    fun compareAB() {
        val taskA = Task("Task1.md","Task 1",TaskAttributes(1).withPriority(Priority.A))
        val taskB = Task("Task2.md","Task 2",TaskAttributes(2).withPriority(Priority.B))

        val comparator = TaskByPriorityComparator()

        Assertions.assertTrue(comparator.compare(taskA, taskB)<0)
        Assertions.assertTrue(comparator.compare(taskB, taskA)>0)
    }

    @Test
    fun compareAA() {
        val taskA = Task("Task1.md", "Task 1", TaskAttributes(1).withPriority(Priority.A))
        val taskB = Task("Task2.md", "Task 2", TaskAttributes(2).withPriority(Priority.A))

        val comparator = TaskByPriorityComparator()

        Assertions.assertTrue(comparator.compare(taskA, taskB) == 0)
        Assertions.assertTrue(comparator.compare(taskB, taskA) == 0)
    }

    @Test
    fun compareSame() {
        val taskA = Task("Task1.md", "Task 1", TaskAttributes(1).withPriority(Priority.A))

        val comparator = TaskByPriorityComparator()

        Assertions.assertTrue(comparator.compare(taskA, taskA) == 0)
    }
}