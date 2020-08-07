package org.example

import java.io._
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

class AIKNN {

  def start(args: Array[String]) {
    val env = ExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)


    // val list = List( (1,2),(2,4),(334,6),(3,64),(35,6),(323,6),(35,6),(32,6),(3,65) )
    val list = ((new InputList()).getInputList()).map{
          x => Tuple2( x._1*60+x._2, x._3)
    }

    val dataset = env.fromCollection(list)
    // val dataset:DataSet[(Double,Double)] = env.fromCollection(list)
    // val dataset:DataSet[(Double,Double)] = env.fromElements(
    //   (1,2),(2,4),(334,6),(3,64),(35,6),(323,6),(35,6),(32,6),(3,65) )
    val datasetLV:DataSet[LabeledVector] = dataset.map{
          x => LabeledVector(x._2, DenseVector(x._1))
    }


    // Obtain training and testing data set
    val trainTestData: TrainTestDataSet[LabeledVector] = Splitter.trainTestSplit(datasetLV)

    val trainingData: DataSet[Vector] = trainTestData.training.map(lv => lv.vector)
    // val testingData: DataSet[Vector] = trainTestData.testing.map(lv => lv.vector)
    val testList = new ListBuffer[DenseVector]()
    for( h <- 1 to 2){
      for( m <- 0 to 59){
          testList += DenseVector(h*60+m)
      }
    }
    val testingData: DataSet[DenseVector] =  env.fromCollection(testList)

    // Create multiple linear regression learner
    val mlr = KNN()
      .setK(args(1).toInt)
      .setBlocks(args(2).toInt)
      .setDistanceMetric(SquaredEuclideanDistanceMetric())
      .setUseQuadTree(false)
      .setSizeHint(CrossHint.SECOND_IS_SMALL)


    // Fit the linear model to the provided data
    println("train")
    mlr.fit(trainingData)

    // Calculate the predictions for the test data
    println("predict")
    val predictions = mlr.predict(testingData)

    val predictionsR = predictions.map{
          x => ( x._1.apply(0), x._2(0).apply(0) ) 
    }
    
    println("writing file...")
    val writer = new PrintWriter(new File("/tmp/zpredictions2.txt" ))
    for (x <- predictionsR.collect()){
      writer.write(x.toString+"\n")
    }
    writer.close()
    println("writing file ok.")


    predictionsR.print()


    println("KNN end\n")
  }
}
