import java.io.File

fun main(args: Array<String>) {

//    solveExample()
//    solve(Data(100, 100, 100, mkLighs(File("input-lights10k").readLines()))) //part 1: 861
    solve(Data(100, 100, 100, mkLighsCornersOn(File("input-lights10k").readLines()))) //part 2: 924
}

private fun solveExample() {
    val sample = """##.#.#
...##.
#....#
..#...
#.#..#
####.#"""
    val sampleIterations = 5
    val boardWidth = 6
    val boardHeight = 6
    val lines = sample.split("\n")

//    val board = Day18.LightBoard(100, 100, mkLighs(File("input-lights10k").readLines())) -- 350
    solve(Data(boardHeight, boardWidth, sampleIterations, mkLighsCornersOn(lines)))
}

data class Data(val boardHeight: Int, val boardWidth: Int, val sampleIterations: Int,
                val initialLightsGenerator: (Int, Int) -> Array<IntArray>)

private fun solve(data: Data) {
    val board = Day18.LightBoard(data.boardWidth, data.boardHeight, data.initialLightsGenerator)
    (1..data.sampleIterations).map { idx ->
        board.next()
        board.print(idx)

    }
}

fun  mkLighs(split: List<String>): (Int, Int) -> Array<IntArray> {
    return {w, h -> Array<IntArray>(split.size,
              {i -> IntArray(split[i].length,
                 { j -> if (split[i][j] == '#') 1 else 0})})}
}

fun  mkLighsCornersOn(split: List<String>): (Int, Int) -> Array<IntArray> {
    fun isCorner(r: Int, c: Int, w: Int, h: Int) =
            (r to c) in setOf((0 to 0), (0 to w - 1), (h - 1 to 0), (h - 1 to w - 1))
    val arr = {w: Int, h: Int -> Array<IntArray>(split.size,
            {i -> IntArray(split[i].length,
                    { j -> if (split[i][j] == '#' || isCorner(i, j, w, h)) 1 else 0})})}
    return arr
}

class Day18 {

    data class Coord(val col: Int, val row: Int)

    class LightBoard(val w :Int, val h:Int, lightsFun: (Int, Int) -> Array<IntArray>) {
        var lights = lightsFun(w, h)
        private var tmp = mkLights(w, h)

        private fun mkLights(cols: Int, rows: Int) =
            Array<IntArray>(cols, {i -> IntArray(rows, { j -> 0})})

        override fun toString(): String {
            return lights.joinToString(separator = "\n", transform =
            { it.joinToString(separator = "", transform = {b -> if (b == 1) "#" else "."}) } )
        }

        fun nrOn() = lights.fold(0, {acc, arr -> acc + nrOnInRow(arr)})

        companion object NeighboursMap {
            private val colRowToNeighMap: MutableMap<Coord, List<Coord>> = mutableMapOf()
            private fun calcNeigh(coord: Coord, cols: Int, rows: Int): List<Coord> =
                (-1..1).flatMap { coldelta ->
                    (-1..1).map { rowdelta ->
                        Coord(col = coord.col + coldelta, row = coord.row + rowdelta)
                    } }.filter { p ->
                            p.col >= 0 &&
                            p.row >= 0 &&
                            p.col < cols &&
                            p.row < rows
                 }.minus(coord)

            fun neigFor(coord: Coord, cols: Int, rows: Int) =
                    colRowToNeighMap.getOrPut(coord, {calcNeigh(coord, cols, rows)})
        }

        fun next() {
            tmp = mkLights(w, h)

            for (c in (0..w-1)) {
                for (r in (0..h-1)) {
//                    println("next: c=$c, r=$r")
                    tmp[r][c] = calcNextLight(Coord(c, r))
                }
            }
            lights = tmp
        }

        private fun get(c: Coord): Int  {
//            println("Get: $c")
            return lights[c.row][c.col]
        }


        private fun  calcNextLight(coord: Coord): Int {
            val curr = get(coord)
            val calcNeigh = neigFor(coord, w, h)
            val neigOnCount = calcNeigh.fold(0, { acc, p -> acc + get(p)})
            return if ((curr == 1  && (neigOnCount == 2 || neigOnCount == 3))
                    || (curr == 0 && (neigOnCount == 3))
            || (isCorner(coord))) 1 else 0
        }

        private fun isCorner(coord: Coord): Boolean =
                coord in setOf(Coord(0, 0), Coord(0,  h - 1), Coord(w - 1, 0), Coord(w - 1, h - 1))

        private fun nrOnInRow(arr: IntArray): Int = arr.fold(0, {acc, bval -> acc + bval})

        fun print(idx: Int) {
            println("On in ieration $idx: ${this.nrOn()}")

        }
    }
}
