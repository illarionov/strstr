package utils

internal fun Comparator<Char>.name(): String {
    return when (this) {
        naturalOrder<Char>() -> "natural"
        reverseOrder<Char>() -> "reverse"
        else -> this.javaClass.simpleName
    }
}
