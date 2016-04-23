package Day13
/* http://www.algorytm.org/algorytmy-grafowe/algorytm-prima/prim-j.html */
import java.util.*

data class Edge(val v1 : Int, val v2: Int, val weight : Int) : Comparable<Edge> {
    override fun compareTo(other: Edge): Int {
        return weight - other.weight
    }

    fun other(v: Int): Int {
        if (v == v1) {
            return v2
        } else if (v == v2) {
            return v1
        } else {
            throw IllegalArgumentException("Edge $this does not contain $v")
        }
    }
}

class Graph (val n: Int) {
    val adjArray : Array<MutableList<Edge>> = Array(n, {i -> mutableListOf<Edge>()})
    var edgeCount = 0

    fun addEdge(e : Edge) {
        adjArray[e.v1].add(e)
        adjArray[e.v2].add(e)
        edgeCount++
    }

    fun adjList(v: Int) = adjArray[v]
    fun value(v1: Int, v2: Int) = adjArray[v1].firstOrNull { it.v1 == v2 || it.v2 == v2 }?.weight ?: 0
}

fun MaxSpanTreeF (g: Graph) =  SpanTree(g, Comparator {e1, e2 -> e2.weight - e1.weight})
fun MinSpanTreeF(g: Graph) = SpanTree(g, Comparator {e1, e2 -> e1.weight - e2.weight})
fun SpanTree(g: Graph, c: Comparator<Edge>) : Pair<MutableList<Edge>, Long> {
    val marked: Array<Boolean> = Array(g.n, {false})
    val minSpanTree : MutableList<Edge> = mutableListOf()
    val edgePrioQ : PriorityQueue<Edge> = PriorityQueue(c)
    var totalWeight : Long = 0

    fun visit(v: Int) {
        marked[v] = true
        edgePrioQ.addAll(g.adjList(v).filter { !marked[it.other(v)] })
    }

    visit(0)
    while (edgePrioQ.isNotEmpty()) {
        var minimal = edgePrioQ.poll()
        if (minimal != null) {
            if (marked[minimal.v1] and marked[minimal.v2]) {
                continue
            }
            minSpanTree.add(minimal)
            totalWeight += minimal.weight
            listOf(minimal.v1, minimal.v2).filter { !marked[it] }.forEach { visit(it) }
        }
    }

    return minSpanTree to (totalWeight + g.value(minSpanTree.first().v1, minSpanTree.last().v2))
}

fun main(args: Array<String>) {
    println(Edge(1, 0, 34))

    val g = Graph(3)

    g.addEdge(Edge(0, 1, 10))
    g.addEdge(Edge(1, 2, 6))
    g.addEdge(Edge(0, 2, 15))
    g.addEdge(Edge(0, 2, 2))

    fun printResult(desc: String, r: Pair<MutableList<Edge>, Long> ){
        val (mint, mins) = r
        println("${desc} value: $mins, edges: ${mint}")
    }

    printResult("min", MinSpanTreeF(g))
    printResult("max", MaxSpanTreeF(g))
}


