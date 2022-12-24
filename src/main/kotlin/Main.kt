fun main(args: Array<String>) {
    val pic = Picture(3, 3, 10, 5)
    pic.setPixel(0, 3, 'A', ANSI_YELLOW)
    Cli.addObject(pic)
    Cli.display()

    for (x in 1..4) {
        pic.setPixel(x-1, 3, ' ', ANSI_YELLOW)
        pic.setPixel(x, 3, 'A', ANSI_YELLOW)
        Cli.display()

        Thread.sleep(400)
    }

}