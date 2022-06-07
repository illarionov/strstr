package utils

internal fun String.short() = if (this.length > 20) "${this.take(10)}…${this.take(3)}" else this

internal fun String.getMaxSuffixNaive(comparator: Comparator<Char> = naturalOrder()): String {
    return (0..this.length)
        .map(this::drop)
        .sortedWith(CharByCharComparator(comparator))
        .lastOrNull() ?: ""
}

internal fun String.getAllSubstringsNative(needle: String): List<Int> {
    val result = mutableListOf<Int>()
    var startIndex = 0
    while (startIndex <= this.lastIndex) {
        val index = this.indexOf(needle, startIndex, false)
        if (index >= 0) {
            result += index
            startIndex = index + 1
        } else {
            break
        }
    }
    return result
}


internal fun String.getMinimalPeriod(): Int {
    val zTable = getZFunction()
    for (i in this.indices) {
        if (i + zTable[i] == this.length) {
            return i
        }
    }
    return this.length
}

private fun String.getZFunction(): List<Int> {
    val z = IntArray(this.length)
    var left = 0
    var right = 0
    for (i in 1 until this.length) {
        if (i > right) {
            right = i
            left = right
            while (right < this.length && this[right - left] == this[right]) right++
            z[i] = right - left
            right--
        } else {
            val k = i - left
            if (z[k] < right - i + 1) z[i] = z[k] else {
                left = i
                while (right < this.length && this[right - left] == this[right]) right++
                z[i] = right - left
                right--
            }
        }
    }
    return z.toList()
}
