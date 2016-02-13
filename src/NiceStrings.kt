import java.io.File

fun main(args: Array<String>) {
    //test(isNice, isNiceTestCases)
    //test (isNice2, isNice2TestCases)
    println(calc(isNice2))
}
fun calc(niceFun: (String) -> Boolean) = File("input-strings").readLines().filter { niceFun(it) } .count()
val isNice: (String) -> Boolean = {
    val disallowed = setOf("ab", "cd", "pq", "xy")
    val vowels = "aeiou"
    (it.findAnyOf(disallowed) == null) and
    (it.filter { it in vowels }.length >= 3) and
    (it.filterIndexed { i, c -> if (i > 0) it[i] == it[i-1] else false }.length > 0)
}

val isNice2: (String) -> Boolean = {
    it.contains("""(\w\w).*\1""".toRegex()) and it.contains("""(\w).\1""".toRegex())
}

val isNiceTestCases = listOf(
        ("ugknbfddgicrmopn" to true),
        ("aaa" to true),
        ("jchzalrnumimnmhp" to false),
        ("haegwjzuvuyypxyu" to false),
        ("dvszwmarrgswjxmb" to false))

val isNice2TestCases = listOf(
        ("kamaika" to true),
        ("aaa" to false),
        ("baoooobab" to true),
        ("qjhvhtzxzqqjkmpb" to true),
        ("xxyxx" to true),
        ("uurcxstgmygtbstg" to false),
        ("ieodomkazucvgmuy" to false)
)

fun test(niceFun: (String) -> Boolean, data: List<Pair<String, Boolean>>) {
    for ((s, expected) in data) {
        println("s ${s} is ${if (niceFun(s)) "nice" else "naughty" }")
        check(niceFun(s) === expected)
    }
}