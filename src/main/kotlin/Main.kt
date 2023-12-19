import kotlin.system.measureTimeMillis

const val TEST_COUNT = 5
fun main() {
    val correctAnswer = SequentialBfs().findDistances(CubicGraph(500), 0)
    val methods: List<Bfs> = listOf(SequentialBfs(), ParallelBfs())
    val (seqTimes, parTimes) = List(TEST_COUNT) {
        println("run$it")
        val graph = CubicGraph(500)
        println("created graph")
        methods.mapIndexed { index, method ->
            var result: IntArray?
            val time = measureTimeMillis {
                result = method.findDistances(graph, 0)

            }
            if (!validate(result, correctAnswer)) {
                throw IllegalStateException("incorrect answer")
            }
            println("method$index ended bfs with time $time")
            time
        }.run { this[0] to this[1] }
    }.unzip()

    println("=============================")

    println("sequential times (in ms)")
    println(seqTimes.toString())
    println("Average is ${seqTimes.average()}")
    println("parallel times (in ms)")
    println(parTimes.toString())
    println("Average is ${parTimes.average()}")

    println("=============================")
    println("ratio is ${seqTimes.average() / parTimes.average()}")
}

fun validate(array: IntArray?, correctAnswer: IntArray): Boolean {
    return correctAnswer.contentEquals(array)
}