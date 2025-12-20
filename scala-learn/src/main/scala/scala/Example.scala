package scala

object Example {
  def main(args: Array[String]): Unit = {
    println("Hello World")

    var factor = 3
    val multipe = (i: Int) => i * factor

    println(multipe(10))
  }
}
