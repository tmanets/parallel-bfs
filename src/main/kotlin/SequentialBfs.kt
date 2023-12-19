import java.util.*

class SequentialBfs : Bfs {
    override fun findDistances(graph: Graph, start: Int): IntArray {
        val queue = LinkedList<Int>()
        val distances = IntArray(graph.size)
        val visited = Array(graph.size) { 0 }

        queue.add(start)
        visited[start] = 1
        distances[start] = 0

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            for (neighbour in graph.getNeighbours(current)) {
                if (visited[neighbour] == 0) {
                    visited[neighbour] = 1
                    queue.add(neighbour)
                    distances[neighbour] = distances[current] + 1
                }
            }
        }
        return distances
    }
}