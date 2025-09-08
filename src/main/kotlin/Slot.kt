package org.example

import java.time.LocalTime

class Slot (
    val startTime: LocalTime = LocalTime.MIN,
    val endTime: LocalTime = LocalTime.MAX
) {
    val isFullDay = startTime.equals(LocalTime.MIN)
            && endTime.equals(LocalTime.MAX)
}