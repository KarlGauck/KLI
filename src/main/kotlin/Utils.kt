operator fun <T> Array<Array<T>>.get(x: Int, y: Int): T = this[x][y]

operator fun <T> Array<Array<T>>.set(x: Int, y: Int, value: T) {
    this[x][y] = value
}

// ##############################################################################################
// ################################### Structure Builders #######################################
// ##############################################################################################

// booleanList
fun bL(vararg b: Boolean) = b.toMutableList()

fun bL(vararg i: Int) = i.map { it != 0 }.toMutableList()

// booleanArray
fun bA(vararg b: Boolean) = b.toTypedArray()

fun bA(vararg i: Int) = i.map { it != 0 }.toTypedArray()

// intList
fun iL(vararg i: Int) = i.toMutableList()

// intArray
fun iA(vararg i: Int) = i.toTypedArray()

// ##############################################################################################
// ####################################### Structures ###########################################
// ##############################################################################################

// display boolean array with integers
fun Array<Boolean>.intString() = "[${this.fold(""){ r, it -> r + if(it) "1" else "0" }}]"

// display boolean list with integers
fun List<Boolean>.intString() = "[${this.fold(""){ r, it -> r + if(it) "1" else "0" }}]"

// contentEquals list
fun <T> List<T>.contentEquals(other: List<T>): Boolean {
    if (other.size != this.size)
        return false
    var equal = true
    this.forEachIndexed { index, it ->
        if (other[index] != it)
            equal = false
    }
    return equal
}

// ##############################################################################################
// ################################# Convenient loop functions ##################################
// ##############################################################################################

operator fun IntRange.plus(other: IntRange) = Range2D(this, other)

class Range2D(val range1: IntRange, val range2: IntRange) {

    fun forEach(op: (Int, Int) -> Unit) {
        range1.forEach { x ->
            range2.forEach { y ->
                op(x, y)
            }
        }
    }

    operator fun plus(other: IntRange) = Range3D(range1, range2, other)

}

class Range3D(val range1: IntRange, val range2: IntRange, val range3: IntRange) {

    fun forEach(op: (Int, Int, Int) -> Unit) {
        range1.forEach { x ->
            range2.forEach { y ->
                range3.forEach { z ->
                    op(x, y, z)
                }
            }
        }
    }

}