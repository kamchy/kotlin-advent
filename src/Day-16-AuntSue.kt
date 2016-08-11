import java.io.File


val tape: Map<String, Int> = mapOf(
        "children" to 3, "cats" to 7, "samoyeds" to 2, "pomeranians" to 3, "akitas" to 0,
        "vizslas" to 0, "goldfish" to 5, "trees" to 3, "cars" to 2, "perfumes" to 1)

fun main(args: Array<String>) {
    val sueRe = Regex("""Sue (\d*): (\w*): (\d*), (\w*): (\d*), (\w*): (\d*)""")
    val conditionsSimple: (Map.Entry<String, Int>, Map<String, Int>) -> Boolean = { e, m ->
        m.containsKey(e.key) && m[e.key] == e.value
    }
    val conditionsComplex: (Map.Entry<String, Int>, Map<String, Int>) -> Boolean = { e, m ->
        m.containsKey(e.key) && when(e.key) {
            in setOf("cats", "trees") -> m[e.key]!! < e.value
            in setOf("pomeranians", "goldfish") -> m[e.key]!! > e.value
            else -> m[e.key] == e.value
        }
    }

    println(File("input-auntsue")
            .readLines()
            .map { val s = createSue(it, sueRe); println(s); s }
            .filter{ it != null && matchesTape(it, conditionsComplex) })
}

fun matchesTape(sue: Sue, condition: (Map.Entry<String, Int>, Map<String, Int>) -> Boolean): Boolean =
        sue.props.all { condition(it,tape) }

data class Sue(val num: Int, val props: Map<String, Int>)

fun  createSue(line: String, sueRe: Regex): Sue? {
    val matched = sueRe.matchEntire(line)
    val m: MutableMap<String, Int> = mutableMapOf()
    return if (matched != null) {
        val (sueNum, n1, v1, n2, v2, n3, v3) = matched.destructured
        m.put(n1, v1.toInt())
        m.put(n2, v2.toInt())
        m.put(n3, v3.toInt())
        Sue(sueNum.toInt(), m)
    } else null
}


