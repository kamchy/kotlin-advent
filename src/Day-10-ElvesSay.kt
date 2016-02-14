import java.util.concurrent.BlockingQueue

/**
 * 1321131112 -> 40 -> 492982 (152936 ms ->  405 ms)
 *            -> 50 -> 6989950 (    :(     -> 5405 ms)
 *
 * todo: no use in generating whole strings - better iterate and use streams
 */
fun main(args: Array<String>) {
    val seed = "1321131112"
    var result = "";
    var times = 50;


    var milis = kotlin.system.measureTimeMillis {
        result = nonThreaded(times, seed)
    }
    notify(times, milis, result, "threaded")

}

fun notify(times: Int, milis: Long, result: String, name: String) {
    println ("$name x $times: $milis ms (${result.length})")
}

fun nonThreaded(repeatCount: Int, seed: String): String {
    var mutableSeed = seed
    repeat(repeatCount, { mutableSeed = describe(mutableSeed) })
    return mutableSeed
}

fun threaded(count: Int, seed: String): String {
    val queues = (0..count).map { java.util.concurrent.LinkedBlockingQueue<Char>() }
    seed.forEach { queues[0].put(it) }
    queues[0].put('x')
    val threads = (0..count - 1).map { QueueTask(queues[it], queues[it + 1], "Thread $it") }
    threads.forEach { it.start() }
    threads.forEach { it.join() }
    val result = queues[count].fold(StringBuffer(), { sb, c -> sb.append(c) }).dropLast(1).toString()
    return result
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

class QueueTask(val inqueue: BlockingQueue<Char>, val outqueue: BlockingQueue<Char>, name: String?) : Thread(name) {
    override fun run() {
        var c = inqueue.take()
        var ccounter = 1
        while (c != 'x') {
            var tmp = inqueue.take()
            if (tmp == c) {
                ccounter++
            } else {
                ccounter.toString().forEach {
                    outqueue.add(it)
                }
                outqueue.add(c)
                ccounter = 1
            }
            c = tmp
        }
        outqueue.offer(c)
    }
}
