package org.example.sequence

class FiniteSequence(
    override val start: Int,
    override val end: Int,
    override val interval: Int = 1,
) : ISequence {

    init {
        require(interval > 0){
            "interval must be a positive integer."
        }
    }

    override val isFinite: Boolean = true

    override val length: Int
        get() = floor(end - start, interval) + 1

    override val isEmpty: Boolean = length < 1

    override fun s(n: Int): Int {
        require(n >= 0){
            "index must be greater than or equal to 0."
        }

        val maxN = (end - start) / interval
        if(n > maxN)
            throw IllegalArgumentException("index must be less than or equal to $maxN.")

        return start + n * interval
    }

    override fun startFromIndex(n: Int): ISequence {
        return FiniteSequence(start + n * interval, end, interval)
    }

    override fun isMember(x: Int): Boolean {
        return x in start..end && (x - start) % interval == 0;
    }

    override fun collapseToRangeOf(other: ISequence): ISequence {
        val n = ceil(other.start - start, interval)
        val newStart = maxOf(start, start + n * interval)
        return FiniteSequence(
            newStart,
            minOf(end, other.end ?: (end + 1)),
            interval
        )
    }
}