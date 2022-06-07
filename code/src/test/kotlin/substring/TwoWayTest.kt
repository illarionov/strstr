package substring

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.az
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.exhaustive
import utils.getAllSubstringsNative
import utils.getMaxSuffixNaive
import utils.getMinimalPeriod
import utils.name
import utils.short

class TwoWayTest : FunSpec({
    val twoWayClass = TwoWay()

    fun TwoWay.Factorization.leftSubstring() = pattern.subSequence(left)
    fun TwoWay.Factorization.rightSubstring() = pattern.subSequence(right)

    data class SubstringTestCase(
        val haystack: String,
        val needle: String
    ) {
        val expectedResult: List<Int> get() = haystack.getAllSubstringsNative(needle)

        operator fun component3() = expectedResult
    }

    fun <T>List<T>.joinShort(): String = if (this.isNotEmpty()) this.joinToString(",", limit=10) else "-"

    context("getAllSubstrings") {
        context("should return all substrings correctly") {
            withData(
                nameFn = { "'${it.needle.short()}' in '${it.haystack.short()}': ${it.expectedResult.joinShort()}" },
                SubstringTestCase(haystack = "", needle = ""),
                SubstringTestCase(haystack = "", needle = "aa"),
                SubstringTestCase(haystack = "a", needle = "a"),
                SubstringTestCase(haystack = "a", needle = "b"),
                SubstringTestCase(haystack = "aa", needle = "a"),
                SubstringTestCase(haystack = "abc", needle = "ab"),
                SubstringTestCase(haystack = "abc", needle = "bc"),
                SubstringTestCase(haystack = "abc", needle = "bd"),
                SubstringTestCase(haystack = "abc", needle = "bd"),
                SubstringTestCase(haystack = "aaabc", needle = "ab"),
                SubstringTestCase(haystack = "aaabc", needle = "abс"),
                SubstringTestCase(haystack = "aaabc", needle = "abd"),
                SubstringTestCase(haystack = "aaabcc", needle = "aab"),
                SubstringTestCase(haystack = "aaabcc", needle = "bcc"),
                SubstringTestCase(haystack = "aaabcc", needle = "bd"),
                SubstringTestCase(haystack = "bd", needle = "aabbdddbd"),

                SubstringTestCase(haystack = "abcd", needle = "abd"),
                SubstringTestCase(haystack = "abcd", needle = "e"),
                SubstringTestCase(haystack = "abc", needle = ""),
                SubstringTestCase(haystack = "abc", needle = "ab"),
                SubstringTestCase(haystack = "abc", needle = "bc"),
                SubstringTestCase(haystack = "abc", needle = "abc"),
                SubstringTestCase(haystack = "aaababcd", needle = "abc"),
                SubstringTestCase(haystack = "aaababab", needle = "abc"),
                SubstringTestCase(haystack = "aaab", needle = "aab"),
                SubstringTestCase(haystack = "ababc", needle = "abc"),
                SubstringTestCase(haystack = "abcdef", needle = "cd"),
                SubstringTestCase(haystack = "ababracadabraf", needle = "abracadabra"),
                SubstringTestCase(haystack = "yov", needle = "o"),
                SubstringTestCase(haystack = "ddviv", needle = "viv"),

                SubstringTestCase(haystack = "ababracadabraabafabaabaf", needle = "aaaaabaaaaabaaaaa"),
                SubstringTestCase(haystack = "ababracadabraabafaaaaaabaaaaabaaaaabaabaf", needle = "aaaaabaaaaaabaaaaaa"),
                SubstringTestCase(haystack = "ababracadabraabafaaaaabaaaaaabaaaaaaaaaaabaaaaaabaaaaaaf", needle = "aaaaabaaaaaabaaaaaa"),
                SubstringTestCase(haystack = "ababracadabraabafaabaaabaaaaaaaabaaabaaabaaaf", needle = "aabaaabaaa"),

                SubstringTestCase(haystack = "A".repeat(1000000) + 'B', needle = "A".repeat(62) + "B"),
                SubstringTestCase(haystack = "A".repeat(100000) + 'B', needle = "A".repeat(50000) + "B"),
            ) { (haystack, needle, expectedResult) ->
                val result = twoWayClass.getAllSubstrings(haystack, needle)
                result shouldContainExactlyInAnyOrder expectedResult
            }
        }

        test("Property test") {
            checkAll<String, String>(
                1_000_000,
                Arb.string(1, 30, Codepoint.az()),
                Arb.string(1, 30, Codepoint.az()),
            ) { haystack, needle ->
                withClue("Testing `$needle` in `$haystack`") {
                    val result = twoWayClass.getAllSubstrings(haystack, needle)
                    result shouldContainExactlyInAnyOrder haystack.getAllSubstringsNative(needle)
                }
            }
        }
    }


    context("computeMaxSuffixAndPeriod") {
        context(" should correctly split string") {
            data class MaxSuffixTest(
                val pattern: String,
                val comparator: Comparator<Char> = naturalOrder(),
                val expectedLeft: String,
                val expectedRight: String,
                val expectedPeriod: Int
            ) {
                constructor(pattern: String, comparator: Comparator<Char> = naturalOrder(), rightPeriod: Int) : this(
                    pattern = pattern,
                    comparator = comparator,
                    expectedLeft = pattern.dropLast(pattern.getMaxSuffixNaive(comparator).length),
                    expectedRight = pattern.getMaxSuffixNaive(comparator),
                    expectedPeriod = rightPeriod
                )
            }

            withData(
                nameFn = { "${it.comparator.name()} '${it.pattern}' => ('${it.expectedLeft}', '${it.expectedRight}'); vPeriod: ${it.expectedPeriod}" },
                MaxSuffixTest("a", naturalOrder(), "", "a", 1),
                MaxSuffixTest("a", reverseOrder(), "", "a", 1),
                MaxSuffixTest("ab", naturalOrder(), "a", "b", 1),
                MaxSuffixTest("abaabaa", naturalOrder(), 3),
                MaxSuffixTest("abaabaa", reverseOrder(), "ab", "aabaa", 3),
                MaxSuffixTest("abcbcbacbcbacbc", naturalOrder(), "ab", "cbcbacbcbacbc", 5),
                MaxSuffixTest("abcbcbacbcbacbc", reverseOrder(), "", "abcbcbacbcbacbc", 15),
                MaxSuffixTest("viv", naturalOrder(), "", "viv", 2),
                MaxSuffixTest("viv", reverseOrder(), "v", "iv", 2),
            ) { (pattern, comparator, expectedLeft, expectedRight, expectedVPeriod) ->
                val result = twoWayClass.computeMaxSuffixAndPeriod(pattern, comparator)
                result.leftSubstring() shouldBe expectedLeft
                result.rightSubstring() shouldBe expectedRight
                result.rightPeriod shouldBe expectedVPeriod
            }
        }

        test("property test") {
            checkAll<String, Comparator<Char>>(200_000,
                Arb.string(1, 30, Codepoint.az()),
                listOf(naturalOrder<Char>(), reverseOrder<Char>()).exhaustive()
            ) { pattern, comparator ->
                val result = twoWayClass.computeMaxSuffixAndPeriod(pattern, comparator)
                val expectedSuffix = pattern.getMaxSuffixNaive(comparator)
                val expectedVPeriod = result.rightSubstring().toString().getMinimalPeriod()
                result.leftSubstring() shouldBe pattern.dropLast(expectedSuffix.length)
                result.rightSubstring() shouldBe expectedSuffix
                result.rightPeriod shouldBe expectedVPeriod
            }
        }
    }
})
