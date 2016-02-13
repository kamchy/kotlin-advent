import java.io.File


fun main(args: Array<String>) {
    val line = File("input-houses").readLines().first().toLowerCase()
    //val line =  "^v^v^v^v^v"
    val santaLine = line.filterIndexed { i, c -> i % 2 == 0 }
    val roboLine = line.filterIndexed { i, c -> i % 2 != 0}
    val (slocs, sloc) = santaLine.fold(Pair(setOf(Pair(0, 0)), Pair(0, 0)), ::updateSetWith)
    val (rlocs, rloc) = roboLine.fold(Pair(setOf(Pair(0, 0)), Pair(0, 0)), ::updateSetWith)

    val locs = slocs.union(rlocs)
    println("Size = ${locs.size}")

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
