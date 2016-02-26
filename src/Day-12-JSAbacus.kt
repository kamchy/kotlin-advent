import java.io.File
import java.util.*

fun main(args: Array<String>) {
    val fromFile = File("input-jsabacus").readLines().joinToString(separator = "")
    val line = fromFile
    val jsonp = JsonP().parse(line)
    println(jsonp.sumNums())
    println(jsonp.sumNumsWithout("red")) // 32431 is too low, 85327 is too low
}

/* My inital solution (didn't know about "red" issue)*/
val numRegex = """-?\d*""".toRegex()
fun sumOfNums(line: String): Int {
    val stringNums = numRegex.findAll(line).map { mr -> mr.value }.filterNot { it.isNullOrBlank() }
    return stringNums.map { Integer.parseInt(it) }.sum()
}

/**
 * Extension function calculating sum of numbers but only for JsonObj which don't have
 * any property equal to given col (color name)
 * */
fun JsonObj.sumNumsWithout(col: String): Int {
    log ("snw: entering ${this}")
    val result = when (this) {
        is JsonObj.JsonDict -> {
            val redOpt = this.m.values.find { it is JsonObj.JsonValue.JsonIdent && it.s == col }
            if (redOpt != null) {
                log("snw: dict $this contains $col, returning 0")
                return 0
            }
            else {
                log("snw: dict $this does NOT contain red, folding values..}")
                return this.m.entries.fold(0, { acc, e -> acc + e.value.sumNumsWithout(col) })
            }
        }

        is JsonObj.JsonList -> {
            log("snw: list $this, so folding values...")
            this.list.fold (0 , {acc, e -> acc + e.sumNumsWithout(col)})
        }
        else -> this.sumNums()
    }
    log("snw: exiting $this ($result)")
    return result
}

/** Extension function for calculating sum of numbers */
fun JsonObj.sumNums(): Int =
    when (this) {
        is JsonObj.JsonValue.JsonNum -> this.value()
        is JsonObj.JsonValue.JsonIdent -> 0
        is JsonObj.JsonList -> this.list.fold(0, {acc, obj -> acc + obj.sumNums()})
        is JsonObj.JsonDict -> this.m.entries.fold(0, {acc, e -> acc + e.value.sumNums()})
        else -> 0
    }

fun sumOfNums(o: JsonObj) : Int = o.sumNums()
fun sumOfNumsColorFun(col: String) : (JsonObj) -> Int = { o -> o.sumNumsWithout(col)}


/**
 * JSON model classes
 * */
sealed class JsonObj {
    sealed class JsonValue(val s: String) : JsonObj() {
        class JsonNum(val n: String) : JsonValue(n) {
            override fun toString(): String = n
            fun value() : Int = n.toInt()
        }

        class JsonIdent(val i: String) :JsonValue(i) {
            override fun toString(): String = "\"$i\""
        }
    }
    class JsonDict (val m: Map<JsonObj, JsonObj>): JsonObj() {
        override fun toString(): String =
            m.asSequence().joinToString(
                prefix = "{",
                postfix = "}",
                    separator = ",",
                transform = { p -> "${p.key}:${p.value}" } )
    }

    class JsonList(val list: List<JsonObj>) :  JsonObj() {
        override fun toString(): String =
            list.joinToString(separator = ",", prefix = "[",
                    postfix = "]", transform = {e -> e.toString()})

    }

    abstract override fun toString(): String
}

/* Simple json parser. Accepts no spaces, no floats...*/
class JsonP {

    fun parse(s: String): JsonObj {
        log("parse: $s")
        val st = StringTokenizer(s, "{}[],\":", true)
        return parseMain(st.nextToken(), st)
    }

    private fun parseMain(nt: String, st: StringTokenizer): JsonObj {
        log("parseMain : next token: $nt")
        when (nt) {
            "\"" -> return parseString(st)
            "{" -> return parseObj(st)
            "[" -> return parseList(st)
            else -> return parseNumber(nt, st)
        }

    }

    private fun parseNumber(s: String, st: StringTokenizer): JsonObj {
        return JsonObj.JsonValue.JsonNum(s)
    }

    /** Parses list; [ is eaten. */
    private fun parseList(st: StringTokenizer): JsonObj.JsonList {
        fun parseListInternal(l: List<JsonObj>): List<JsonObj> {
            val tok = st.nextToken()
            when (tok) {
                "]" -> return l
                else -> {
                    val elem = parseMain(tok, st)
                    log("parse list internal: elem: ${elem}")
                    val sepOrEnd = st.nextToken()
                    log("parse: ${sepOrEnd}")
                    when(sepOrEnd) {
                        "," ->  return parseListInternal(l.plus(elem))
                        "]" -> return l.plus(elem)
                        else -> throw Exception("expected , or ]")
                    }
                }
            }
        }
        val o = JsonObj.JsonList(parseListInternal(ArrayList<JsonObj>()))
        log("parseList returning ${o}")
        return o

    }

    /** Keyval is expected here, " is eaten */
    private fun parseKeyVal(st: StringTokenizer): Pair<JsonObj, JsonObj> {
        val key = parseString(st)
        val semi = st.nextToken()
        if (semi != ":") {
            throw Exception(": expected.")
        }
        val value = parseMain(st.nextToken(), st)
        return Pair(key, value)
    }

    /**
     * Opening { is already eaten, eats last }*/
    private fun parseObj(st: StringTokenizer): JsonObj.JsonDict {
        fun parseObjInternal(m: Map<JsonObj, JsonObj>): Map<JsonObj, JsonObj> {
            val objToken = st.nextToken()
            when(objToken) {
                "\"" -> {
                    val pair = parseKeyVal(st)
                    val afterPair = st.nextToken()
                    return when(afterPair) {
                        "," -> return parseObjInternal(m.plus(pair))
                        "}" -> return m.plus(pair)
                        else -> throw Exception(", expected.")
                    }
                }
                "}" -> return m
                else -> throw Exception("expected : or }")
            }
        }

        return JsonObj.JsonDict(parseObjInternal(HashMap<JsonObj, JsonObj>()))

    }

    /*Parses string; opening " is eaten, this should eat closing ". */
    private fun parseString(st: StringTokenizer): JsonObj.JsonValue {
        val s = st.nextToken()
        log("parseString:  str=$s")
        val closing = st.nextToken()
        log("parseString:  closing=$closing")

        if (closing != "\"") {
            throw Exception("\" expected")
        }
        val ps = JsonObj.JsonValue.JsonIdent(s)
        log("parseString: result=$ps")
        return ps
    }
}


