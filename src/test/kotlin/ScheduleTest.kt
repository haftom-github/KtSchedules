import org.example.RecurrenceType
import org.example.Schedule
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import java.time.LocalTime

class ScheduleTest {
    val today = LocalDate.now()!!
    val tomorrow = today.plusDays(1)!!
    val yesterday = today.minusDays(1)!!

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
        assertTrue(Schedule(today).recursDaily())
    }

    @Test
    fun shouldRecurWeekly(){
        val s = Schedule(today)
        s.updateRecurrence(RecurrenceType.Weekly)
        assertTrue(s.recursWeekly())
        assertFalse(s.recursDaily())
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
        assertEquals(0, s.periodsAt(yesterday).size)
    }

    @Test
    fun shouldReturn_ASingleFullDayPeriod_forFullDaySchedules(){
        val s = Schedule(today)
        val periods = s.periodsAt(today)
        assertEquals(1, periods.size)
        assertTrue(periods[0].isFullDay)
    }

    @Test
    fun shouldReturn_TwoPeriods_WhenDayBoundaryIsCrossed() {
        val s = Schedule(today, null, fourOClock, twoOClock)
        val periods = s.periodsAt(tomorrow)
        assertEquals(2, periods.size)
    }

    @Test
    fun startAndEndOfPeriodShouldEqualStartTime_whenDayBoundaryNotCrossed(){
        val s = Schedule(today, null, twoOClock, threeOClock)
        val periods = s.periodsAt(today)
        assertEquals(twoOClock, periods[0].startTime)
        assertEquals(threeOClock, periods[0].endTime)
    }

    @Test
    fun secondPeriod_ShouldMatchAfterMidNightSchedule_WhenDayBoundaryIsCrossed() {
        val s = Schedule(today, null, threeOClock, twoOClock)
        val firstPeriod = s.periodsAt(tomorrow)[1]
        assertEquals(LocalTime.MIN, firstPeriod.startTime)
        assertEquals(twoOClock, firstPeriod.endTime)
    }

    @Test
    fun firstPeriod_ShouldMatchBeforeMidNight_WhenDayBoundaryCrossed(){
        val s = Schedule(today, null, threeOClock, twoOClock)
        val secondPeriod = s.periodsAt(today)[0]
        assertEquals(threeOClock, secondPeriod.startTime)
        assertEquals(LocalTime.MAX, secondPeriod.endTime)
    }

    @Test
    fun thereCanNotBeTwoPeriods_WhenIsRecurringDaily_AtMoreThanOneInterval_AndCrossesBoundary(){
        val s = Schedule(today, startTime = fiveOClock, endTime = threeOClock)
        assertEquals(1, s.periodsAt(today).size)
    }
}