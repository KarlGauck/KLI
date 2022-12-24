import kotlin.math.abs

val ANSI_RESET = "\u001B[0m"
val ANSI_BLACK = "\u001B[30m"
val ANSI_RED = "\u001B[31m"
val ANSI_GREEN = "\u001B[32m"
val ANSI_YELLOW = "\u001B[33m"
val ANSI_BLUE = "\u001B[34m"
val ANSI_PURPLE = "\u001B[35m"
val ANSI_CYAN = "\u001B[36m"
val ANSI_WHITE = "\u001B[37m"

object Cli {

    private var lastPrintendHeight = 0

    private val objects = mutableListOf<Object>()

    private val maxX get() = if (objects.isEmpty()) 0 else objects.maxOf { it.shape.x + it.shape.width }
    private val maxY get() = if (objects.isEmpty()) 0 else objects.maxOf { it.shape.y + it.shape.height }

    fun display() {
        print("\u001B[${lastPrintendHeight}A")

        for (y in 0..maxY+1) {
            for (x in 0..maxX+1 ) {
                var text = ""

                val objs = objects.filter { it.contains(arrayOf(x, y)) }
                if (objs.isNotEmpty()) {
                    val obj = objs.groupBy { it.zIndex }.maxBy { it.key }.value[0]
                    text = obj.pixel(arrayOf(x-obj.shape.x, y-obj.shape.y))
                } else {
                    val edges = mutableListOf<Boolean>()

                    ((-1..1)+(-1..1)).forEach loop@{ dx, dy ->
                        if (abs(dx)+abs(dy) == 0)
                            return@loop
                        //print("[$dx,$dy]")
                        edges += objects.any { it.contains(iA(x+dx, y+dy)) }
                    }

                    text += getBorder(edges)
                }

                text += "  "
                print(text)
            }
            println()
        }
        lastPrintendHeight = maxY+2
    }

    private fun getBorder(edges: List<Boolean>): Char = when {
        //    ul, u, ur, r, dr, d, dl, l
        edges.contentEquals(bL(0, 1, 0, 1, 0, 1, 0, 1)) -> '+'
        edges.contentEquals(bL(0, 0, 0, 1, 0, 0, 0, 0)) ||
                edges.contentEquals(bL(0, 0, 0, 0, 0, 0, 0, 1)) -> '|'
        edges.contentEquals(bL(0, 0, 0, 0, 0, 1, 0, 0)) -> '_'
        edges.contentEquals(bL(0, 1, 0, 0, 0, 0, 0, 0)) -> '‾'
        edges.contentEquals(bL(0, 0, 0, 0, 0, 0, 1, 0)) -> '◜'
        edges.contentEquals(bL(0, 0, 1, 0, 0, 0, 0, 0)) -> '◟'
        else -> {
            //println(edges.intString())
            '#'
        }
    }


    fun addObject(obj: Object) {
        objects.add(obj)
    }

}

class Shape {
    var x = 0
    var y = 0
    var width = 0
    var height = 0
}

abstract class Object {

    abstract var zIndex: Int

    abstract var shape: Shape

    abstract operator fun contains(pos: Array<Int>): Boolean

    abstract fun pixel(pos: Array<Int>): String

}

class Picture(x: Int, y: Int, width: Int, height: Int, override var zIndex: Int = 0): Object() {

    override lateinit var shape: Shape

    init {
        shape = Shape().apply {
            this.x = x
            this.y = y
            this.width = width
            this.height = height
        }
    }

    private val pixels = Array(shape.width) { Array(shape.height) { 'T' } }
    private val colors = Array(shape.width) { Array(shape.height) { "" } }

    fun setPixel(x: Int, y: Int, char: Char, color: String) {
        if (x !in pixels.indices || y !in pixels[x].indices)
            println("$ANSI_RED Pixel out of bounds")
        pixels[x, y] = char
        colors[x, y] = color
    }

    override fun pixel(pos: Array<Int>): String {
        return "${colors[pos[0], pos[1]]}${pixels[pos[0], pos[1]]}$ANSI_RESET"
    }

    override fun contains(pos: Array<Int>): Boolean {
        return pos[0] in shape.x until shape.x+shape.width && pos[1] in shape.y until shape.y+shape.height
    }

}