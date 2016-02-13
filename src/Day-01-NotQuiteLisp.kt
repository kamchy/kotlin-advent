import java.io.File

fun main(args: Array<String>) {
    val stepsText = File("input-lisp.txt").readText()

    fun stepsValues(text: String) : List<Int> = text.map {
        when(it) {
            '(' -> 1
            ')'-> -1
            else -> throw Exception("unknown")
        }}

    val steps = stepsValues(stepsText)
    println("target floor: ${steps.sum()}")

    fun charPositionForFloor(floor: Int, stepsList: List<Int>) : Int {
        var prevFloor = 0
        for ((idx, value) in stepsList.mapIndexed { i, v -> (i to v) }) {
            val currFloor = prevFloor + value
            if (currFloor == floor) {
                return idx + 1
            }
            prevFloor = currFloor
        }
        return -1
    }
    print("Floor index is ${charPositionForFloor(-1, steps)}}")

}
