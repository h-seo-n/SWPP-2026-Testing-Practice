package com.example.swpp.tododapp.tasks

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

/** One test per Tip in specs/DueDateUtils-AI.spec.md. */
class DueDateUtilsTest {
    @Test
    fun dueDateLabel_completedWithAnyDueDate_returnsClear() {
        // Given
        val today = LocalDate.parse("2026-10-19")
        val dueDates = listOf(null, "2026-10-18", "2026-10-19", "2026-10-20")
            .map { it?.let(LocalDate::parse) }

        // When
        val labels = dueDates.map { dueDateLabel(it, today, isCompleted = true) }

        // Then
        assertEquals(listOf("CLEAR", "CLEAR", "CLEAR", "CLEAR"), labels)
    }

    @Test
    fun dueDateLabel_activeWithoutDueDate_returnsEmptyString() {
        // Given
        val today = LocalDate.parse("2026-10-19")
        val dueDate = null

        // When
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // Then
        assertEquals("", label)
    }

    @Test
    fun dueDateLabel_activeDueToday_returnsDDay() {
        // Given
        val today = LocalDate.parse("2026-10-19")
        val dueDate = LocalDate.parse("2026-10-19")

        // When
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // Then
        assertEquals("D-Day", label)
    }

    @Test
    fun dueDateLabel_activeDueIn43Days_returnsDMinus43() {
        // Given
        val today = LocalDate.parse("2026-10-19")
        val dueDate = LocalDate.parse("2026-12-01")

        // When
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // Then
        assertEquals("D-43", label)
    }

    @Test
    fun dueDateLabel_activeOverdue365Days_returnsDPlus365() {
        // Given
        val today = LocalDate.parse("2026-10-19")
        val dueDate = LocalDate.parse("2025-10-19")

        // When
        val label = dueDateLabel(dueDate, today, isCompleted = false)

        // Then
        assertEquals("D+365", label)
    }

    @Test
    fun dueDateLabel_yesterdayTodayTomorrow_returnsOverdueTodayFuture() {
        // Given
        val today = LocalDate.parse("2026-10-19")
        val dueDates = listOf("2026-10-18", "2026-10-19", "2026-10-20").map(LocalDate::parse)

        // When
        val labels = dueDates.map { dueDateLabel(it, today, isCompleted = false) }

        // Then
        assertEquals(listOf("D+1", "D-Day", "D-1"), labels)
    }

    @Test
    fun dueDateLabel_futureMonthBoundary_countsAllDays() {
        // Given
        val today = LocalDate.parse("2026-01-01")
        val dueDates = listOf("2026-01-31", "2026-02-01", "2026-02-02").map(LocalDate::parse)

        // When
        val labels = dueDates.map { dueDateLabel(it, today, isCompleted = false) }

        // Then
        assertEquals(listOf("D-30", "D-31", "D-32"), labels)
    }

    @Test
    fun dueDateLabel_overdueMonthBoundary_countsAllDays() {
        // Given
        val today = LocalDate.parse("2026-03-03")
        val dueDates = listOf("2026-01-31", "2026-02-01", "2026-02-02").map(LocalDate::parse)

        // When
        val labels = dueDates.map { dueDateLabel(it, today, isCompleted = false) }

        // Then
        assertEquals(listOf("D+31", "D+30", "D+29"), labels)
    }

    @Test
    fun dueDateLabel_futureYearBoundary_countsAllDays() {
        // Given
        val today = LocalDate.parse("2026-12-01")
        val dueDates = listOf("2026-12-31", "2027-01-01", "2027-01-02").map(LocalDate::parse)

        // When
        val labels = dueDates.map { dueDateLabel(it, today, isCompleted = false) }

        // Then
        assertEquals(listOf("D-30", "D-31", "D-32"), labels)
    }

    @Test
    fun dueDateLabel_overdueYearBoundary_countsAllDays() {
        // Given
        val today = LocalDate.parse("2027-02-01")
        val dueDates = listOf("2026-12-31", "2027-01-01", "2027-01-02").map(LocalDate::parse)

        // When
        val labels = dueDates.map { dueDateLabel(it, today, isCompleted = false) }

        // Then
        assertEquals(listOf("D+32", "D+31", "D+30"), labels)
    }

    @Test
    fun dueDateLabel_futureLeapDayBoundary_includesLeapDay() {
        // Given
        val today = LocalDate.parse("2028-02-28")
        val dueDates = listOf("2028-02-28", "2028-02-29", "2028-03-01").map(LocalDate::parse)

        // When
        val labels = dueDates.map { dueDateLabel(it, today, isCompleted = false) }

        // Then
        assertEquals(listOf("D-Day", "D-1", "D-2"), labels)
    }

    @Test
    fun dueDateLabel_overdueLeapDayBoundary_includesLeapDay() {
        // Given
        val today = LocalDate.parse("2028-03-01")
        val dueDates = listOf("2028-02-28", "2028-02-29", "2028-03-01").map(LocalDate::parse)

        // When
        val labels = dueDates.map { dueDateLabel(it, today, isCompleted = false) }

        // Then
        assertEquals(listOf("D+2", "D+1", "D-Day"), labels)
    }

    @Test
    fun dueDateLabel_nonLeapFebruaryBoundary_endsOnFebruary28() {
        // Given
        val today = LocalDate.parse("2027-02-27")
        val dueDates = listOf("2027-02-27", "2027-02-28", "2027-03-01").map(LocalDate::parse)

        // When
        val labels = dueDates.map { dueDateLabel(it, today, isCompleted = false) }

        // Then
        assertEquals(listOf("D-Day", "D-1", "D-2"), labels)
    }
}
