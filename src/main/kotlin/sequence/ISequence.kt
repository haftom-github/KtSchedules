package org.example.sequence

interface ISequence {
    val start: Int
    val end: Int?
    val interval: Int
    val isFinite: Boolean
    val isInfinite: Boolean
        get() = !isFinite
    val length: Int?
    val isEmpty: Boolean
    fun s(n: Int): Int
    fun startFromIndex(n: Int): ISequence
    fun isMember(x: Int): Boolean
    fun collapseToRangeOf(other: ISequence): ISequence

    fun floor(a: Int, b: Int): Int {
        return if ((a xor b) < 0) {
            if (a < 0) (a - b + 1) / b else (a - b - 1) / b
        } else {
            a / b
        }
    }

    fun ceil(a: Int, b: Int): Int {
        return if ((a xor b) < 0) {
            a / b
        } else {
            if (a < 0) (a + b + 1) / b else (a + b - 1) / b
        }
    }
}