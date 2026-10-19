package com.example.swpp.tododapp.tasks

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import com.example.swpp.tododapp.data.Task
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import java.time.LocalDate
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * One test per Tip in specs/TasksListBindings-AI.spec.md.
 * Only LocalDate.now() is stubbed: the binding and date-label calculation remain real.
 * Inline static mocking requires Android 9 (API 28) or later.
 */
@SmallTest
@SdkSuppress(minSdkVersion = 28)
@RunWith(AndroidJUnit4::class)
class TasksListBindingsTest {
    @Before
    fun setUpDateControl() {
        mockkStatic(LocalDate::class)
        setToday("2026-10-19")
    }

    @After
    fun restoreDateControl() {
        unmockkStatic(LocalDate::class)
    }

    @Test
    fun setDueDateLabel_completedWithAnyDueDate_showsClear() = onMainThread {
        // Given
        setToday("2026-10-19")
        val textView = labelView("old", View.GONE)
        val tasks = listOf(null, "2026-10-18", "2026-10-19", "2026-10-20")
            .map { task(dueDate = it, isCompleted = true) }
        val expectedLabels = listOf("CLEAR", "CLEAR", "CLEAR", "CLEAR")

        tasks.zip(expectedLabels).forEach { (task, expectedLabel) ->
            // When
            setDueDateLabel(textView, task)

            // Then
            assertLabel(textView, expectedLabel, View.VISIBLE)
        }
    }

    @Test
    fun setDueDateLabel_activeDatedTasks_showsExactLabels() = onMainThread {
        // Given
        setToday("2026-10-19")
        val textView = labelView("old", View.INVISIBLE)
        val tasks = listOf("2026-10-12", "2026-10-19", "2026-10-26", "2026-12-01")
            .map { task(dueDate = it, isCompleted = false) }
        val expectedLabels = listOf("D+7", "D-Day", "D-7", "D-43")

        tasks.zip(expectedLabels).forEach { (task, expectedLabel) ->
            // When
            setDueDateLabel(textView, task)

            // Then
            assertLabel(textView, expectedLabel, View.VISIBLE)
        }
    }

    @Test
    fun setDueDateLabel_activeWithoutDueDate_clearsAndHidesLabel() = onMainThread {
        // Given
        val textView = labelView("D-7", View.VISIBLE)
        val task = task(dueDate = null)

        // When
        setDueDateLabel(textView, task)

        // Then
        assertLabel(textView, "", View.GONE)
    }

    @Test
    fun setDueDateLabel_nullTask_clearsAndHidesLabel() = onMainThread {
        // Given
        val textView = labelView("CLEAR", View.VISIBLE)
        val task: Task? = null

        // When
        setDueDateLabel(textView, task)

        // Then
        assertLabel(textView, "", View.GONE)
    }

    @Test
    fun setDueDateLabel_yesterdayTodayTomorrow_showsOverdueTodayFuture() = onMainThread {
        // Given
        setToday("2026-10-19")
        val textView = labelView("", View.GONE)
        val tasks = listOf("2026-10-18", "2026-10-19", "2026-10-20")
            .map { task(dueDate = it, isCompleted = false) }
        val expectedLabels = listOf("D+1", "D-Day", "D-1")

        tasks.zip(expectedLabels).forEach { (task, expectedLabel) ->
            // When
            setDueDateLabel(textView, task)

            // Then
            assertLabel(textView, expectedLabel, View.VISIBLE)
        }
    }

    @Test
    fun setDueDateLabel_rebindThroughMissingDueDate_clearsAndRestoresLabel() = onMainThread {
        // Given
        val textView = labelView("", View.GONE)
        val futureTask = task("2026-10-20")
        val undatedTask = task(null)
        val overdueTask = task("2026-10-18")

        // When
        setDueDateLabel(textView, futureTask)

        // Then
        assertLabel(textView, "D-1", View.VISIBLE)

        // When
        setDueDateLabel(textView, undatedTask)

        // Then
        assertLabel(textView, "", View.GONE)

        // When
        setDueDateLabel(textView, overdueTask)

        // Then
        assertLabel(textView, "D+1", View.VISIBLE)
    }

    @Test
    fun setDueDateLabel_rebindThroughNullTask_clearsAndRestoresLabel() = onMainThread {
        // Given
        val textView = labelView("", View.GONE)
        val completedTask = task(null, isCompleted = true)
        val dueTodayTask = task("2026-10-19")

        // When
        setDueDateLabel(textView, completedTask)

        // Then
        assertLabel(textView, "CLEAR", View.VISIBLE)

        // When
        setDueDateLabel(textView, null)

        // Then
        assertLabel(textView, "", View.GONE)

        // When
        setDueDateLabel(textView, dueTodayTask)

        // Then
        assertLabel(textView, "D-Day", View.VISIBLE)
    }

    @Test
    fun setDueDateLabel_undatedTaskCompletionChanges_showsAndClearsLabel() = onMainThread {
        // Given
        val textView = labelView("old", View.VISIBLE)
        val activeTask = task(null)
        val completedTask = task(null, isCompleted = true)

        // When
        setDueDateLabel(textView, activeTask)

        // Then
        assertLabel(textView, "", View.GONE)

        // When
        setDueDateLabel(textView, completedTask)

        // Then
        assertLabel(textView, "CLEAR", View.VISIBLE)

        // When
        setDueDateLabel(textView, activeTask)

        // Then
        assertLabel(textView, "", View.GONE)
    }

    @Test
    fun setDueDateLabel_datedTaskCompletionChanges_replacesAndRestoresLabel() = onMainThread {
        // Given
        val textView = labelView("", View.GONE)
        val activeTask = task("2026-10-20")
        val completedTask = task("2026-10-20", isCompleted = true)

        // When
        setDueDateLabel(textView, activeTask)

        // Then
        assertLabel(textView, "D-1", View.VISIBLE)

        // When
        setDueDateLabel(textView, completedTask)

        // Then
        assertLabel(textView, "CLEAR", View.VISIBLE)

        // When
        setDueDateLabel(textView, activeTask)

        // Then
        assertLabel(textView, "D-1", View.VISIBLE)
    }

    @Test
    fun setDueDateLabel_localDateChangesAcrossMidnight_refreshesLabelOnEachCall() = onMainThread {
        // Given
        val textView = labelView("", View.GONE)
        val task = task("2027-01-01")

        // When
        setToday("2026-12-31")
        setDueDateLabel(textView, task)

        // Then
        assertLabel(textView, "D-1", View.VISIBLE)

        // When
        setToday("2027-01-01")
        setDueDateLabel(textView, task)

        // Then
        assertLabel(textView, "D-Day", View.VISIBLE)

        // When
        setToday("2027-01-02")
        setDueDateLabel(textView, task)

        // Then
        assertLabel(textView, "D+1", View.VISIBLE)
    }

    @Test
    fun setDueDateLabel_monthBoundary_countsAllCalendarDays() = onMainThread {
        // Given
        setToday("2026-01-01")
        val textView = labelView("", View.GONE)
        val tasks = listOf("2026-01-31", "2026-02-01", "2026-02-02")
            .map { task(dueDate = it, isCompleted = false) }
        val expectedLabels = listOf("D-30", "D-31", "D-32")

        tasks.zip(expectedLabels).forEach { (task, expectedLabel) ->
            // When
            setDueDateLabel(textView, task)

            // Then
            assertLabel(textView, expectedLabel, View.VISIBLE)
        }
    }

    @Test
    fun setDueDateLabel_yearBoundary_countsAllCalendarDays() = onMainThread {
        // Given
        setToday("2026-12-01")
        val textView = labelView("", View.GONE)
        val tasks = listOf("2026-12-31", "2027-01-01", "2027-01-02")
            .map { task(dueDate = it, isCompleted = false) }
        val expectedLabels = listOf("D-30", "D-31", "D-32")

        tasks.zip(expectedLabels).forEach { (task, expectedLabel) ->
            // When
            setDueDateLabel(textView, task)

            // Then
            assertLabel(textView, expectedLabel, View.VISIBLE)
        }
    }

    @Test
    fun setDueDateLabel_leapDayBoundary_includesLeapDay() = onMainThread {
        // Given
        setToday("2028-02-28")
        val textView = labelView("", View.GONE)
        val tasks = listOf("2028-02-28", "2028-02-29", "2028-03-01")
            .map { task(dueDate = it, isCompleted = false) }
        val expectedLabels = listOf("D-Day", "D-1", "D-2")

        tasks.zip(expectedLabels).forEach { (task, expectedLabel) ->
            // When
            setDueDateLabel(textView, task)

            // Then
            assertLabel(textView, expectedLabel, View.VISIBLE)
        }
    }

    private fun setToday(isoDate: String) {
        val today = LocalDate.parse(isoDate)
        every { LocalDate.now() } returns today
    }

    private fun task(dueDate: String?, isCompleted: Boolean = false) = Task(
        title = "Example",
        description = "Details",
        isCompleted = isCompleted,
        id = "task-1",
        dueDate = dueDate?.let(LocalDate::parse)
    )

    private fun labelView(initialText: String, initialVisibility: Int): TextView {
        val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
        return TextView(context).apply {
            text = initialText
            visibility = initialVisibility
        }
    }

    private fun assertLabel(textView: TextView, expectedText: String, expectedVisibility: Int) {
        assertEquals("Label text", expectedText, textView.text.toString())
        assertEquals("Label visibility", expectedVisibility, textView.visibility)
    }

    private fun onMainThread(block: () -> Unit) {
        // Report assertion failures on the test thread so a failing case cannot crash the UI thread.
        var outcome: Result<Unit>? = null
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            outcome = runCatching(block)
        }
        checkNotNull(outcome).getOrThrow()
    }
}
