import java.io.File


val samples = listOf("toggle 753,664 through 970,926",
        "turn off 150,300 through 213,740",
        "turn on 141,242 through 932,871")

enum class Operation {ON, OFF, TOGGLE}
fun Operation.getFun(): (Boolean) -> Boolean = when (this) {
    Operation.OFF -> { x -> false}
    Operation.ON -> { x -> true }
    Operation.TOGGLE -> { x -> !x }
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
    val lights = Array<BooleanArray>(w, {i -> BooleanArray(h, { j -> false})})
    override fun toString(): String {
        return lights.joinToString(separator = "\n", transform =
          { it.joinToString(separator = " ", transform = {b -> if (b) "1" else "0"}) } )
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

    private fun oper(f: (Boolean) -> Boolean, x: Int, y: Int) {lights[y][x] = f(lights[y][x])}
    private fun nrOnInRow(arr: BooleanArray): Int = arr.fold(0, {acc, bval -> acc + if (bval) 1 else 0})

    fun print() {
        println("\n\n${this}\nOn: ${this.nrOn()}")
    }
}