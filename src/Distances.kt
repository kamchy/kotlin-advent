import java.io.File

fun main(args: Array<String>) {

    val reg = """(\w*)\s*to\s*(\w*)\s*=\s*(\d*)""".toRegex()
    val distances = File("input-distances").readLines().flatMap { mkDist(it, reg) }.filterNotNull()
    distances.forEach { println(it) }
    val mapFromToDistList = distances.groupBy { d -> d.from }
            .mapValues { e -> e.value.groupBy { d -> d.dest }
            .mapValues { e -> e.value.single().len }}
    val locations = mapFromToDistList.keys

    var globMin = Int.MAX_VALUE

    fun checkMinWhenStartingFrom(loc: String, toVisit: Set<String>, lenthTillLoc: Int) {
        log("Visited ${loc} after ${lenthTillLoc}, before: ${toVisit.joinToString{x -> x.toString()}}")
        if (toVisit.isEmpty()) {
            if (lenthTillLoc < globMin) {
                globMin = lenthTillLoc
            }
        } else {
            for (nextLoc in toVisit) {
                val lenTillNext = lenthTillLoc + mapFromToDistList[loc]!![nextLoc]!!
                checkMinWhenStartingFrom(nextLoc, toVisit.minus(nextLoc), lenTillNext)
            }
        }

    }

    for (loc in locations) {
        checkMinWhenStartingFrom(loc, locations.minus(loc), 0)
    }

    println("Min len is ${globMin}")

}

fun mkDist(lineWithDistance: String, reg: Regex): List<Dist> {
    val grs = reg.find(lineWithDistance)?.groups
    if (grs != null) {
        val from = grs[1]?.value ?: ""
        val to = grs[2]?.value ?: ""
        val v = grs[3]?.value?.toInt() ?: 0
        return listOf(Dist(from, to, v), Dist(to, from, v))
    } else {
        return listOf<Dist>()
    }
}

data class Dist (val from : String, val dest : String, val len : Int)
