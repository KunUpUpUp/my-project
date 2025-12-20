package scala

import scala_java.SystemTime


object TimeDemo {
    def main(args: Array[String]): Unit = {
        val time = new SystemTime()
        println(time.milliseconds())
    }
}
