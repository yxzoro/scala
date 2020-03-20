package org.example

import org.influxdb.InfluxDB
import org.influxdb.BatchOptions
import org.influxdb.InfluxDBFactory
import org.influxdb.dto._


object Job {
  def main(args: Array[String]) {
   
    // conn influxdb
    val db = "metric_minute"
    val username = "root"
    val password = "root"
    val addr = "http://10.10.25.54:8090"
    val conn = InfluxDBFactory.connect(addr, username, password)
    println("influx conn ok")

    // read
    val results = conn.query(new Query("SELECT * FROM mt order by time desc limit 1000", db))
    val oneRow = results.getResults().get(0)
    println("read ok")







    // write
    // Point p = Point.measurement(uuid)
    //           .fields(value_map)
    //           .build()
    // BatchPoints bp = BatchPoints.database(dbName).build()
    // bp.point(p)
    // conn.write(bp)
    // println("write ok")
    conn.close()
  }
}

