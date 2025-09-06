package org.example

import java.time.LocalDate
import java.time.LocalTime

class Schedule(
    val start: LocalDate,
    val end: LocalDate? = null,
    val startTime: LocalTime = LocalTime.MIN,
    val endTime: LocalTime = LocalTime.MAX) {

    init {
        require(end == null || start <= end) {
            "endDate should not come before startDate"
        }

        require(!startTime.equals(endTime)) {
            "endTime cannot equal startTime"
        }
    }

    fun isWithInSchedule(date: LocalDate): Boolean {
        if (start > date) return false
        if (end == null) return true
        if (date <= end) return true
        return crossesDayBoundary && date.isEqual(end.plusDays(1))
    }

    fun isWithInSchedule(time: LocalTime, date: LocalDate =  LocalDate.now()) : Boolean {
        if (!isWithInSchedule(date))
            return false

        return if (crossesDayBoundary) time >= startTime || time <= endTime
        else time in startTime..endTime
    }

    val crossesDayBoundary = endTime < startTime
    val recursDaily = true
    val isForever = end == null
}
