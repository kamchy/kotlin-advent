import java.io.File

fun main(args: Array<String>) {
    val fromFile = File("input-jsabacus").readLines().joinToString(separator = "")
    val myline = """[-123,"ala",100]"""
    val line = fromFile

    println(sumOfNums(line))
}

val numRegex = """-?\d*""".toRegex()
fun sumOfNums(line: String): Int {
    val stringNums = numRegex.findAll(line).map { mr -> mr.value }.filterNot { it.isNullOrBlank() }
    return stringNums.map { Integer.parseInt(it) }.sum()
}
