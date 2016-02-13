import java.io.File


fun main(args: Array<String>) {
    val line = File("input-houses").readLines().first().toLowerCase()
    val (locs, loc) = line.fold(Pair(setOf(Pair(0, 0)), Pair(0, 0)), ::updateSetWith)
    println("Locs: ${locs}, current: ${loc}, visited houses: ${locs.size}")

}

fun updateSetWith(arg: Pair<Set<Pair<Int, Int>>, Pair<Int, Int>>, c: Char): Pair<Set<Pair<Int, Int>>, Pair<Int, Int>> {
    val (locs, loc) = arg
    val updatedLoc = when (c) {
        '>' -> Pair(loc.first + 1, loc.second)
        '<' -> Pair(loc.first - 1, loc.second)
        'v' -> Pair(loc.first, loc.second - 1)
        '^' -> Pair(loc.first, loc.second + 1)
        else -> loc
    }
    return Pair(locs.plus(updatedLoc), updatedLoc)
}
