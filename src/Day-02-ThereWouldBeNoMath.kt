import java.io.File

fun main(args: Array<String>) {
    val res = File("input-paper").readLines()
            .map { mkBox(it) }
            .map { (paper(it) to ribbon(it)) }
            .fold (Pair(0, 0), {acc, p -> (acc.first + p.first to acc.second + p.second) } )
    println("Result: paper ${res.first} ribbon ${res.second}")
}

fun paper(b: List<Int>): Int = b[0]*b[1] + (b[0]*b[1] + b[1]*b[2] + b[2]*b[0])*2
fun ribbon(b: List<Int>): Int = 2*(b[0]+b[1]) + b[0]*b[1]*b[2]
fun mkBox(line: String): List<Int> = line.split('x').map { it.toInt() }.sorted()
