import java.util.*

fun main(args: Array<String>) {

    val lex = Lexer()
    val wires: Map<String, Wire> = hashMapOf()
    //val lines = File("input-wires").readLines()
    val lines = """
    NOT 65 -> c
    b OR c -> a
    4 -> b
    """.split("\n").filter { it.isNotBlank() }
    lines.forEach {
        val wire = makeWire(lex.tokenize(it))
        wires.plus((wire.name to wire))
    }
    println(wires["a"]?.eval(wires))
}




enum class Gate {
    AND {
        override fun evalValues(wiresValues: List<Int?>): Int {
            return double(wiresValues, { a, b -> a.and(b) })
        }

    },

    OR {
        override fun evalValues(wiresValues: List<Int?>): Int {
            return double(wiresValues, { a, b -> a.or(b) })
        }
    },
    NOT {
        override fun evalValues(wiresValues: List<Int?>): Int {
            return single(wiresValues, { it.inv() })
        }

    },
    RSHIFT {
        override fun evalValues(wiresValues: List<Int?>): Int {
            return double(wiresValues, {a, b -> a.shr(b)})
        }
    },

    LSHIFT {
        override fun evalValues(wiresValues: List<Int?>): Int {
            return double(wiresValues, {a, b -> a.shl(b)})
        }

    },
    NOP {
        override fun evalValues(wiresValues: List<Int?>): Int {
            return single(wiresValues, {it})
        }

    };

    abstract fun evalValues(wiresValues: List<Int?>): Int

    fun single(wiresValues: List<Int?>, f: (Int) -> Int): Int {
        val i = wiresValues[0]
        if (i != null) {
            return f(i)
        } else throw Exception("failed unary op")
    }

    fun double(wiresValues: List<Int?>, f: (Int, Int)->Int): Int{
        val first = wiresValues[0]
        val second = wiresValues[1]
        if ((first != null) && (second != null)) {
            return f(first, second)
        } else {
            throw Exception("failed binary op")
        }
    }

}

class Wire (val name: String, val g: Gate, val wires: List<Token.Ident>) {
    fun eval(context: Map<String, Wire>) = eval(g, wires.map { context.get(it.s) }, context)
    fun eval(g: Gate, wires: List<Wire?>, context: Map<String, Wire>): Int {
        val wiresValues = wires.map { it?.eval(context) }
        return g.evalValues(wiresValues)
    }

}

sealed class Token(val v: Any) {
    class Oper(val g: Gate) : Token(g)
    class Ident(val s: String) : Token(s)
    class Value(val i: Int) : Token(i)

    override fun toString() = v.toString()
}

fun makeWire(lt : List<Token>) : Wire  {
    val res = when (lt[0]) {
        is Token.Value -> Wire(lt[1].toString(), Gate.NOP, listOf())
        is Token.Oper -> Wire(lt[2].toString(), (lt[0] as Token.Oper).g, listOf(lt[1] as Token.Ident))
        is Token.Ident ->Wire(lt[3].toString(), (lt[1] as Token.Oper).g, listOf(lt[0] as Token.Ident, lt[2] as Token.Ident))
    }
    return res
}

class Lexer {

    fun tokenize(input: String): List<Token>  {
        val st = StringTokenizer(input)
        val tl = ArrayList<Token>()
        while (st.hasMoreTokens()) {
            val t = st.nextToken()
            val g = try { Gate.valueOf(t) } catch (e: IllegalArgumentException) { null }
            if (g != null) {
                tl.add(Token.Oper(g))
            } else {
                try {
                    val ival = t.toInt()
                    tl.add(Token.Value(ival))
                } catch (e: NumberFormatException) {
                    if (t.toLowerCase().equals(t)) {
                        tl.add(Token.Ident(t))
                    } else {
                        if (t == "->") {
                            // ok, skip
                        } else {
                            throw Exception("Parse failed at token ${t}")
                        }
                    }
                }
            }

        }
        return tl;
    }
}


