import org.junit.Test

import org.junit.Assert.*

class Day_12_JSAbacusTest {
    val redSamples = listOf(
            """[1,2,3]""",
            """[1,{"c":"red","b":2},3]""",
            """{"d":"red","e":[1,2,3,4],"f":5}""",
            """[1,"red",5]"""
    )
    val redSamplesSums = listOf(6, 4, 0, 6)
    val redSamplesHasRedProperty = listOf(false, false, true, false)

    val simpleParsingSamples = listOf(
            """[-123,"ala",100]""",
            """{"ala":123,"kasia":"ula","zii":[1,2,3,"i"],"mapa":{"a":1,"b":2}}""",
            """[[[3]]]""",
            """{"a":{"b":4},"c":-1}""",
            """{"a":[-1,1]}""",
            """[-1,{"a":1}]""",
            "{}",
            "[]")

    val simpleParsingSums = listOf(-23, 132,  3, 3, 0, 0, 0, 0)



     fun <T> assertResults(samples : List<String>, expectedResults : List<T>, resultFun : (JsonObj) -> T) {
        samples.zip(expectedResults).forEach { sample ->
            val jsonObj = JsonP().parse(sample.first)
            val res = resultFun(jsonObj)
            assertEquals("For jsonobj $jsonObj:\nExpected: ${sample.second}, actual: $res", sample.second, res)
        }
    }


    @Test
    fun testParser() {
        (simpleParsingSamples + redSamples).forEach {
            val jsonObj = JsonP().parse(it)
            assertEquals(it, jsonObj.toString())
        }
    }

    @Test
    fun testSumNumsTest() {
        assertResults(simpleParsingSamples, simpleParsingSums, ::sumOfNums)
    }

    @Test
    fun testSumNumsColor() {
        assertResults(redSamples, redSamplesSums, sumOfNumsColorFun("red"))
    }

}