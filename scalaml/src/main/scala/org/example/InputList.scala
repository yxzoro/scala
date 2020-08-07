package org.example

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer

import org.influxdb.InfluxDB
import org.influxdb.BatchOptions
import org.influxdb.InfluxDBFactory
import org.influxdb.dto._


class InputList {

  def getInputList() :ListBuffer[(Int, Int, Double)] = { 
//   def getInputList() :ListBuffer[(Int, Int)] = {
    // conn influxdb
    val db = "metric_minute"
    val username = "root"
    val password = "root"
    val addr = "http://10.10.25.54:8090"
    val conn = InfluxDBFactory.connect(addr, username, password)
    
    println("influx conn ok")

    // read
    // [time, count_value, key, max_value, md5, mean_value, metric, min_value, square_mean_value, stddev_value, type]
    val sql = """
        select *
        from  "5d1492669958ce01d424ae4e" 
        where  '2020-01-01T00:00:00Z' <= time and 
        metric='prCpuUsage' and 
        cmdline='' and 
        "key"='/home/uaq/itom/flink' 
    """
    val results = conn.query(new Query(sql, db)).getResults()
    println("read results:")

    // get input list from influx
    // val inputList = new ListBuffer[(Int, Int)]()    
    val inputList = new ListBuffer[(Int, Int, Double)]()

    for( x <- results ){
        for( y <- x.getSeries() ) {
            // println( y.getName() )
            // println( y.getTags() )
            // println( y.getColumns() )                        
            for ( z <- y.getValues() ) {
            //   println(z)
            //   println(timeToInt(z(0).toString), z(3).toString.toDouble.toInt )
            //   inputList += Tuple2(timeToInt(z(0).toString), z(3).toString.toDouble.toInt)
              inputList += Tuple3( 
                  z(0).toString.subSequence(11, 13).toString.toInt ,
                  z(0).toString.subSequence(14, 16).toString.toInt ,
                  z(6).toString.toDouble 
                  )
            }
            // println("--------------")
        }
          
    }

    // write
    // Point p = Point.measurement(uuid)
    //           .fields(value_map)
    //           .build()
    // BatchPoints bp = BatchPoints.database(dbName).build()
    // bp.point(p)
    // conn.write(bp)
    // println("write ok")
    conn.close()
    println("\nconn closed")
    return inputList
  }  

  // def timeToInt(time: String) :Tuple2[Int, Int] = {
  //   //time: 2020-03-19T00:02:08Z
  //   //use hour and minute to make int
  //   // return time.subSequence(11, 13).toString.toInt * 60 + time.subSequence(14, 16).toString.toInt
  //   return Tuple2(time.subSequence(11, 13).toString.toInt, time.subSequence(14, 16).toString.toInt)
  // }  

}

