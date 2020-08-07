package org.example

import org.apache.flink.api.scala._

object Job {
  def main(args: Array[String]) {

      // println("WordCount start...")          
      // val d = new WordCount()
      // d.start()

      println("ALS start...")            
      val c = new AIALS()
      c.start(args)

      // println("KNN start...")            
      // val a = new AIKNN()
      // a.start(args)

      // println("MLS start...")      
      // val b = new AIMLS()
      // b.start(args)


  }
}
