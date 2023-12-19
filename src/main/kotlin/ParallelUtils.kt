import java.util.concurrent.ForkJoinPool
import java.util.concurrent.RecursiveAction


class ParallelUtils {
    companion object {
        private const val PARALLEL_FOR_THRESHOLD = 1_000
        private const val PARALLEL_SCAN_THRESHOLD = 10_000
        private const val THREADS = 4
        private val pool = ForkJoinPool(THREADS)
    }

    fun parallelFor(left: Int, right: Int, body: (Int) -> Unit) {
        pool.invoke(ParallelForAction(left, right, body))
    }

    fun IntArray.parallelScan(): IntArray {
        return ParallelScan(this).scan()
    }

    fun IntArray.parallelMap(lambda: (Int) -> Int): IntArray {
        val result = IntArray(this.size)
        parallelFor(0, this.size) { i ->
            result[i] = lambda(this[i])
        }
        return result
    }

    fun IntArray.parallelFilter(lambda: (Int) -> Boolean): IntArray {
        val flags = this.parallelMap { i ->
            if (lambda(i)) {
                1
            } else {
                0
            }
        }
        val sums = flags.parallelScan()
        val result = IntArray(sums.last())
        parallelFor(0, this.size) { i ->
            if (flags[i] == 1) {
                result[sums[i]] = this[i]
            }
        }

        return result
    }

    private class ParallelForAction(val left: Int, val right: Int, val body: (Int) -> Unit) :
        RecursiveAction() {
        override fun compute() {
            if (right - left < PARALLEL_FOR_THRESHOLD) {
                for (i in left until right) {
                    body(i)
                }
                return
            }
            val m = (left + right) / 2
            invokeAll(
                ParallelForAction(left, m, body),
                ParallelForAction(m, right, body)
            )
        }
    }

    private class ParallelScan(val data: IntArray) {

        val tree = IntArray(data.size * 4)
        val result = IntArray(data.size + 1)

        inner class ScanUpAction(
            val left: Int,
            val right: Int,
            val index: Int
        ) : RecursiveAction() {
            override fun compute() {
                if (right - left < PARALLEL_SCAN_THRESHOLD) {
                    for (i in left..right) {
                        tree[index] += data[i]
                    }
                    return
                }
                val m = (left + right) / 2
                invokeAll(
                    ScanUpAction(left, m, index * 2),
                    ScanUpAction(m + 1, right, index * 2 + 1)
                )
                tree[index] += tree[index * 2] + tree[index * 2 + 1]
            }

        }

        inner class ScanDownAction(
            val left: Int,
            val right: Int,
            val index: Int,
            val prefix: Int
        ) : RecursiveAction() {
            override fun compute() {
                if (right - left < PARALLEL_SCAN_THRESHOLD) {
                    result[left + 1] = prefix + data[left]
                    for (i in left + 1..right) {
                        result[i + 1] = data[i] + result[i]
                    }
                    return
                }
                val m = (left + right) / 2
                invokeAll(
                    ScanDownAction(left, m, index * 2, prefix),
                    ScanDownAction(m + 1, right, index * 2 + 1, prefix + tree[index * 2])
                )
            }

        }

        fun scan(): IntArray {
            pool.invoke(ScanUpAction(0, data.size - 1, 1))
            pool.invoke(ScanDownAction(0, data.size - 1, 1, 0))
            return result
        }
    }
}