import java.io.File
import java.util.*

fun log(s: String) {
    //println(s)
}
fun main(args: Array<String>) {
    val s = Store()

    val realLines = File("input-wires").readLines()

    realLines.forEach {
        parse( it, s)
    }
    val value = eval(Arg.Ident(Name.Of("a")), s)
    println("Wire a has value $value")


    s.addExpr(Name.Of("b"), Expr.UnaryOp.Nop(Arg.Value(value)))
    s.cleanValuesWithExpr()

    val newValue: Int = eval(Arg.Ident(Name.Of("a")), s)
    println("Wire a has now value $newValue")




}


fun parse(line: String, s: Store) {
    fun isOper(t: String): Boolean = t in setOf("OR", "AND", "NOT", "LSHIFT", "RSHIFT")

    val st = MyStringTokenizer(line)
    val t = st.nextToken()
    if (isOper(t)) {
        val arg = parseArg(st)
        if (t == "NOT") {
            val e = Expr.UnaryOp.Not(arg)
            val output = parseOutIdent(st)
            s.addExpr(output, e)
        } else {
            throw Exception("Invalid unary op.")
        }
    } else {
        if (isValue(t) || isIdent(t)) {
            val arg = if (isValue(t)) Arg.Value(t.toInt()) else (Arg.Ident(Name.Of(t)))
            val output = try { parseOutIdent(st) } catch (p: ParseException)  {
                val (n, e) = parseBinary(arg, p.token, st)
                s.addExpr(n, e)
                null
            }
            if (output != null) {
                val e = Expr.UnaryOp.Nop(arg)
                s.addExpr(output, e)
            }

        } else {
            throw Exception("Value expected")
        }
    }
}

class MyStringTokenizer(line: String) : StringTokenizer(line) {
    override fun nextToken(): String {
        val  nt = super.nextToken()
        log("Next Token: ${nt}")
        return nt
    }
}

fun parseBinary(first: Arg, token: String, st: StringTokenizer): Pair<Name, Expr> {
    val binaryOps = setOf("AND", "OR", "LSHIFT", "RSHIFT")
    val second = parseArg(st)
    val binop = when (token) {
        "AND" -> Expr.BinaryOp.And(first, second)
        "OR" -> Expr.BinaryOp.Or(first, second)
        "LSHIFT" -> Expr.BinaryOp.Shl(first, second)
        "RSHIFT" -> Expr.BinaryOp.Shr(first, second)
        else -> {
            throw ParseException("Expected one of ${binaryOps.joinToString { "," }}", token)
        }
    }
    val name = parseOutIdent(st)
    return (name to binop)
}

fun isIdent(t: String): Boolean = t.toLowerCase() == t

fun parseOutIdent(st: StringTokenizer): Name {
    if (st.hasMoreTokens()) {
        parseArrow(st)
        return parseName(st)
    } else {
        throw NoMoreTokensException()
    }
}

fun parseName(st: StringTokenizer): Name {
    if (st.hasMoreTokens()) {
        val tok = st.nextToken()
        if (tok.toLowerCase() == tok) {
            return Name.Of(tok)
        } else {
            throw ParseException("Lowercase name expected", tok)
        }
    } else {
        throw NoMoreTokensException()
    }
}

fun parseArrow(st: StringTokenizer) {
    if (st.hasMoreTokens()) {
        val tok = st.nextToken()
        if (tok == "->") {
            // parsed
        } else {
            throw ParseException("Expected ->", tok)
        }
    }
}

class ParseException(val reason: String, val token: String) : Exception(reason)
class NoMoreTokensException : Exception()
fun isValue(t: String): Boolean = try { t.toInt(); true } catch (e: NumberFormatException) { false}
fun parseArg(st: StringTokenizer): Arg =
    if (st.hasMoreTokens()) {
        val a = st.nextToken()
        if (isValue(a)) Arg.Value(a.toInt()) else Arg.Ident(Name.Of(a))
    } else {
        throw Exception("Arg expected")
    }


sealed class Name (val s: String) {
    class Of(a: String) : Name(a)
    object Empty : Name("")

    override fun toString(): String {
        return "Name[${s}]"
    }

    override fun equals(other: Any?): Boolean {
        return if ((other == null) || !(other is Name)) false else s.equals(other.s)
    }

    override fun hashCode(): Int {
        return s.hashCode()
    }
}
sealed class Arg(val a: Any) {
    class Value(val v: Int) : Arg(v)
    class Ident(val n: Name) : Arg(n)

    override fun toString(): String {
        return "Argument[${a}]"
    }

    override fun equals(other: Any?): Boolean {
        return if ((other == null) || !(other is Arg)) false else a.equals(other.a)
    }

    override fun hashCode(): Int {
        return a.hashCode()
    }
}
fun Expr.name(): String  = when (this) {
    is Expr.UnaryOp.Nop -> ""
    is Expr.UnaryOp.Not -> "NOT"
    is Expr.BinaryOp.And -> "AND"
    is Expr.BinaryOp.Or -> "OR"
    is Expr.BinaryOp.Shl -> "<<"
    is Expr.BinaryOp.Shr -> ">>"
    else  -> ""
}

sealed class Expr {
    open class UnaryOp(val f: (Int) -> Int, val input: Arg) : Expr() {
        class Nop(i: Arg) : UnaryOp( { it }, i )
        class Not(i: Arg) : UnaryOp ( { it.inv() }, i)

        override fun toString(): String {
            return "${name()} ${input}"
        }
    }
    open class BinaryOp(val f: (Int, Int) -> Int, val first: Arg, val second: Arg) : Expr() {
        class And(f: Arg, s: Arg) : BinaryOp ({ a, b -> a.and(b) }, f, s)
        class Or(f: Arg, s: Arg) : BinaryOp ({ a, b -> a.or(b) }, f, s)
        class Shl(f: Arg, s: Arg) : BinaryOp ({ a, b -> a.shl(b) }, f, s)
        class Shr(f: Arg, s: Arg) : BinaryOp ({ a, b -> a.shr(b) }, f, s)

        override fun toString(): String {
            return "${first} ${name()} ${second}"
        }
    }

}

fun eval(e: Expr, s: Store): Int { 
    val result: Int = when(e) {
        is Expr.UnaryOp -> e.f(eval(e.input, s))
        is Expr.BinaryOp -> e.f(eval(e.first, s), eval(e.second, s))
        else -> throw Exception("Not supported")
    }
    return result;
}


class Store {
    private val nameToExpr: MutableMap<Name, Expr> = HashMap()
    private val nameToValue: MutableMap<Name, Int> = HashMap()

    fun getExpr(s: Name) : Expr  {
        log("Get expr for $s: ${nameToExpr[s]}")
        if (nameToExpr.containsKey(s)) {
           return nameToExpr[s]!!
        } else {
            throw Exception("Cannot find expression for ${s}")
        }
    }
    fun setValue(s: Name, v: Int) {
        log("Adding value to store: ${s} = ${v}")
        nameToValue.put(s, v)
    }

    fun getValue(n: Name): Int? = nameToValue.get(n)

    fun addExpr(s: Name, e: Expr) {
        nameToExpr.put(s, e)
        log("(${nameToExpr.containsKey(s)} - Adding ${e} -> ${s}, store:\n" +
                nameToExpr.entries.joinToString (transform = { e -> "${e.key} - ${e.value}"}, separator = "\n"))
    }

    fun cleanValuesWithExpr() {
        val namesHavingExpr = nameToValue.filterKeys { it in nameToExpr.keys }.map { it.key }
        namesHavingExpr.forEach { nameToValue.remove(it) }
    }
}
fun eval(a: Arg, store: Store): Int =
    when (a) {
        is Arg.Ident -> {
            val ready = store.getValue(a.n)
            if (ready != null) {
                ready
            } else {
                val e = store.getExpr(a.n)
                val value = eval(e, store)
                store.setValue(a.n, value)
                value
            }
        }
        is Arg.Value -> a.v
    }

