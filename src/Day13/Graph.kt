package Day13

import java.util.*

class Edge<T: Comparable<T>, V>(val value: T, val from: V, val to: V) : Comparable<Edge<T, V>> {
    override fun compareTo(other: Edge<T, V>): Int = value.compareTo(other.value)
}

abstract class Graph<T:Comparable<T>, V, E: Edge<T, V>> {
    private val edges = mutableMapOf<V, Queue<E>>()

    fun addVertex(v: V) = edges.getOrPut(v, mkEdgeQueue())
    fun addEdge(e: E) = {
        edges.getOrPut(e.from, mkEdgeQueue()).offer(e)
    }
    fun getSmallestFrom(v: V): E? = edges[v]?.first()
    abstract fun mkEdgeQueue(): () -> Queue<E>
}


