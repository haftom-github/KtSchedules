package org.example

import java.time.LocalTime

class Period (
    startTime: LocalTime = LocalTime.MIN,
    endTime: LocalTime = LocalTime.MAX
) {
    val isFullDay = startTime.equals(LocalTime.MIN)
            && endTime.equals(LocalTime.MAX)
}