package org.example.sequence

class InfiniteSequence(
    override val start: Int,
    override val interval: Int
) : ISequence {

    override val end = null
    override val isFinite = false
    override val length = null
    override val isEmpty = false

    override fun s(n: Int): Int {
        require(n >= 0){
            "index must be a non-negative integer."
        }

        return start + n * interval
    }

    override fun startFromIndex(n: Int): ISequence {
        return InfiniteSequence(s(n), interval)
    }

    override fun isMember(x: Int): Boolean {
        return x >= start && (x - start) % interval == 0
    }

    override fun collapseToRangeOf(other: ISequence): ISequence {
        val n = ceil(other.start - start, interval)
        val newStart = maxOf(start, start + n * interval)

        return SequenceFactory.create(newStart, other.end, interval)
    }
}