import org.example.Schedule
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import java.time.LocalTime

class ScheduleTest {
    val today = LocalDate.now()!!
    val tomorrow = today.plusDays(1)!!
    val yesterday = today.minusDays(1)!!

    val twoOClock: LocalTime = LocalTime.of(2, 0)
    val threeOClock: LocalTime = LocalTime.of(3, 0)
    val fourOClock: LocalTime = LocalTime.of(4, 0)
    val fiveOClock: LocalTime = LocalTime.of(5, 0)

    @Test
    fun isForever() {
        assertTrue(Schedule(today).isForever)
    }

    @Test
    fun isNotForever_WhenEndProvided() {
        assertFalse(Schedule(yesterday, tomorrow).isForever)
    }

    @Test
    fun shouldRecurDaily() {
        assertTrue(Schedule(today).recursDaily)
    }

    @Test
    fun shouldBeAvailable_OnScheduleDate() {
        assertTrue(Schedule(today, tomorrow).isWithInSchedule(today))
    }

    @Test
    fun shouldNotBeAvailable_OnNonScheduleDate() {
        assertFalse(Schedule(tomorrow, tomorrow).isWithInSchedule(today))
    }

    @Test
    fun shouldBeAvailable_OnTheLastDay() {
        assertTrue(Schedule(yesterday, tomorrow).isWithInSchedule(tomorrow))
    }

    @Test
    fun shouldBeAvailable_OnADayInBetween() {
        assertTrue(Schedule(yesterday, tomorrow).isWithInSchedule(today))
    }

    @Test
    fun illegalArgument_WhenEndDateIsBeforeStartDate() {
        assertThrows<IllegalArgumentException> { Schedule(tomorrow, today) }
    }

    @Test
    fun shouldNotBeAvailable_OnAnyTimeToday() {
        val s = Schedule(tomorrow)
        assertFalse(s.isWithInSchedule(LocalTime.now()))
    }

    @Test
    fun shouldBeAvailable_OnATimeOfADayInSchedule() {
        val s = Schedule(yesterday, tomorrow)
        assertTrue(s.isWithInSchedule(threeOClock, tomorrow))
    }

    @Test
    fun shouldNotBeAvailable_OnATimeBeforeScheduleStart() {
        val s = Schedule(yesterday, today, fourOClock)
        assertFalse(s.isWithInSchedule(threeOClock))
    }

    @Test
    fun shouldNotBeAvailable_OnATimeAfterEnd() {
        val s = Schedule(yesterday, today, threeOClock, fourOClock)
        assertFalse(s.isWithInSchedule(fiveOClock))
    }

    @Test
    fun illegalArgument_WhenStartTimeEqualsEndTime() {
        assertThrows<IllegalArgumentException> {
            Schedule(today, startTime = threeOClock, endTime = threeOClock)
        }
    }

    @Test
    fun crossesDayBoundary_WhenEndComesBeforeStartTime() {
        val s = Schedule(today, startTime = fourOClock, endTime = threeOClock)
        assertTrue(s.crossesDayBoundary)
    }

    @Test
    fun doesNotCrossBoundary_WhenEndComesAfterStartTime() {
        val s = Schedule(today, startTime = threeOClock, endTime = fourOClock)
        assertFalse(s.crossesDayBoundary)
    }

    @Test
    fun isWithInSchedule_IfCrossesBoundaryAndTimeIsAfterStartTime() {
        val s = Schedule(today, startTime = fourOClock, endTime = threeOClock)
        assertTrue(s.isWithInSchedule(fiveOClock))
    }

    @Test
    fun isWithInSchedule_IfCrossesBoundaryAnd_DayIsNotTheFirstDay_AndTimeIsBeforeEndTime() {
        val s = Schedule(today, tomorrow, startTime = fourOClock, endTime = threeOClock)
        assertTrue(s.isWithInSchedule(twoOClock, tomorrow))
    }

    @Test
    fun isNotWithInSchedule_IfCrossesBoundary_IsTheFirstDay_TimeIsBeforeEndTime() {
        val s = Schedule(today, tomorrow, startTime = fourOClock, endTime = threeOClock)
        assertFalse(s.isWithInSchedule(twoOClock, today))
    }

    @Test
    fun theDayAfterTheLastDay_isWithInSchedule_WhenScheduleCrossesBoundary() {
        val s = Schedule(today, today, startTime = fourOClock, endTime = threeOClock)
        assertTrue(s.isWithInSchedule(tomorrow))
    }

    @Test
    fun theTimeAfterStartTimeIsNotWithInSchedule_WhenCrossesBoundaryAndDateIsTheLastDay() {
        val s = Schedule(today, today, fourOClock, threeOClock)
        assertFalse(s.isWithInSchedule(fiveOClock, tomorrow))
    }
}