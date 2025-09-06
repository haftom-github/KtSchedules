package org.example

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class Schedule(
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime? = null,
    startTime: LocalTime = LocalTime.MIN,
    endTime: LocalTime = LocalTime.MAX
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

    fun updateRecurrence(type: RecurrenceType) {
        recurrenceType = type
    }

    fun updateRecurrence(interval: Int){
        require(interval > 0) {
            "recurrence interval can only be a positive integer"
        }
        recurrenceInterval = interval
    }

    fun periodsAt(date: LocalDate) : Array<Period> {
        if (date < startDateTime.toLocalDate() || (endDateTime != null && date > endDateTime.toLocalDate()))
            return arrayOf()
        return if (!crossesDayBoundary) arrayOf(Period())
        else arrayOf(Period(), Period())
    }
}

enum class RecurrenceType {
    Daily, Weekly
}
