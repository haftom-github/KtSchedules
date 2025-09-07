package org.example.sequence

object SequenceFactory {
    fun create(start: Int, end: Int?, interval: Int): ISequence {
        return when (end) {
            null -> InfiniteSequence(start, interval)
            else -> FiniteSequence(start, end, interval)
        }
    }
}