import java.io.File

fun main(args: Array<String>) {
//    sample()
    task()
}

private fun task() {
    println(OlympicsSolver(File("input-reindeer").readLines()).maxDistanceAfterSeconds(2503))
//     part 2
    println(OlympicsSolver(File("input-reindeer").readLines()).maxPointsAfterSeconds(2503))
}

private fun sample() {
    val sample = """
    Comet can fly 14 km/s for 10 seconds, but then must rest for 127 seconds.
    Dancer can fly 16 km/s for 11 seconds, but then must rest for 162 seconds.
    """
    val lines = sample.split("\n").filterNot { it.isBlank() }
    //part1
//    println(OlympicsSolver(lines).maxDistanceAfterSeconds(1000))
    //part2
    println(OlympicsSolver(lines).maxPointsAfterSeconds(1000))
}

data class ReinData(val name: String, val speed: Int, val flyTime: Int, val restTime: Int)

/* Solves problem of day 14 */
class OlympicsSolver(val lines: List<String>) {
    private val data: MutableList<ReinData> = mutableListOf()
    private val pointsMap: MutableMap<ReinData, Int> = mutableMapOf()
    private val regexp = Regex("""\s*(\w*) can fly (\d*) km/s for (\d*) .* for (\d*) seconds\.""")
    init{
        readReindeerData()

    }

    private fun readReindeerData() {
        lines.forEach {
            println("--$it--")
            val element = regexp.matchEntire(it)
            if (element != null) {
                val (name, speed, flytime, restTime) = element.destructured
                addToData(ReinData(name, speed.toInt(), flytime.toInt(), restTime.toInt()))
            }
        }
    }

    private fun addToData(reinData: ReinData) {
//        println("adding $reinData")
        data.add(reinData)
    }

    fun maxDistanceAfterSeconds(secs: Int): Int {
        return data.map { distanceAfter(secs, it) }.max() ?: 0

    }

    private fun  distanceAfter(secs: Int, rein: ReinData): Int {
        val cycle = rein.flyTime + rein.restTime
        val fullCycles: Int = secs / cycle
        val rest = secs % cycle
        val lastCycleDist = Math.min(rest, rein.flyTime) * rein.speed
        return fullCycles * rein.flyTime * rein.speed + lastCycleDist
    }

    fun  maxPointsAfterSeconds(secs: Int): Int {
        data.forEach { pointsMap.put(it, 0) }
        (1..secs+1).forEach {
            val passedSecond = it
            val fastestReindeersSorted = data.map { it to distanceAfter(passedSecond, it) }.sortedByDescending { p -> p.second }
            //println("Fastest: $fastestReindeersSorted")
            val longestDistance = fastestReindeersSorted.first().second
            fastestReindeersSorted.takeWhile { p -> p.second == longestDistance }.forEach { pair ->
                pointsMap.put(pair.first, pointsMap.get(pair.first)!! + 1)
//                println("After $passedSecond ${pair.first.name} increases points. Map: $pointsMap")
            }

            if (it == 140) {
//                println("Fastest after $it:all: $pointsMap")
            }
        }
        val maxp = pointsMap.values.max()!!
        return maxp
    }
}
