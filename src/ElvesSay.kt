/**
 *"+reated by karma on 06.02.16.
 * 1321131112
 * 1321131112
 * 492982
 *
 * todo: no use in generating whole strings - better iterate and use streams
 */
fun main(args: Array<String>) {
    val seed = "1321131112"
    var mutableSeed = seed
    val times = 40
    val ms = kotlin.system.measureTimeMillis {
        repeat(times, { mutableSeed = describe(mutableSeed); println(mutableSeed.length) })
    }
    println("Patter $seed repeated $times times: $ms")
}


fun describe(s: String): String {
    fun update(res: String, cnt: Int, ch: Char): String {
        return res + cnt.toString() + ch.toString()
    }
    var prev = '.'
    var currIdx = 0
    var countCurr = 0
    var res = ""
    while (currIdx < s.length) {
        if (prev != s[currIdx]) {
            if (currIdx != 0) {
                res = update(res, countCurr, prev)
            }
            countCurr = 1
        } else {
            countCurr += 1;
        }
        prev = s[currIdx]
        currIdx += 1
    }
    if (countCurr > 0) {
        res = update(res, countCurr, prev)
    }
    return res

}