import substring.TwoWay
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val twoWay = TwoWay()
    if (args.size != 2) {
        System.err.println("Usage: strstr <NEEDLE> <HAYSTACK>")
        exitProcess(1)
    }
    val (needle, haystack) = args
    println(twoWay.isSubstring(haystack, needle))
}
