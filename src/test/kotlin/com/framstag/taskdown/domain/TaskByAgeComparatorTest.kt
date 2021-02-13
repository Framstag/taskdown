package com.framstag.taskdown.domain

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDate

class TaskByAgeComparatorTest {

    @Test
    fun compareTwoDates() {
        val dateNow = LocalDate.now()
        val taskNow = Task("Task1.md","Task 1",TaskAttributes(1).withCreationDate(dateNow),"")
        val dateTomorrow = dateNow.plusDays(1)
        val taskTomorrow = Task("Task2.md","Task 2",TaskAttributes(2).withCreationDate(dateTomorrow),"")

        val comparator = TaskByAgeComparator()

        Assertions.assertTrue(comparator.compare(taskNow, taskTomorrow)<0)
        Assertions.assertTrue(comparator.compare(taskTomorrow, taskNow)>0)
    }

    @Test
    fun compareOneNull() {
        val dateNow = LocalDate.now()
        val taskNow = Task("Task1.md","Task 1",TaskAttributes(1).withCreationDate(dateNow),"")
        val taskTomorrow = Task("Task2.md","Task 2",TaskAttributes(2),"")

        val comparator = TaskByAgeComparator()

        Assertions.assertTrue(comparator.compare(taskNow, taskTomorrow)<0)
        Assertions.assertTrue(comparator.compare(taskTomorrow, taskNow)>0)
    }

    @Test
    fun compareBothNull() {
        val taskNow = Task("Task1.md","Task 1",TaskAttributes(1),"")
        val taskTomorrow = Task("Task2.md","Task 2",TaskAttributes(2),"")

        val comparator = TaskByAgeComparator()

        Assertions.assertTrue(comparator.compare(taskNow, taskTomorrow)==0)
        Assertions.assertTrue(comparator.compare(taskTomorrow, taskNow)==0)
    }
}