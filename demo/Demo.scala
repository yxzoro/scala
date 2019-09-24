// demo: import and use java class from scala
import java.util.Date;
import java.util.HashMap;

object Demo {
    def main(args: Array[String]): Unit = {
        println("Hello, world!")
        var date = new Date()
        println(date)
        var map = new HashMap[String, Int]();
        map.put("0", 1)
        println(map)
    }
}

