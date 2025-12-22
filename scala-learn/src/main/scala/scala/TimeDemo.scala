package scala

import scala_java.SystemTime

import java.util.concurrent.TimeUnit



object TimeDemo {
    def main(args: Array[String]): Unit = {
        val time = new SystemTime()
        println(time.milliseconds())

        println(TimeUnit.NANOSECONDS.toMillis(System.nanoTime))
    }
}
