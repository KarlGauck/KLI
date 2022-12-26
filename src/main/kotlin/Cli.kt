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

        for (y in 0..maxY) {
            for (x in 0..maxX) {
                var text = ""

                val objs = objects.filter { it.contains(arrayOf(x, y)) }
                if (objs.isNotEmpty()) {
                    val obj = objs.groupBy { it.zIndex }.maxBy { it.key }.value[0]
                    text = " ${obj.pixel(arrayOf(x-obj.shape.x, y-obj.shape.y))} "
                } else {
                    val edges = mutableListOf<Boolean>()

                    ((-1..1)+(-1..1)).forEach loop@{ dx, dy ->
                        if (abs(dx)+abs(dy) == 0)
                            return@loop
                        edges += objects.any { it.contains(iA(x+dx, y+dy)) }
                    }

                    text += getBorder(edges)
                }

                print(text)
            }
            println()
        }
        lastPrintendHeight = maxY+2
    }

    private fun getBorder(edges: List<Boolean>): String = ANSI_GREEN + when (borders[edges]) {
        0 -> "═══"
        1 -> " ╔═"
        2 -> " ╚═"
        3 -> "═╝ "
        4 -> "═╗ "
        5 -> "═╣ "
        6 -> " ╠═"
        7 -> "═╩═"
        8 -> "═╦═"
        9 -> "═╬═"
        10 -> " ║ "
        11 -> "   "
        12 -> " ☐ "
        13 -> " ╞ "
        14 -> " ╡ "
        15 -> " ╥ "
        16 -> " ╨ "
        else -> "error"
    } + ANSI_RESET

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




// ##############################################################################################
// ######################################## Picture #############################################
// ##############################################################################################

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

    private val pixels = Array(shape.width) { Array(shape.height) { '·' } }
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



// ##############################################################################################
// ########################################## Text ##############################################
// ##############################################################################################

class Text(x: Int, y: Int, width: Int, height: Int, override var zIndex: Int = 0, var text: String): Object()  {

    override lateinit var shape: Shape

    init {
        shape = Shape().apply {
            this.x = x
            this.y = y
            this.width = width
            this.height = height
        }
    }

    override fun pixel(pos: Array<Int>): String {
        if (pos[1] == 0 && pos[0] in text.indices)
            return text[pos[0]].toString()
        return " "
    }

    override fun contains(pos: Array<Int>): Boolean {
        return pos[0] in shape.x until shape.x+shape.width && pos[1] in shape.y until shape.y+shape.height
    }

}

val borders = mutableMapOf(
    Pair(bL(1,1,1,1,1,1,1,1), 12),
    Pair(bL(1,1,1,1,1,1,1,0), 12),
    Pair(bL(1,1,1,1,1,1,0,1), 13),
    Pair(bL(1,1,1,1,1,1,0,0), 13),
    Pair(bL(1,1,1,1,1,0,1,1), 12),
    Pair(bL(1,1,1,1,1,0,1,0), 12),
    Pair(bL(1,1,1,1,1,0,0,1), 13),
    Pair(bL(1,1,1,1,1,0,0,0), 13),
    Pair(bL(1,1,1,1,0,1,1,1), 15),
    Pair(bL(1,1,1,1,0,1,1,0), 15),
    Pair(bL(1,1,1,1,0,1,0,1), 1),
    Pair(bL(1,1,1,1,0,1,0,0), 1),
    Pair(bL(1,1,1,1,0,0,1,1), 15),
    Pair(bL(1,1,1,1,0,0,1,0), 15),
    Pair(bL(1,1,1,1,0,0,0,1), 1),
    Pair(bL(1,1,1,1,0,0,0,0), 1),
    Pair(bL(1,1,1,0,1,1,1,1), 16),
    Pair(bL(1,1,1,0,1,1,1,0), 16),
    Pair(bL(1,1,1,0,1,1,0,1), 2),
    Pair(bL(1,1,1,0,1,1,0,0), 2),
    Pair(bL(1,1,1,0,1,0,1,1), 16),
    Pair(bL(1,1,1,0,1,0,1,0), 16),
    Pair(bL(1,1,1,0,1,0,0,1), 2),
    Pair(bL(1,1,1,0,1,0,0,0), 2),
    Pair(bL(1,1,1,0,0,1,1,1), 10),
    Pair(bL(1,1,1,0,0,1,1,0), 10),
    Pair(bL(1,1,1,0,0,1,0,1), 6),
    Pair(bL(1,1,1,0,0,1,0,0), 6),
    Pair(bL(1,1,1,0,0,0,1,1), 10),
    Pair(bL(1,1,1,0,0,0,1,0), 10),
    Pair(bL(1,1,1,0,0,0,0,1), 6),
    Pair(bL(1,1,1,0,0,0,0,0), 10),
    Pair(bL(1,1,0,1,1,1,1,1), 12),
    Pair(bL(1,1,0,1,1,1,1,0), 12),
    Pair(bL(1,1,0,1,1,1,0,1), 13),
    Pair(bL(1,1,0,1,1,1,0,0), 13),
    Pair(bL(1,1,0,1,1,0,1,1), 12),
    Pair(bL(1,1,0,1,1,0,1,0), 12),
    Pair(bL(1,1,0,1,1,0,0,1), 13),
    Pair(bL(1,1,0,1,1,0,0,0), 13),
    Pair(bL(1,1,0,1,0,1,1,1), 15),
    Pair(bL(1,1,0,1,0,1,1,0), 15),
    Pair(bL(1,1,0,1,0,1,0,1), 1),
    Pair(bL(1,1,0,1,0,1,0,0), 1),
    Pair(bL(1,1,0,1,0,0,1,1), 15),
    Pair(bL(1,1,0,1,0,0,1,0), 15),
    Pair(bL(1,1,0,1,0,0,0,1), 1),
    Pair(bL(1,1,0,1,0,0,0,0), 1),
    Pair(bL(1,1,0,0,1,1,1,1), 16),
    Pair(bL(1,1,0,0,1,1,1,0), 16),
    Pair(bL(1,1,0,0,1,1,0,1), 2),
    Pair(bL(1,1,0,0,1,1,0,0), 2),
    Pair(bL(1,1,0,0,1,0,1,1), 16),
    Pair(bL(1,1,0,0,1,0,1,0), 16),
    Pair(bL(1,1,0,0,1,0,0,1), 2),
    Pair(bL(1,1,0,0,1,0,0,0), 2),
    Pair(bL(1,1,0,0,0,1,1,1), 10),
    Pair(bL(1,1,0,0,0,1,1,0), 10),
    Pair(bL(1,1,0,0,0,1,0,1), 6),
    Pair(bL(1,1,0,0,0,1,0,0), 6),
    Pair(bL(1,1,0,0,0,0,1,1), 10),
    Pair(bL(1,1,0,0,0,0,1,0), 10),
    Pair(bL(1,1,0,0,0,0,0,1), 6),
    Pair(bL(1,1,0,0,0,0,0,0), 10),
    Pair(bL(1,0,1,1,1,1,1,1), 14),
    Pair(bL(1,0,1,1,1,1,1,0), 14),
    Pair(bL(1,0,1,1,1,1,0,1), 0),
    Pair(bL(1,0,1,1,1,1,0,0), 0),
    Pair(bL(1,0,1,1,1,0,1,1), 14),
    Pair(bL(1,0,1,1,1,0,1,0), 14),
    Pair(bL(1,0,1,1,1,0,0,1), 0),
    Pair(bL(1,0,1,1,1,0,0,0), 0),
    Pair(bL(1,0,1,1,0,1,1,1), 4),
    Pair(bL(1,0,1,1,0,1,1,0), 4),
    Pair(bL(1,0,1,1,0,1,0,1), 8),
    Pair(bL(1,0,1,1,0,1,0,0), 8),
    Pair(bL(1,0,1,1,0,0,1,1), 4),
    Pair(bL(1,0,1,1,0,0,1,0), 4),
    Pair(bL(1,0,1,1,0,0,0,1), 8),
    Pair(bL(1,0,1,1,0,0,0,0), 8),
    Pair(bL(1,0,1,0,1,1,1,1), 3),
    Pair(bL(1,0,1,0,1,1,1,0), 3),
    Pair(bL(1,0,1,0,1,1,0,1), 7),
    Pair(bL(1,0,1,0,1,1,0,0), 7),
    Pair(bL(1,0,1,0,1,0,1,1), 3),
    Pair(bL(1,0,1,0,1,0,1,0), 3),
    Pair(bL(1,0,1,0,1,0,0,1), 7),
    Pair(bL(1,0,1,0,1,0,0,0), 7),
    Pair(bL(1,0,1,0,0,1,1,1), 5),
    Pair(bL(1,0,1,0,0,1,1,0), 5),
    Pair(bL(1,0,1,0,0,1,0,1), 9),
    Pair(bL(1,0,1,0,0,1,0,0), 9),
    Pair(bL(1,0,1,0,0,0,1,1), 5),
    Pair(bL(1,0,1,0,0,0,1,0), 5),
    Pair(bL(1,0,1,0,0,0,0,1), 9),
    Pair(bL(1,0,1,0,0,0,0,0), 5),
    Pair(bL(1,0,0,1,1,1,1,1), 14),
    Pair(bL(1,0,0,1,1,1,1,0), 14),
    Pair(bL(1,0,0,1,1,1,0,1), 0),
    Pair(bL(1,0,0,1,1,1,0,0), 0),
    Pair(bL(1,0,0,1,1,0,1,1), 14),
    Pair(bL(1,0,0,1,1,0,1,0), 14),
    Pair(bL(1,0,0,1,1,0,0,1), 0),
    Pair(bL(1,0,0,1,1,0,0,0), 0),
    Pair(bL(1,0,0,1,0,1,1,1), 4),
    Pair(bL(1,0,0,1,0,1,1,0), 4),
    Pair(bL(1,0,0,1,0,1,0,1), 8),
    Pair(bL(1,0,0,1,0,1,0,0), 0),
    Pair(bL(1,0,0,1,0,0,1,1), 4),
    Pair(bL(1,0,0,1,0,0,1,0), 4),
    Pair(bL(1,0,0,1,0,0,0,1), 8),
    Pair(bL(1,0,0,1,0,0,0,0), 0),
    Pair(bL(1,0,0,0,1,1,1,1), 3),
    Pair(bL(1,0,0,0,1,1,1,0), 3),
    Pair(bL(1,0,0,0,1,1,0,1), 7),
    Pair(bL(1,0,0,0,1,1,0,0), 7),
    Pair(bL(1,0,0,0,1,0,1,1), 3),
    Pair(bL(1,0,0,0,1,0,1,0), 3),
    Pair(bL(1,0,0,0,1,0,0,1), 7),
    Pair(bL(1,0,0,0,1,0,0,0), 7),
    Pair(bL(1,0,0,0,0,1,1,1), 5),
    Pair(bL(1,0,0,0,0,1,1,0), 5),
    Pair(bL(1,0,0,0,0,1,0,1), 9),
    Pair(bL(1,0,0,0,0,1,0,0), 7),
    Pair(bL(1,0,0,0,0,0,1,1), 5),
    Pair(bL(1,0,0,0,0,0,1,0), 5),
    Pair(bL(1,0,0,0,0,0,0,1), 9),
    Pair(bL(1,0,0,0,0,0,0,0), 3),
    Pair(bL(0,1,1,1,1,1,1,1), 12),
    Pair(bL(0,1,1,1,1,1,1,0), 12),
    Pair(bL(0,1,1,1,1,1,0,1), 13),
    Pair(bL(0,1,1,1,1,1,0,0), 13),
    Pair(bL(0,1,1,1,1,0,1,1), 12),
    Pair(bL(0,1,1,1,1,0,1,0), 12),
    Pair(bL(0,1,1,1,1,0,0,1), 13),
    Pair(bL(0,1,1,1,1,0,0,0), 13),
    Pair(bL(0,1,1,1,0,1,1,1), 15),
    Pair(bL(0,1,1,1,0,1,1,0), 15),
    Pair(bL(0,1,1,1,0,1,0,1), 1),
    Pair(bL(0,1,1,1,0,1,0,0), 1),
    Pair(bL(0,1,1,1,0,0,1,1), 15),
    Pair(bL(0,1,1,1,0,0,1,0), 15),
    Pair(bL(0,1,1,1,0,0,0,1), 1),
    Pair(bL(0,1,1,1,0,0,0,0), 1),
    Pair(bL(0,1,1,0,1,1,1,1), 16),
    Pair(bL(0,1,1,0,1,1,1,0), 16),
    Pair(bL(0,1,1,0,1,1,0,1), 2),
    Pair(bL(0,1,1,0,1,1,0,0), 2),
    Pair(bL(0,1,1,0,1,0,1,1), 16),
    Pair(bL(0,1,1,0,1,0,1,0), 16),
    Pair(bL(0,1,1,0,1,0,0,1), 2),
    Pair(bL(0,1,1,0,1,0,0,0), 2),
    Pair(bL(0,1,1,0,0,1,1,1), 10),
    Pair(bL(0,1,1,0,0,1,1,0), 10),
    Pair(bL(0,1,1,0,0,1,0,1), 6),
    Pair(bL(0,1,1,0,0,1,0,0), 6),
    Pair(bL(0,1,1,0,0,0,1,1), 10),
    Pair(bL(0,1,1,0,0,0,1,0), 10),
    Pair(bL(0,1,1,0,0,0,0,1), 6),
    Pair(bL(0,1,1,0,0,0,0,0), 10),
    Pair(bL(0,1,0,1,1,1,1,1), 12),
    Pair(bL(0,1,0,1,1,1,1,0), 12),
    Pair(bL(0,1,0,1,1,1,0,1), 13),
    Pair(bL(0,1,0,1,1,1,0,0), 13),
    Pair(bL(0,1,0,1,1,0,1,1), 12),
    Pair(bL(0,1,0,1,1,0,1,0), 12),
    Pair(bL(0,1,0,1,1,0,0,1), 13),
    Pair(bL(0,1,0,1,1,0,0,0), 13),
    Pair(bL(0,1,0,1,0,1,1,1), 15),
    Pair(bL(0,1,0,1,0,1,1,0), 15),
    Pair(bL(0,1,0,1,0,1,0,1), 1),
    Pair(bL(0,1,0,1,0,1,0,0), 1),
    Pair(bL(0,1,0,1,0,0,1,1), 15),
    Pair(bL(0,1,0,1,0,0,1,0), 15),
    Pair(bL(0,1,0,1,0,0,0,1), 1),
    Pair(bL(0,1,0,1,0,0,0,0), 1),
    Pair(bL(0,1,0,0,1,1,1,1), 16),
    Pair(bL(0,1,0,0,1,1,1,0), 16),
    Pair(bL(0,1,0,0,1,1,0,1), 2),
    Pair(bL(0,1,0,0,1,1,0,0), 2),
    Pair(bL(0,1,0,0,1,0,1,1), 16),
    Pair(bL(0,1,0,0,1,0,1,0), 16),
    Pair(bL(0,1,0,0,1,0,0,1), 2),
    Pair(bL(0,1,0,0,1,0,0,0), 2),
    Pair(bL(0,1,0,0,0,1,1,1), 10),
    Pair(bL(0,1,0,0,0,1,1,0), 10),
    Pair(bL(0,1,0,0,0,1,0,1), 6),
    Pair(bL(0,1,0,0,0,1,0,0), 6),
    Pair(bL(0,1,0,0,0,0,1,1), 10),
    Pair(bL(0,1,0,0,0,0,1,0), 10),
    Pair(bL(0,1,0,0,0,0,0,1), 6),
    Pair(bL(0,1,0,0,0,0,0,0), 10),
    Pair(bL(0,0,1,1,1,1,1,1), 14),
    Pair(bL(0,0,1,1,1,1,1,0), 14),
    Pair(bL(0,0,1,1,1,1,0,1), 0),
    Pair(bL(0,0,1,1,1,1,0,0), 0),
    Pair(bL(0,0,1,1,1,0,1,1), 14),
    Pair(bL(0,0,1,1,1,0,1,0), 14),
    Pair(bL(0,0,1,1,1,0,0,1), 0),
    Pair(bL(0,0,1,1,1,0,0,0), 0),
    Pair(bL(0,0,1,1,0,1,1,1), 4),
    Pair(bL(0,0,1,1,0,1,1,0), 4),
    Pair(bL(0,0,1,1,0,1,0,1), 8),
    Pair(bL(0,0,1,1,0,1,0,0), 8),
    Pair(bL(0,0,1,1,0,0,1,1), 4),
    Pair(bL(0,0,1,1,0,0,1,0), 4),
    Pair(bL(0,0,1,1,0,0,0,1), 8),
    Pair(bL(0,0,1,1,0,0,0,0), 8),
    Pair(bL(0,0,1,0,1,1,1,1), 3),
    Pair(bL(0,0,1,0,1,1,1,0), 3),
    Pair(bL(0,0,1,0,1,1,0,1), 7),
    Pair(bL(0,0,1,0,1,1,0,0), 7),
    Pair(bL(0,0,1,0,1,0,1,1), 3),
    Pair(bL(0,0,1,0,1,0,1,0), 3),
    Pair(bL(0,0,1,0,1,0,0,1), 0),
    Pair(bL(0,0,1,0,1,0,0,0), 0),
    Pair(bL(0,0,1,0,0,1,1,1), 5),
    Pair(bL(0,0,1,0,0,1,1,0), 5),
    Pair(bL(0,0,1,0,0,1,0,1), 9),
    Pair(bL(0,0,1,0,0,1,0,0), 9),
    Pair(bL(0,0,1,0,0,0,1,1), 5),
    Pair(bL(0,0,1,0,0,0,1,0), 5),
    Pair(bL(0,0,1,0,0,0,0,1), 8),
    Pair(bL(0,0,1,0,0,0,0,0), 4),
    Pair(bL(0,0,0,1,1,1,1,1), 14),
    Pair(bL(0,0,0,1,1,1,1,0), 14),
    Pair(bL(0,0,0,1,1,1,0,1), 0),
    Pair(bL(0,0,0,1,1,1,0,0), 0),
    Pair(bL(0,0,0,1,1,0,1,1), 14),
    Pair(bL(0,0,0,1,1,0,1,0), 14),
    Pair(bL(0,0,0,1,1,0,0,1), 0),
    Pair(bL(0,0,0,1,1,0,0,0), 0),
    Pair(bL(0,0,0,1,0,1,1,1), 4),
    Pair(bL(0,0,0,1,0,1,1,0), 4),
    Pair(bL(0,0,0,1,0,1,0,1), 8),
    Pair(bL(0,0,0,1,0,1,0,0), 0),
    Pair(bL(0,0,0,1,0,0,1,1), 4),
    Pair(bL(0,0,0,1,0,0,1,0), 4),
    Pair(bL(0,0,0,1,0,0,0,1), 8),
    Pair(bL(0,0,0,1,0,0,0,0), 0),
    Pair(bL(0,0,0,0,1,1,1,1), 3),
    Pair(bL(0,0,0,0,1,1,1,0), 3),
    Pair(bL(0,0,0,0,1,1,0,1), 7),
    Pair(bL(0,0,0,0,1,1,0,0), 7),
    Pair(bL(0,0,0,0,1,0,1,1), 3),
    Pair(bL(0,0,0,0,1,0,1,0), 3),
    Pair(bL(0,0,0,0,1,0,0,1), 0),
    Pair(bL(0,0,0,0,1,0,0,0), 0),
    Pair(bL(0,0,0,0,0,1,1,1), 10),
    Pair(bL(0,0,0,0,0,1,1,0), 10),
    Pair(bL(0,0,0,0,0,1,0,1), 6),
    Pair(bL(0,0,0,0,0,1,0,0), 2),
    Pair(bL(0,0,0,0,0,0,1,1), 10),
    Pair(bL(0,0,0,0,0,0,1,0), 10),
    Pair(bL(0,0,0,0,0,0,0,1), 1),
    Pair(bL(0,0,0,0,0,0,0,0), 11)
)