import sun.plugin.dom.exception.InvalidStateException

/**
* Advent: http://adventofcode.com/day/11
*/

val letters = "abcdefghjkmnpqrstuvwxyz"
val pairs = letters.map { it.toString().repeat(2) }
val triples = (0 .. letters.length - 3).map { letters.substring(it, it + 3) }

fun main(args: Array<String>) {
    log(letters)
    log(pairs.toString())
    log(triples.toString())
    log(nextPass("vzbxkghb"))

}


fun nextPass(pass: String): String = simpleNextPass(pass)

fun simpleNextPass(pass: String): String {
    for (c in LetterIterator('b', "abcd").iterator().asSequence().take(20)) {
        println("Letter ${c}")
    }

  /*  stringIterator("ca", "abcd").iterator().asSequence().take(20).forEach {
        println("Word ${it}")
    }*/
    return "end"
}

interface Beterator<T> {
    fun get(): T
    fun next(): Unit
}

class LetterIterator(val from: Char, val letters: String) : Beterator<Char> {
    var idx = letters.indexOfFirst { it == from }
    override fun get(): Char = if (letters.indices.contains(idx)) {letters[idx]} else {throw InvalidStateException("")}
    override fun next() {
        idx = (idx + 1)
    }
}


class PingingIterator<P, Q>(vararg val iterators: Beterator<P>, val f: (List<P>) -> Q) : Beterator<Q> {
    override fun get(): Q = f(iterators.map { it.get() })

    override fun next() {
        val firstNullOrNull = iterators.indexOfFirst { it.get() == null }
        if (firstNullOrNull != null) {

        } else {

        }
    }

}


class CountingIterator(s: String)

/*
fun stringIterator (s: String, letters: String): MyIterator<String> {
    val v = s.map { LetterIterator(it, letters) }
    //return CompoundIterator(v, { it.fold("", {a, c -> a+c})})
    return
}
*/

fun <T> Beterator<T>.iterator(): Iterator<T> {
  class Internal : Iterator<T> {
      override fun hasNext(): Boolean {
          return this@iterator.get() != null
      }

      override fun next(): T {
          this@iterator.next()
          return this@iterator.get()
      }
  }
    return Internal();
}

private class SimpleIterator(val s: String) : AbstractIterator<String>() {
    val prev = if (s.isNotBlank()) SimpleIterator(s.dropLast(1)) else null
    val next = if (s.isNotBlank()) letters.substringAfter(s.last()).iterator() else null

    override fun computeNext() {
        if (next != null && next.hasNext()) {

        }
    }


}



