object Pass {
    val currentPass ="vzbxkghb"
    val letters = "abcdefghjkmnpqrstuvwxyz"
    val pairs = letters.map { it.toString().repeat(2) }
    val triples =  (0 .. letters.length - 3).map { letters.substring(it, it+3) }
}
fun main(args: Array<String>) {
   println(Pass.pairs)
   println(Pass.triples)
}
