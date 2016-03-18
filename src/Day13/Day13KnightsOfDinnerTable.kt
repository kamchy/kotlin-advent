package Day13

import java.util.*


class SitInfo(val from: String, val to: String, val value: Int) {
    companion object Static {
        val sitPattern = """(\w*) would (\w*) (\d*) happiness units by sitting next to (\w*)\.""".toRegex()

        val fromString: (s: String) -> SitInfo = {
            val groups = sitPattern.findAll(it).first().groupValues
            val sign = if (groups[2] == "gain") 1 else -1
            SitInfo(groups[1], groups[4], groups[3].toInt() * sign)
        }

        fun from(s: String): SitInfo = fromString(s)
    }

    override fun toString(): String {
        return "$from->$value->$to"
    }
}

class TableGraph(val infos: List<SitInfo>) {
    val namesList = infos.flatMapTo(mutableSetOf(), { it -> listOf(it.from, it.to) }).toList()
    val priorityQueue = PriorityQueue<Node>()
    val nodes = mkMap(infos)

    private fun mkNode(s: String): Node = Node(s, namesList.mapTo(mutableMapOf(), (it to 0)))

private fun mkMap(infos: List<SitInfo>): MutableMap<String, Node> {
        val mm = mutableMapOf<String, Node>()
        infos.forEach {
            val n1 = mm.getOrPut(it.from, ::mkNode(it.from)})
            val n2 = mm.getOrPut(it.to, ::mkNode(it.to))
            n1.value[it.to] = it.value

        }

        return mm
    }

    val setNodesNotInTree = nodes.values.toMutableSet()
    val finalNodes = mutableListOf<Node>()

    fun calculate(): List<Node> {
        val node = nodes[namesList.first()]!!
        addToFinalNodesAndUpdatePrioQ(node)

        while (finalNodes.size < namesList.size) {
            val smallest = priorityQueue.remove()
            if (smallest != null) {
                addToFinalNodesAndUpdatePrioQ(smallest)
            }
        }

        return listOf();
    }

    /**
     * Post: node is in final nodes list and prioq contains
     * all nodes that are achievable from final nodes list
     * */
    fun addToFinalNodesAndUpdatePrioQ(node: Node) {
        setNodesNotInTree.remove(node)
        finalNodes.add(node)
        val finalSet = finalNodes.toSet()
        val candidates = setNodesNotInTree.filter { (it.connectedNodes() - finalSet).isNotEmpty() }
        priorityQueue.addAll(candidates)
    }

}

fun main(s: Array<String>) {
    val res = Day13KnightsOfDinnerTable.javaClass.getResourceAsStream("input-sitting-arrange")
    val infos = res.bufferedReader().readLines()
            .fold(listOf<SitInfo>(), { li, st -> li + SitInfo.from(st) })

    val gr = TableGraph(infos)
    gr.calculate().forEach { print(it) }


}


class Node(val name: String, val values: MutableMap<String, Int>) {
    val connected = mutableSetOf<Node>();
    fun connectedNodes(): Set<Node> = connected;
    fun addConnected(n: Node) = connected.add(n)
}

object Day13KnightsOfDinnerTable {
    fun calculate(infos: List<SitInfo>): String {
        return "no response yet"
    }
}


