import java.util.concurrent.atomic.AtomicIntegerArray

class ParallelBfs : Bfs {
    override fun findDistances(graph: Graph, start: Int): IntArray {
        var frontier = listOf(start)
        val distances = IntArray(graph.size)
        val visited = AtomicIntegerArray(graph.size)

        visited.set(0, 1)
        var curDist = 0
        with(ParallelUtils()) {
            while (frontier.isNotEmpty()) {
                curDist++
                var deg = IntArray(frontier.size)
                parallelFor(0, frontier.size) { i ->
                    deg[i] = graph.getNeighbours(frontier[i]).size
                }
                deg = deg.parallelScan()
                val fSize = deg.last()
                val nextFrontier = IntArray(fSize) { -1 }
                parallelFor(0, deg.size - 1) { i ->
                    var cur = deg[i]
                    for (u in graph.getNeighbours(frontier[i])) {
                        if (visited.compareAndSet(u, 0, 1)) {
                            distances[u] = curDist
                            nextFrontier[cur++] = u
                        }
                    }
                }
                frontier = nextFrontier.filter { it != -1 }
            }
        }
        return distances

    }

}