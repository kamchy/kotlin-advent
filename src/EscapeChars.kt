import java.io.File
import java.io.StringReader

/**
 * Created by karma on 06.02.16.
 */
fun main(args: Array<String>) {
    val samples = """
        ""
        "abc"
        "aaa\"aaa"
        "\x27"
        """.split("\n").filter { ! it.isEmpty() }.map { it.trim() }

    val reals = File("input-escapes").readLines()

    val res = reals.fold(0, { acc, s -> acc + calcCounts(s).diff()})
    println(res)
}

fun Pair<Int, Int>.diff() = this.first - this.second

fun reprCount(s: String): Int {
    return s.length
}

fun inMemCount(s: String): Int {
    var count: Int = 0
    val r = StringReader(s)
    var c = r.read()
    var prev = -1
    fun readNext(): Int  { prev = c; c = r.read(); return c}
    while (c != -1) {
        if (count != 0 || c.toChar() != '"') {
            count += 1
        }
        if (c.toChar() == '\\') {
            readNext()
            if (c != -1) {
                val ch = c.toChar()
                if (ch == '"' || ch == '\\') {
                    readNext()
                } else if (ch == 'x') {
                    val a = readNext()
                    val b = readNext()
                    if (a == -1 || b == -1) {
                        throw Exception("should be escape x at ${count} in ${s}")
                    }
                    readNext()
                } else {
                    throw Exception("should be \" or \\ or x")
                }
            } else {
                throw Exception("\\ should be followed by escape char")
            }
        } else {
            readNext()
        }
    }
    if (prev.toChar() == '"' && count > 0) {
        count = count.dec()
    }
    return count
}

fun calcCounts(s: String) : Pair<Int, Int> = (reprCount(s) to inMemCount(s))
