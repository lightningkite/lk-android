package lk.kotlin.utils.bytes


/**
 * A mutable container for a long integer that allows access to individual bits.
 * Created by shanethompson on 6/29/17.
 */
class LongBitArray(var value: Long = 0) : Iterable<Boolean> {

    /**
     * Gets whether or not the bit at [index] is on.
     * Index starts with the right-most bit at zero and increases going left.
     */
    operator fun get(index: Int): Boolean {
        return value.ushr(index).and(0x1L) == 1L
    }

    /**
     * Sets whether or not the bit at [index] is on.
     * Index starts with the right-most bit at zero and increases going left.
     */
    operator fun set(index: Int, isTrue: Boolean) {
        if (isTrue) {
            value = value or 0x1L.shl(index)
        } else {
            value = value.and(1L.shl(index).inv())
        }
    }

    /**
     * Allows you to iterate through all 32 bits from right-most to left-most.
     */
    override fun iterator(): Iterator<Boolean> {
        return object : Iterator<Boolean> {
            var currentIndex = 0
            override fun hasNext(): Boolean {
                return currentIndex < 64
            }

            override fun next(): Boolean {
                val result = this@LongBitArray[currentIndex]
                currentIndex++
                return result
            }
        }
    }
}

/**
 * Creates a [LongBitArray] that represents the given integer, allowing for easy access of individual bits.
 */
fun Long.toBitArray(): LongBitArray {
    return LongBitArray(this)
}
