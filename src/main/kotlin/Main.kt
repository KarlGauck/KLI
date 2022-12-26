fun main(args: Array<String>) {

    val b = true
    if (b) {
        val p1 = Picture(1, 1, 21, 10)
        Cli.addObject(p1)

        val text = Text(2, 11, 5, 1, text="LABEL")
        Cli.addObject(text)

        Cli.display()
    } else {
        val map = mutableMapOf<List<Boolean>, Int>()
        val s1 = "|  0  |  1 | 2  | 3  | 4  | 5  | 6  |  7  |  8  |  9  | 10  |   11  | 12  | 13  | 14  | 15  | 16  |"
        val s2 = "| ═══ | ╔═ | ╚═ | ═╝ | ═╗ | ═╣ | ╠═ | ═╩═ | ═╦═ | ═╬═ |  ║  | empty |  ☐  |  ╞  |  ╡  |  ╥  |  ╨  |"
        println(s1)
        println(s2)
        println(" _________ ")

        fun rec(d: Int = 0, l: List<Boolean> = listOf()) {
            if (d == 8) {
                while (true) {

                    println("|${l[0].c}${l[3].c}${l[5].c}|")
                    println("|${l[1].c} · ${l[6].c}|")
                    println("|${l[2].c}${l[4].c}${l[7].c}|")
                    println(" ‾‾‾‾‾‾‾‾‾ ")
                    println(l.intString())
                    val res = readlnOrNull()?.toIntOrNull()
                    if (res == null) {
                        print("\u001B[4A")
                        System.err.println("Please give valid input")
                        continue
                    }
                    print("\u001B[6A")
                    map[l] = res
                    break
                }
                return
            }
            rec(d + 1, l + true)
            rec(d + 1, l + false)
        }

        rec()
        for (kvp in map) println("Pair((${kvp.key.intString()}), ${kvp.value}),")
    }

}

val Boolean.c
    get() = if (this) " # " else " · "


