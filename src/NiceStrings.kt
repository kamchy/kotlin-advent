import java.io.File

fun main(args: Array<String>) {
    val tests = listOf(
            ("ugknbfddgicrmopn" to true),
            ("aaa" to true),
            ("jchzalrnumimnmhp" to false),
            ("haegwjzuvuyypxyu" to false),
            ("dvszwmarrgswjxmb" to true))
    for ((s, expected) in tests) {
        println("s ${s} is ${if (isNice(s)) "nice" else "naughty" }")
        assert(isNice(s) === expected)
    }
    println(calc())
}
fun calc() = File("input-strings").readLines().filter { isNice(it) } .count()

fun isNice(it: String): Boolean {
    val disallowed = setOf("ab", "cd", "pq", "xy")
    val vowels = "aeiou"

    return (it.findAnyOf(disallowed) == null) and
            (it.filter { it in vowels }.length >= 3) and
            (it.filterIndexed { i, c -> if (i > 0) it[i] == it[i-1] else false }.length > 0)
}
