package org.example

import scala.collection.mutable.ListBuffer

import org.apache.flink.api.scala._
import org.apache.flink.ml.common.LabeledVector
import org.apache.flink.ml.math.DenseVector
import org.apache.flink.ml.regression.MultipleLinearRegression
import org.apache.flink.api.scala.DataSet
import org.apache.flink.ml.preprocessing.Splitter

import org.apache.flink.ml.preprocessing.Splitter.TrainTestDataSet
import org.apache.flink.ml.math.Vector

import org.apache.flink.api.common.operators.base.CrossOperatorBase.CrossHint
import org.apache.flink.api.scala._
import org.apache.flink.ml.nn.KNN
import org.apache.flink.ml.math.Vector
import org.apache.flink.ml.metrics.distances.SquaredEuclideanDistanceMetric

import org.apache.flink.ml.recommendation.ALS
import org.apache.flink.ml.common.ParameterMap
import java.io._


class AIALS {

  def start(args: Array[String]) {
    val env = ExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    // val list: List[(Int, Int, Double)] = List( 
    //   (1,2,13.0),(2,4,10.0),(334,6,10.0),(3,64,10.0),(35,6,10.0),(323,6,10.0),(35,6,14.0) )
    val list = (new InputList()).getInputList()


    // Obtain training and testing data set
    val trainingData: DataSet[(Int, Int, Double)] = env.fromCollection(list )

    // simulate test data
    val testList = new ListBuffer[(Int, Int)]()
    for( h <- 1 to 1){
      for( m <- 0 to 59){
          testList += Tuple2(h, m)
      }
    }
    val testingData: DataSet[(Int, Int)] =  env.fromCollection(testList)

    // Create multiple linear regression learner
    // Setup the ALS learner
    val als = ALS()
    .setIterations( args(0).toInt )
    .setNumFactors( args(1).toInt )
    .setBlocks( args(2).toInt )
    // .setTemporaryPath("/tmp")

    // Set the other parameters via a parameter map
    val parameters = ParameterMap()
    .add(ALS.Lambda, args(3).toDouble )
    .add(ALS.Seed, args(4).toLong )

    // Fit the linear model to the provided data
    println("train")
    als.fit(trainingData, parameters)

    // Calculate the predictions for the test data
    println("predict")
    val predictions = als.predict(testingData)
    
    println("writing file...")
    val writer = new PrintWriter(new File("/tmp/zpredictions1.txt" ))
    for (x <- predictions.collect()){
      writer.write(x.toString+"\n")
    }
    writer.close()
    println("writing file ok.")

    predictions.print()
    // predictions.writeAsText("file:///tmp/predictions")
  
    // env.execute("flinkMLDemo")
    println("ALS end\n")
  }
}
