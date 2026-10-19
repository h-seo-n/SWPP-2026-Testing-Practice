package com.example.swpp.tododapp.statistics

import com.example.swpp.tododapp.data.Task
import org.junit.Assert.assertEquals
import org.junit.Test

class StatisticsUtilsTest {

    @Test
    fun getActiveAndCompletedStats_two_return50f50f() {
        // GIVEN - One completed task and one active task
        val tasks = listOf(
            Task("t1", "desc", true, "Test1"),
            Task("t2", "desc",false, "Test2")
        )
        // WHEN - Compute the statistics
        val result = getActiveAndCompletedStats(tasks)

        // THEN - Both percentages are 50%
        assertEquals(50f, result.activeTasksPercent)
        assertEquals(50f, result.completedTasksPercent)
    }

    @Test
    fun getActiveAndCompletedStats_zero_return0f0f() {
        val tasks = emptyList<Task>()
        val result = getActiveAndCompletedStats(tasks)
        assertEquals(0f, result.activeTasksPercent)
        assertEquals(0f, result.completedTasksPercent)
    }

    @Test
    fun getActiveAndCompletedStats_two_return0f100f(){
        val tasks = listOf(
            Task("t1", "desc", false, "Test1"),
            Task("t2", "desc", true, "Test2"),
            Task("t3", "desc", true, "Test3"),
            Task("t4", "desc", true, "Test4")

        )
        val result = getActiveAndCompletedStats(tasks)
        assertEquals(25f, result.activeTasksPercent)
        assertEquals(75f, result.completedTasksPercent)
    }
}

