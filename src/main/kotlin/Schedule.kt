package org.example

import org.example.sequence.ISequence
import org.example.sequence.SequenceFactory
import java.time.DayOfWeek
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
    val daysOfWeek = mutableSetOf<DayOfWeek>()
    val isForever = endDateTime == null
    val crossesDayBoundary = startTime > endTime

    fun updateRecurrence(type: RecurrenceType? = null, interval: Int? = null) {
        if(interval != null) {
            require(interval > 0) {
                "recurrence interval can only be a positive integer"
            }

            recurrenceInterval = interval
        }
        if(type != null) recurrenceType = type
    }

    fun recurWeekly(daysOfWeek: Set<DayOfWeek>) {
        this.daysOfWeek.clear()
        this.daysOfWeek.addAll(daysOfWeek)
    }

    fun periodsAtDate(date: LocalDate) : List<Period> {
        val sequenceMap = toSequencesMap()
        val periods: MutableList<Period> = mutableListOf()
        for ((key, sequences) in sequenceMap) {
            for (sequence in sequences) {
                if (sequence.isMember(date.toEpochDay().toInt())){
                    periods.add(
                        when(key){
                            "before" -> periodBeforeMidNight()
                            else -> periodAfterMidNight()
                        }
                    )
                    break
                }
            }
        }
        return periods
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

    private fun toSequencesMap(): Map<String, Array<ISequence>> {
        val splits = split()
        val keys = arrayOf("before", "after")
        val sequencesMap = HashMap<String, Array<ISequence>>()

        for (i in 0 until splits.size) {
            sequencesMap[keys[i]] = arrayOf(
                SequenceFactory.create(
                    splits[i].startDateTime.toLocalDate().toEpochDay().toInt(),
                    splits[i].endDateTime?.toLocalDate()?.toEpochDay()?.toInt(),
                    recurrenceInterval
                )
            )
        }

        return sequencesMap
    }

    private fun periodBeforeMidNight() : Period {
        return when (crossesDayBoundary) {
            true -> Period(startTime)
            false -> Period(startTime, endTime)
        }
    }

    private fun periodAfterMidNight() : Period {
        return when (crossesDayBoundary) {
            true -> Period(endTime = endTime)
            false -> Period(startTime, endTime)
        }
    }
}

enum class RecurrenceType {
    Daily, Weekly
}
