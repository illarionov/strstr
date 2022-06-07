package substring

import java.lang.Integer.MAX_VALUE
import kotlin.math.abs

private val IntProgression.length get() = when (this.step) {
    1, -1 -> if (!this.isEmpty()) abs(this.last - this.first) + 1 else 0
    else -> count()
}

class TwoWay {
    internal data class Factorization(val pattern: String,
                                      val left: IntRange,
                                      val right: IntRange,
                                      val rightPeriod: Int) {
        val criticalPosition get() = left.last

        fun leftIsSuffixOfRight(): Boolean {
            return left.all { i -> pattern[i] == pattern[right.first + rightPeriod - left.length + i] }
        }

        init {
            check(criticalPosition >= -1)
            check(left.last + 1 == right.first)
        }
    }

    fun isSubstring(text: String, pattern: String): Boolean {
        when {
            text.isEmpty() -> return pattern.isEmpty()
            pattern.isEmpty() -> return true
        }

        return getAllSubstrings(text, pattern, 1).isNotEmpty()
    }

    internal fun getAllSubstrings(text: String, pattern: String, maxCount: Int = MAX_VALUE): List<Int> {
        when {
            text.isEmpty() -> return emptyList()
            pattern.isEmpty() -> return text.indices.toList()
        }

        val result: MutableList<Int> = mutableListOf()

        val factorization = factorize(pattern)
        if (factorization.leftIsSuffixOfRight()) {
            // CP1
            val (_, left, right, period) = factorization
            var pos = 0
            var memPrefix = -1
            while (pos + pattern.length <= text.length) {
                // Сравнение правой части
                val i = (maxOf(right.first, memPrefix + 1) .. right.last).find { i -> pattern[i] != text[pos + i] }
                if (i != null) {
                    pos = pos + i - right.first + 1
                    memPrefix = -1
                } else {
                    // Сравнение левой части
                    val match = (left.last downTo memPrefix + 1).all { j -> pattern[j] == text[pos + j] }
                    if (match) {
                        result.add(pos)
                        if (result.size >= maxCount) break
                    }
                    pos += period
                    memPrefix = pattern.length - period - 1
                }
            }
        } else {
            // CP2
            val left = factorization.left.reversed()
            val right = factorization.right
            val q = maxOf(left.length, right.length) + 1
            var pos = 0
            while (pos + pattern.length <= text.length) {
                // Сравнение правой части
                val i = right.find { i -> pattern[i] != text[pos + i] }
                if (i != null) {
                    pos = pos + i - right.first + 1
                } else {
                    // Сравнение левой части
                    val match = left.all { j -> pattern[j] == text[pos + j] }
                    if (match) {
                        result.add(pos)
                        if (result.size >= maxCount) break
                    }
                    pos += q
                }
            }
        }
        return result
    }

    private fun factorize(pattern: String): Factorization {
        val naturalOrder = computeMaxSuffixAndPeriod(pattern, naturalOrder())
        val reverseOrder = computeMaxSuffixAndPeriod(pattern, reverseOrder())
        return if (naturalOrder.right.length <= reverseOrder.right.length)
            naturalOrder else reverseOrder
    }

    internal fun computeMaxSuffixAndPeriod(pattern: String, comparator: Comparator<in Char>): Factorization {
        var maxSuffix = -1
        var j = 0
        var k = 1
        var period = 1
        while (j + k < pattern.length) {
            val a = pattern[j + k]
            val b = pattern[maxSuffix + k]
            val abOrder = comparator.compare(a, b)
            when {
                // a < b
                abOrder < 0 -> {
                    j += k
                    k = 1
                    period = j - maxSuffix
                }

                // a == b
                abOrder == 0 -> {
                    if (k != period) {
                        k += 1
                    } else {
                        j += period
                        k = 1
                    }
                }

                // a > b
                else ->  {
                    maxSuffix = j
                    j = maxSuffix + 1
                    k = 1
                    period = 1
                }
            }
        }
        return Factorization(pattern,
                0 .. maxSuffix,
                maxSuffix + 1 .. pattern.lastIndex,
                period
        )
    }
}
