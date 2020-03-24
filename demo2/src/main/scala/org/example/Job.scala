package org.example

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer

import org.influxdb.InfluxDB
import org.influxdb.BatchOptions
import org.influxdb.InfluxDBFactory
import org.influxdb.dto._


object Job {
  def main(args: Array[String]) {

      val input = new InputList()
      println( input.getInputList() )

}
}

