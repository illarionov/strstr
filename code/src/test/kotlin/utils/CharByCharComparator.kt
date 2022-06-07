package utils

internal class CharByCharComparator(private val comparator: Comparator<in Char> = naturalOrder()) : java.util.Comparator<String> {
    override fun compare(o1: String, o2: String): Int {
        val str1Array = o1.toCharArray()
        val str2Array = o2.toCharArray()
        for (i in 0..Integer.min(str1Array.lastIndex, str2Array.lastIndex)) {
            val cmp = comparator.compare(str1Array[i], str2Array[i])
            if (cmp != 0) {
                return cmp
            }
        }
        return str1Array.size - str2Array.size
    }
}
