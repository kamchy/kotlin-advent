import java.io.File


fun main(args: Array<String>) {
//    solveFor(volume = 25, listOfSizes = listOf(20, 15, 10, 5, 5))
    val sumToContainersCount = solveFor(volume = 150, listOfSizes = File("input-eggnog").readLines().map { it.toInt() })
    val filledContainers = sumToContainersCount.filter { it.first == 150 }
    println("Number of combinations: ${filledContainers.count()}")
    val minContainers: Int? = filledContainers.minBy { it.second }?.second
    if (minContainers != null) {
        val filledContainersSmallestCount = filledContainers.filter { it.second == minContainers }
        println("Number of combinations with $minContainers containers is ${filledContainersSmallestCount.size}")
    }

}

private fun solveFor(volume: Int, listOfSizes: List<Int>): List<Pair<Int, Int>> {
    val boxes = listOfSizes.sorted().toIntArray()
    val intWith20Ones = (1 shl boxes.size) - 1
    return (1..intWith20Ones + 1).map { num ->
        var bits = Integer.toBinaryString(num).padStart(boxes.size, '0')
        var sum = 0
        (0..boxes.size - 1).forEach { ch ->
            val idx = bits.length - ch - 1
            sum += if (bits[idx] == '1') boxes[idx] else 0
//            println("Number: $bits ($num), ch=$ch, checking bit $idx: (${bits[idx]}), sum=$sum")
        }
        (sum to Integer.bitCount(num))
    }

}
