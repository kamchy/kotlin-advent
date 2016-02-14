import java.io.File

sealed class Finder(var currentExtremum: Int) {
    class Min : Finder(Int.MAX_VALUE) {
        override fun localExtremumFound(it: Int): Boolean = it < currentExtremum
    }

    class Max : Finder(Int.MIN_VALUE) {
        override fun localExtremumFound(it: Int): Boolean = it > currentExtremum
    }

    abstract fun localExtremumFound(it: Int): Boolean
    var updateExtremumWith: (Int) -> Unit = { currentExtremum = it }
}




fun main(args: Array<String>) {

    val reg = """(\w*)\s*to\s*(\w*)\s*=\s*(\d*)""".toRegex()
    val distances = File("input-distances").readLines().flatMap { mkDist(it, reg) }.filterNotNull()
    distances.forEach { println(it) }
    val mapFromToDistList = distances.groupBy { d -> d.from }
            .mapValues { e -> e.value.groupBy { d -> d.dest }
            .mapValues { e -> e.value.single().len }}
    val locations = mapFromToDistList.keys


    fun checkExtremumWhenStartingFrom(loc: String, toVisit: Set<String>, lenthTillLoc: Int, finder: Finder) {
        log("Visited ${loc} after ${lenthTillLoc}, before: ${toVisit.joinToString { x -> x.toString() }}")
        if (toVisit.isEmpty()) {
            if (finder.localExtremumFound(lenthTillLoc)) {
                finder.updateExtremumWith(lenthTillLoc)
            }
        } else {
            for (nextLoc in toVisit) {
                val lenTillNext = lenthTillLoc + mapFromToDistList[loc]!![nextLoc]!!
                checkExtremumWhenStartingFrom(nextLoc, toVisit.minus(nextLoc), lenTillNext, finder)
            }
        }
    }


    var minFinder = Finder.Min()
    var maxFinder = Finder.Max()
    for (loc in locations) {
        checkExtremumWhenStartingFrom(loc, locations.minus(loc), 0, minFinder)
    }
    for (loc in locations) {
        checkExtremumWhenStartingFrom(loc, locations.minus(loc), 0, maxFinder)
    }

    println("Min len is ${minFinder.currentExtremum}, max len is ${maxFinder.currentExtremum}")

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
