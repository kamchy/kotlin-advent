import java.io.File

fun main(args: Array<String>) {
//    test()
    println(Solver().solve(File("input-knights").readLines()))
}

private fun test() {
    val elements = listOf(1, 2, 3, 4)
    println("List size: ${elements.size}, expected circle combinations size is " +
            "(n-1)! = (${elements.size} - 1) ! = (${elements.size - 1})! = ${silnia(elements.size - 1)}")
    permuteCircle(elements).forEachIndexed { i, list ->
        println("Index: $i, list: ${list.joinToString(" ")}")
    }
}

fun  silnia(i: Int): Int {
    if (i < 2) {
        return i
    } else {
        return i * silnia (i - 1)
    }
}


fun <T> permuteCircle(elements: List<T>): Sequence<List<T>> {
    if (elements.size <= 2) {
        return listOf(elements).asSequence()
    } else {
        return permuteCircle(elements.minus(elements[0])).flatMap { it.spread(elements[0]) }
    }
}

fun <E> List<E>.spread(elem: E): Sequence<List<E>> {
    return this.mapIndexedTo(destination = mutableListOf(), transform = { i, e ->
        this.subList(0, i).plus(elem).plus(this.subList(i, this.size))
    }).asSequence()
}



class Key(private val l: String, private val r : String) {
    val left = if (l < r) l else r
    val right = if (l < r) r else l
    override fun toString() = "{ $left->$right }"
    override fun hashCode(): Int = left.hashCode() * 31 + right.hashCode()
    override fun equals(other: Any?): Boolean =
            other != null &&
            other is Key &&
            other.left == left &&
            other.right == right
}

class Solver {
    val happyMap: MutableMap<Key, Int> = mutableMapOf()
    val reg = Regex("""(\w*) .* (gain|lose) (\d*) .* to (\w*)\.""")

    fun names() : List<String> = happyMap.keys.flatMap { k -> listOf(k.left, k.right) }.distinct()
    fun totalHappynessForCircle(uniqueNames: List<String>): Int {
        return (0..uniqueNames.size - 1)
                .map { idx -> Key(uniqueNames[idx], uniqueNames[(idx + 1) % uniqueNames.size])}
                .fold(0, { acc, k -> acc + happyMap.getOrElse(k, { 0 }) })
    }
    fun solve(lines: List<String>): Int {
        lines.forEach { updateMap(it) }
        generateMyselfLines(names()).forEach {updateMap(it)} // this line covers part 2
        return permuteCircle(names()).map { totalHappynessForCircle(it) }.max() ?: 0

    }

    private fun generateMyselfLines(names: List<String>): List<String> =
        names.map { "KamilaChyla would gain 0 in happiness when sitting next to $it." }


    private fun  updateMap(line: String) {
//        println("Update map for $line")
        val res = reg.matchEntire(line)
        if (res != null) {
            val (from, action, value, to) = res.destructured
            val key = Key(from, to)
//            println("Update for $from->$to ($value for $action)")
            val happVal = if (action == "gain") value.toInt() else value.toInt() * (-1)
            happyMap.put(key, happyMap.getOrElse(key, {0}) + happVal)
//            println("Map now:\n $happyMap")
        }
    }
}