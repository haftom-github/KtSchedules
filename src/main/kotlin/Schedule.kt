package org.example

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class Schedule(
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime? = null,
    val startTime: LocalTime = LocalTime.MIN,
    val endTime: LocalTime = LocalTime.MAX,
) {
    init {
        require(endDateTime == null || startDateTime <= endDateTime){
            "end of schedule should not come before its start"
        }

        require(!startTime.equals(endTime)) {
            "start time and end time can not be equal"
        }
    }

    constructor(
        startDate: LocalDate,
        endDate: LocalDate? = null,
        startTime: LocalTime = LocalTime.MIN,
        endTime: LocalTime = LocalTime.MAX
    ): this(
        startDate.atStartOfDay(),
        endDate?.atStartOfDay(),
        startTime, endTime
    )

    var recurrenceType = RecurrenceType.Daily
    var recurrenceInterval = 1
    fun recursDaily() = recurrenceType == RecurrenceType.Daily
    fun recursWeekly() = recurrenceType == RecurrenceType.Weekly
    val isForever = endDateTime == null
    val crossesDayBoundary = startTime > endTime

    fun updateRecurrence(type: RecurrenceType? = null, interval: Int = 1) {
        require(interval > 0) {
            "recurrence interval can only be a positive integer"
        }
        recurrenceInterval = interval
        if(type != null) recurrenceType = type
    }

    fun periodsAt(date: LocalDate) : Array<Period> {
        if (date < startDateTime.toLocalDate() || (endDateTime != null && date > endDateTime.toLocalDate()))
            return arrayOf()
        if (!crossesDayBoundary) return arrayOf(Period(startTime, endTime))
        val splits = split()
        return splits[0].periodsAt(date) + splits[1].periodsAt(date)
    }

    private fun split(): Array<Schedule> {
        if (!crossesDayBoundary) return arrayOf(this)

        val beforeMidnight = Schedule(startDateTime, endDateTime, startTime, LocalTime.MAX)
        beforeMidnight.updateRecurrence(recurrenceType, recurrenceInterval)

        val afterMidnight =
            Schedule(
                startDateTime.plusDays(1).toLocalDate(),
                endDateTime?.plusDays(1)?.toLocalDate(),
                LocalTime.MIN, endTime)

        afterMidnight.updateRecurrence(recurrenceType, recurrenceInterval)
        return arrayOf(beforeMidnight, afterMidnight)
    }
}

enum class RecurrenceType {
    Daily, Weekly
}
