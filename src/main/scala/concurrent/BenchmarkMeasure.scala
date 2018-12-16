package concurrent
import org.scalameter._
import org.scalameter.api.{Warmer, Executor, Aggregator, Gen, LocalExecutor, LoggingReporter, Measurer, Persistor}


/**
  * Goal of microbenchmark
  * 1. optimize a known and well-established bottleneck in an application
  * 2. compare several implementation alternatives, e.g. several algorithms
  * 3. verify that an optimization is an optimization at all
  * 4. have a performance regression test for a particular piece of code
  *
  *
  * */
object BenchmarkMeasure {
  def main(args: Array[String]): Unit = {
    simpleMeasure()
    warmUpMeasure()
    val rangeMeasure = new RangeBenchmark()
  }

  private def testCode(): Unit = {
    (0 until 10000000).map(math.pow(_, 5)).sum
  }

  def simpleMeasure(): Unit = {
    // Runs a block and returns how long it took in millisecons
    val time = measure {
      testCode()
    }
    println(s"simpleMeasure: $time")
  }
  /** During the warm-up period the JIT compilation would be complete
    * and the caches should be populated with commonly accessed data. */
  def warmUpMeasure(): Unit = {
    // Use the default ScalaMeter warmer
    val time = withWarmer(new Warmer.Default).measure {
      testCode()
    }
    println(s"warmUpMeasure: $time")
  }

  def configMeasure(): Unit = {
    val time = config(Key.exec.minWarmupRuns -> 30,
      Key.exec.maxWarmupRuns -> 60).withWarmer(new Warmer.Default).measure {
      testCode()
    }
    println(s"configMeasure: $time")
  }
}


import org.scalameter.picklers.Implicits._

/** ScalaMeter represents performance tests with the Bench abstract class */
class RangeBenchmark extends Bench[Double] {
  /* configuration */

  /** 4 test pipelines
    * 1. Defining tests with a DSL
    * 2. Executing tests with an executor
    * 3. Reporting test results with a reporter
    * 4. Persisting test results with a persistor */
  lazy val executor = LocalExecutor(  // LocalExecutor let us run in same JVM instance
    new Executor.Warmer.Default,
    Aggregator.min[Double],  // take the minimum running time of all the benchmarks run for each size
    measurer)
//  lazy val executor = SeparateJvmsExecutor(  // run on separateJVM
//    new Executor.Warmer.Default,
//    Aggregator.min,
//    new Measurer.Default
//  )
  lazy val measurer = new Measurer.Default   // fixed number of measurements for each size
  lazy val reporter = new LoggingReporter[Double]  // output results to the terminal
  lazy val persistor = Persistor.None  // no need now

  /* inputs */

  val sizes:Gen[Int] = Gen.range("size")(300000, 1500000, 300000)

  val ranges: Gen[Range] = for {
    size <- sizes
  } yield 0 until size

  /* tests */
  println("RangeBenchmark: ")
  performance of "Range" in {
    measure method "map" in {
      using(ranges) in {
        r => r.map(_ + 1)
      }
    }
  }
}
//class RegressionTest extends Bench.Regression {
//  def persistor = new SerializationPersistor
//  val sizes = Gen.range("size")(1000000, 5000000, 2000000)
//  val arrays = for (sz <- sizes) yield (0 until sz).toArray
//
//  performance of "Array" in {
//    measure method "foreach" in {
//      using(arrays) api.config (
//        exec.independentSamples -> 1
//        ) in { xs =>
//        var sum = 0
//        xs.foreach(x => sum += x)
//      }
//    }
//  }
//}
