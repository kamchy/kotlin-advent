import org.testng.Assert
import org.testng.annotations.Test

/**
 * Created by karma on 14.02.16.
 */
class JU {
    val valueToEncoded = listOf(
            (""" "aaa\"aaa" """ to """ "\"aaa\\\"aaa\"" """),
            (""" "\x27" """ to """ "\"\\x27\"" """),
            (""" "" """ to """ "\"\"" """),
            (""" "abc" """ to """ "\"abc\"" """))

    @Test
    fun test() {
        for ((idx, case) in valueToEncoded.mapIndexed { i: Int, pair: Pair<String, String> -> (i to pair) }) {
            Assert.assertEquals(encoded(case.first.trim()), case.second.trim())
        }
    }
}
