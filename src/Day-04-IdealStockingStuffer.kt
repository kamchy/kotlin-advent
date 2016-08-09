import java.math.BigInteger
import java.security.MessageDigest

fun main(args: Array<String>) {
    val intRange = 1..Int.MAX_VALUE
    val value = intRange.find { isCoin(it) }
    print("found value in range ${intRange}: ${value}")
}

fun potentialCoinString(value: Int) = bytesToHex(calcMD5(prefix +value))
fun isCoin(value: Int): Boolean = hasAtLeastLeadingZeros(5)(potentialCoinString(value))


val prefix = "yzbqklnj"

val md = MessageDigest.getInstance("MD5")
fun calcMD5(s : String): ByteArray {
    md.reset()
    return md.digest(s.toByteArray())
}
fun bytesToHex(b: ByteArray): String = BigInteger(1, b).toString(16).padStart(32, '0')
fun hasAtLeastLeadingZeros(n: Int) : (String) -> Boolean {
    val pref = "".padStart(n, '0')
    return { it.startsWith(pref)}
}


