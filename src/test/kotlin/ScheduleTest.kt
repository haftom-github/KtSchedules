import org.example.RecurrenceType
import org.example.Schedule
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import kotlin.test.assertContains

class ScheduleTest {
    val today = LocalDate.now()!!
    val tomorrow = today.plusDays(1)!!
    val yesterday = today.minusDays(1)!!
    val afterTomorrow = tomorrow.plusDays(1)!!

    val twoOClock = LocalTime.of(2, 0)!!
    val threeOClock = LocalTime.of(3, 0)!!
    val fourOClock = LocalTime.of(4, 0)!!
    val fiveOClock = LocalTime.of(5, 0)!!

    @Test
    fun isForever(){
        assertTrue(Schedule(today).isForever)
        assertTrue(Schedule(today.atStartOfDay()).isForever)
    }

    @Test
    fun isNotForEver_IfEndSpecified(){
        assertFalse(Schedule(today, tomorrow).isForever)
        assertFalse(Schedule(today.atStartOfDay(), tomorrow.atStartOfDay()).isForever)
    }

    @Test
    fun endShouldNotComeBeforeStart(){
        assertThrows<IllegalArgumentException>{
            Schedule(tomorrow, today)
        }
    }

    @Test
    fun shouldRecurDaily(){
        assertEquals(RecurrenceType.Daily, Schedule(today).recurrenceType)
    }

    @Test
    fun hasRecurrenceIntervalOf_One(){
        assertEquals(1, Schedule(today).recurrenceInterval)
    }

    @Test
    fun shouldNotAllowANonPositiveRecurrenceInterval(){
        val s = Schedule(today)
        assertThrows<IllegalArgumentException>{
            s.updateRecurrence(interval = 0)
        }
    }

    @Test
    fun startTimeCanNotBeEqualToEndTime() {
        assertThrows<IllegalArgumentException>{
            Schedule(today, tomorrow, threeOClock, threeOClock)
        }
    }

    @Test
    fun crossBoundary_Allowed(){
        val s = Schedule(today, startTime = fourOClock, endTime = threeOClock)
        assertTrue(s.crossesDayBoundary)
    }

    @Test
    fun doesNotCrossBoundary_WhenStartTimeComesBeforeEndTime() {
        val s = Schedule(today, startTime = threeOClock, endTime = fourOClock)
        assertFalse(s.crossesDayBoundary)
    }

    @Test
    fun shouldReturnEmpty_WhenDateNotWithInSchedule(){
        val s = Schedule(today)
        assertEquals(0, s.slotsAtDate(yesterday).size)
    }

    @Test
    fun shouldReturn_ASingleFullDayPeriod_forFullDaySchedules(){
        val s = Schedule(today)
        val slots = s.slotsAtDate(today)
        assertEquals(1, slots.size)
        assertTrue(slots[0].isFullDay)
    }

    @Test
    fun shouldReturn_TwoPeriods_WhenDayBoundaryIsCrossed() {
        val s = Schedule(today, null, fourOClock, twoOClock)
        val slots = s.slotsAtDate(tomorrow)
        assertEquals(2, slots.size)
    }

    @Test
    fun startAndEndOfPeriodShouldEqualStartTime_whenDayBoundaryNotCrossed(){
        val s = Schedule(today, null, twoOClock, threeOClock)
        val slots = s.slotsAtDate(today)
        assertEquals(twoOClock, slots[0].startTime)
        assertEquals(threeOClock, slots[0].endTime)
    }

    @Test
    fun secondSlot_ShouldMatchAfterMidNightSchedule_WhenDayBoundaryIsCrossed() {
        val s = Schedule(today, null, threeOClock, twoOClock)
        val firstSlot = s.slotsAtDate(tomorrow)[1]
        assertEquals(LocalTime.MIN, firstSlot.startTime)
        assertEquals(twoOClock, firstSlot.endTime)
    }

    @Test
    fun firstSlot_ShouldMatchBeforeMidNight_WhenDayBoundaryCrossed(){
        val s = Schedule(today, null, threeOClock, twoOClock)
        val secondSlot = s.slotsAtDate(today)[0]
        assertEquals(threeOClock, secondSlot.startTime)
        assertEquals(LocalTime.MAX, secondSlot.endTime)
    }

    @Test
    fun thereCanNotBeTwoPeriods_WhenIsRecurringDaily_AtMoreThanOneInterval_AndCrossesBoundary(){
        val s = Schedule(today, startTime = fiveOClock, endTime = threeOClock)
        assertEquals(1, s.slotsAtDate(today).size)
    }

    // intervals different from 1
    @Test
    fun evenDistanceFromStart_shouldResultInAPeriodBeforeMidnight_WhenDaily_EveryTwoDays_CrossesBoundary() {
        val s = Schedule(yesterday, startTime = fiveOClock, endTime = fourOClock)
        s.updateRecurrence(interval = 2)

        var slots = s.slotsAtDate(yesterday)
        assertEquals(1, slots.size)
        assertEquals(LocalTime.MAX, slots[0].endTime)
        assertEquals(s.startTime, slots[0].startTime)

        slots = s.slotsAtDate(tomorrow)
        assertEquals(1, slots.size)
        assertEquals(LocalTime.MAX, slots[0].endTime)
        assertEquals(s.startTime, slots[0].startTime)
    }

    @Test
    fun oddDistanceFromStart_shouldResultInAPeriodAfterMidnight_WhenDaily_EveryTwoDays_crossesBoundary(){
        val s = Schedule(yesterday, startTime = fiveOClock, endTime = fourOClock)
        s.updateRecurrence(interval = 2)

        var slots = s.slotsAtDate(today)
        assertEquals(1, slots.size)
        assertEquals(s.endTime, slots[0].endTime)
        assertEquals(LocalTime.MIN, slots[0].startTime)

        slots = s.slotsAtDate(afterTomorrow)
        assertEquals(1, slots.size)
        assertEquals(s.endTime, slots[0].endTime)
        assertEquals(LocalTime.MIN, slots[0].startTime)
    }

    @Test
    fun emptyPeriods_whenDateNotInSchedule(){
        val s = Schedule(yesterday, startTime = fiveOClock, endTime = fourOClock)
        s.updateRecurrence(interval = 3)

        assertEquals(0, s.slotsAtDate(tomorrow).size)
    }

    @Test
    fun shouldUpdateRecurrence(){
        val s = Schedule(today)

        s.updateRecurrence(interval = 2)
        assertEquals(2, s.recurrenceInterval)

        s.updateRecurrence(type = RecurrenceType.Weekly)
        assertEquals(2, s.recurrenceInterval)
        assertEquals(RecurrenceType.Weekly, s.recurrenceType)

        s.updateRecurrence(RecurrenceType.Daily, 3)
        assertEquals(RecurrenceType.Daily, s.recurrenceType)
        assertEquals(3, s.recurrenceInterval)
    }

    // weekly recurrences
    @Test
    fun shouldRecurWeekly_AtASpecifiedInterval(){
        val s = Schedule(today)

        s.updateRecurrence(RecurrenceType.Weekly, daysOfWeek = hashSetOf(DayOfWeek.MONDAY))
        assertEquals(RecurrenceType.Weekly, s.recurrenceType)
        assertEquals(1, s.daysOfWeek.size)
        assertContains(s.daysOfWeek, DayOfWeek.MONDAY)

        s.updateRecurrence(RecurrenceType.Weekly, daysOfWeek = hashSetOf(), interval = 5)
        assertEquals(5, s.recurrenceInterval)
        assertEquals(0, s.daysOfWeek.size)
        assertEquals(RecurrenceType.Weekly, s.recurrenceType)
    }
}