import java.io.File


val samples = listOf("toggle 753,664 through 970,926",
        "turn off 150,300 through 213,740",
        "turn on 141,242 through 932,871")

enum class Operation {ON, OFF, TOGGLE}
object Strategy {
    val on: (Int)->Int = { x -> x + 1}
    val off: (Int)->Int = { x -> if (x > 0) x - 1 else 0}
    val toggle: (Int)->Int = { x -> x + 2}
}

fun Operation.getFun(): (Int) -> Int = when (this) {
    Operation.OFF -> Strategy.off
    Operation.ON -> Strategy.on
    Operation.TOGGLE -> Strategy.toggle
}
data class Square(val from: Pair<Int, Int>, val to: Pair<Int, Int>, val op: Operation)

fun main(args: Array<String>) {
    val lb = LightBoard(1000, 1000)
    File("input-lights").readLines().forEach { lb.doCommand(parseLine(it)) }
    print("On is: ${lb.nrOn()}")
}
val r = """([^\d]*)\s(\d*),(\d*)\sthrough\s(\d*),(\d*)""".toRegex()
fun parseLine(l: String): Square {
    val groups = r.find(l)?.groups?.toList()?.map { it?.value }
    if (groups != null) {
        val from = mkints(groups[2] to groups[3])
        val to = mkints(groups[4] to groups[5])
        val op = when (groups[1]) {
            "toggle" -> Operation.TOGGLE
            "turn on" -> Operation.ON
            "turn off" -> Operation.OFF
            else -> {
                throw Exception("Command ${groups[1]} is invalid")
            }
        }

        return Square(from, to, op)
    } else {
        throw  Exception("invalid input")
    }
}

fun mkints(pair: Pair<String?, String?>): Pair<Int, Int> {
    val f = pair.first
    val s = pair.second
    if (f != null && s!= null) {
        return (f.toInt() to s.toInt())
    } else {
        throw Exception("Unparsable pair: ${pair}" )
    }
}

class LightBoard(val w :Int, val h:Int) {
    val lights = Array<IntArray>(w, {i -> IntArray(h, { j -> 0})})
    override fun toString(): String {
        return lights.joinToString(separator = "\n", transform =
          { it.joinToString(separator = " ", transform = {b -> b.toString()}) } )
    }
    fun nrOn() = lights.fold(0, {acc, arr -> acc + nrOnInRow(arr)})
    fun doCommand(s: Square) {
        val xmin = Math.min(s.from.first, s.to.first)
        val xmax = Math.max(s.from.first, s.to.first)
        val ymin = Math.min(s.from.second, s.to.second)
        val ymax = Math.max(s.from.second, s.to.second)
        for (x in (xmin..xmax)) {
            for (y in (ymin..ymax)) {
                oper(s.op.getFun(), x, y)
            }
        }
    }

    private fun oper(f: (Int) -> Int, x: Int, y: Int) {lights[y][x] = f(lights[y][x])}
    private fun nrOnInRow(arr: IntArray): Int = arr.fold(0, {acc, bval -> acc + bval})

    fun print() {
        println("\n\n${this}\nOn: ${this.nrOn()}")
    }
}