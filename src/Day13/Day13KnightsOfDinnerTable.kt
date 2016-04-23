package Day13


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


fun main(s: Array<String>) {
    val origFile = "input-sitting-arrange"
    val testFile = "input-test"
    val res = Day13.SitInfo.javaClass.getResourceAsStream(origFile)
    val infos = res.bufferedReader().readLines().fold(listOf<SitInfo>(), { li, st -> li + SitInfo.from(st) })
    val people: Set<String> = infos.fold(setOf(), { s, i -> s.plus(i.from) })
    val nameToIndex = people.mapIndexedNotNull  { i, s -> (s to i) }.toMap()
    val indexToName = people.mapIndexedNotNull  { i, s -> (i to s) }.toMap()
    val g = Graph(people.size)

    fun edgeFrom(s: SitInfo): Edge {
        val idxFrom: Int? = nameToIndex[s.from]
        val idxTo: Int? = nameToIndex[s.to]
        if (idxFrom == null || idxTo == null) {
            throw IllegalArgumentException();
        }
        return Edge(Math.min(idxFrom, idxTo), Math.max(idxFrom, idxTo), s.value)
    }

    val edgeList = infos
            .map { edgeFrom(it) }
            .groupBy { e -> (e.v1 to e.v2) }
            .mapValues { me -> me.value.sumBy { it.weight } }
            .map { Edge (it.key.first, it.key.second, it.value) }

    edgeList.forEach { g.addEdge(it) }
    println(infos.joinToString (separator = "\n"))
    println(edgeList.map {
        ((indexToName[it.v1] to indexToName[it.v2]) to it.weight)
    }.joinToString( separator = "\n") )
    val (t, v) = MaxSpanTreeF(g)
    val f = t.first()
    val l = t.last()
    println ("tree: ${t.map { e -> ((indexToName[e.v1] to indexToName[e.v2]) to e.weight) }}, val: ${v}")
}





