class CubicGraph(private val cubeSize: Int) : Graph {
    override val size: Int
        get() = cubeSize * cubeSize * cubeSize

    override fun getNeighbours(index: Int): IntArray {
        val x = index / (cubeSize * cubeSize)
        val y = index / cubeSize % cubeSize
        val z = index % cubeSize
        val neighbours = mutableListOf<Int>()
        if (x != 0) {
            neighbours.add((x - 1) * cubeSize * cubeSize + y * cubeSize + z)
        }
        if (x != cubeSize - 1) {
            neighbours.add((x + 1) * cubeSize * cubeSize + y * cubeSize + z)
        }
        if (y != 0) {
            neighbours.add(x * cubeSize * cubeSize + (y - 1) * cubeSize + z)
        }
        if (y != cubeSize - 1) {
            neighbours.add(x * cubeSize * cubeSize + (y + 1) * cubeSize + z)
        }
        if (z != 0) {
            neighbours.add(x * cubeSize * cubeSize + y * cubeSize + z - 1)
        }
        if (z != cubeSize - 1) {
            neighbours.add(x * cubeSize * cubeSize + y * cubeSize + z + 1)
        }
        return neighbours.toIntArray()
    }
}