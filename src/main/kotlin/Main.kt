fun main(args: Array<String>) {
    val p1 = Picture(1, 1, 21, 10)
    Cli.addObject(p1)

    val text = Text(1, 13, 5, 1, text="LABEL")
    Cli.addObject(text)

    val bar = ProgressBar(8, 13, 14, 1, 0.3)
    Cli.addObject(bar)

    val label = Text(x = 7, y = 14, width = 8, height = 1, zIndex = 1, text="PROGRESS")
    Cli.addObject(label)

    Cli.display()
}