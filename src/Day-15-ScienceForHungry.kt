import java.io.File

fun main(args: Array<String>) {
//    sample()
    taskSolveHunger()
}

fun taskSolveHunger() {
    println(HungrySolver(File("input-hungry").readLines()).solve(100))
}

private fun sample() {
    val lines: List<String> =
            """Butterscotch: capacity -1, durability -2, flavor 6, texture 3, calories 8
Cinnamon: capacity 2, durability 3, flavor -2, texture -1, calories 3""".split("\n")
    val hungrySolver = HungrySolver(lines)
    println(hungrySolver.solve(100))
}

data class Ingredient(val name: String, val capacity: Int, val durability: Int,
                      val flavour: Int, val texture: Int, val calories: Int)

class HungrySolver(private val lines: List<String>) {
    val ingRegex = Regex("""(\w*):\s*capacity\s*(-?\d*),\s*durability\s*(-?\d*),\s*flavor\s*(-?\d*),\s*texture\s*(-?\d*),\s*calories\s*(\d*)\s*""")
    val ing: List<Ingredient> = parse(lines)

    private fun  parse(lines: List<String>): List<Ingredient> {
        return lines.map {
            val matchEntire = ingRegex.matchEntire(it)
            if (matchEntire == null) {
                println("Could not match line: $it")
            }
            if (matchEntire != null) {
                val (n, c, d, f, t, ca) = matchEntire.destructured
                Ingredient(n, c.toInt(), d.toInt(), f.toInt(), t.toInt(), ca.toInt())
            } else null
        }.filterNotNull()
    }

    fun calcProperty(propGetter: (Ingredient) -> Int): (List<Int>) -> Int = { amounts ->
        val sum = amounts.indices.map {
            amounts[it] * propGetter(ing[it])
        }.sum()
        if (sum < 0) 0 else sum
    }


    private fun calculateValueForAmounts(amounts: List<Int>): Int =
      listOf<(Ingredient) -> Int>(
              { i -> i.capacity },
              { i -> i.durability },
              { i -> i.flavour },
              { i -> i.texture }).map {
          calcProperty(it)(amounts)
      }.reduce { v1, v2 ->  v1 * v2}

    fun solve(spoonCount: Int): Int {
        val values = generateAmounts(spoonCount, ing.size).filter { caloriesSumTo(it, 500) }.map {
            val value = calculateValueForAmounts(it)
            //println("Value $value, elements: \n$it")
            value
        }
        return values.max() ?: 0
    }

    private fun caloriesSumTo(amounts: List<Int>, maxCalories: Int): Boolean = calcProperty({ i -> i.calories})(amounts) == maxCalories


    fun generateAmounts(spoonCount: Int, ingredientsCount: Int): Sequence<List<Int>> {
        return generateSums(spoonCount, ingredientsCount)
    }



}
fun generateSums(sum: Int, buckets: Int): Sequence<List<Int>> {
    if (buckets == 1) {
        return Sequence { listOf(listOf(sum)).iterator() }
    } else {
        return (0..sum+1).map { last ->
            if (sum - last > 0 && buckets - 1 > 0) {
                generateSums(sum - last, buckets - 1).map { rest ->
                    rest.plus(last)
                }
            } else null
        }.filterNotNull().asSequence().flatten()
    }
}

