package lectures.part3concurrency

import java.util.concurrent.ForkJoinPool
import java.util.concurrent.atomic.AtomicReference

// import scala.collection.parallel.CollectionConverters._

object ParallelUtils extends App {

  // 1 - parallel collections
  // par method on a serial
  // If you’re using Scala 2.13+ and want to use
  // Scala’s parallel collections, you’ll have to import
  // a separate module, as described here
  // https://github.com/scala/scala-parallel-collections
  // private val parList = List(1, 2, 3).par

  // instantiate a parallel version of our collection by hand
  // private val aParVector = ParVector[Int](1, 2, 3)

  /*
   * Seq
   * Vector
   * Array
   * Map - Hash, Trie
   * Set - Hash, Trie
   * ...
   */

  // Parallelizing collections leads to increased perf
  private def measure[T](operation: => T): Long = {
    val time = System.currentTimeMillis()
    operation
    System.currentTimeMillis() - time
  }

  private val list = (1 to 10000000).toList
  private val serialTime = measure {
    list.map(_ + 1)
  }
  println("serial time " + serialTime)
  /*
  private val parallelTime = measure {
    list.par.map(_ + 1)
  }
  println("parallel time " + parallelTime)
  */

  // lesson
  // small vs big collections
  // it is expensive to start and stop threads
  // therefore it is cheaper to do serial computation
  // than parallel for smaller collections

  /*
   * Parallel collections operate on
   *  - Map-reduce model
   *    - split the elements into chunks
   *      - processed independently by a single thread
   *      - Splitter (Scala Parallel library)
   *    - operation: then actual operation takes on each of those chunks
   *      - every chunk is processed by a separate thread
   *    - recombine - Combiner
   */

  // BE CAREFUL with reduce, fold operation for parallel collections
  // fold, reduce with non-associate operators
  println(List(1, 2, 3).reduce(_ - _))
  // println(List(1,2,3).par.reduce(_ - _))

  // synchronization
  // may be required on the results
  /*
   private var sum = 0
   List(1, 2, 3).par.foreach(sum += _)
   println(sum) // should be 6
   // race conditions!
   */

  // configuring parallel collections
  // tasksupport: internal member of parallel collection
  /*
    aParVector.tasksupport =
      // Thread manager
      new ForkJoinTaskSupport(new ForkJoinPool(2))
    */

  /*
   * alternatives
   *  - ThreadPoolTaskSupport - deprecated
   *  - ExecutionContextTaskSupport(EC)
   */

  // Custom
  /*
  aParVector.tasksupport = new TaskSupport {
    // schedules a thread to run in parallel
    override def execute[R, Tp](fjtask: Task[R, Tp]): () => R = ???

    // schedules a thread to run in parallel
    // blocks until a result is available
    // i.e. wait for the thread to join
    override def executeAndWaitResult[R, Tp](task: Task[R, Tp]): R = ???

    // number of CPU cores to run
    override def parallelismLevel: Int = ???

    // actual manager that manages thread
    override val environment: AnyRef = ???
  }
  */

  // 2- atomic ops and references
  /*
   * Atomic operations
   *  - operation that cannot be divided
   *  - either it runs fully or not at all
   *  - in multi-threaded context, it cannot be intercepted
   *      by another thread
   */
  private val atomic = new AtomicReference[Int](2)
  private val currentValue = atomic.get() // thread-safe read
  atomic.set(4) // thread-safe write
  atomic.getAndSet(5) // thread-safe combo

  // if (x == 42) x = 45
  atomic.compareAndSet(38, 56)
  // if the value is 38, then set to 56
  // shallow equality
  // reference equality

  atomic.updateAndGet(_ + 1) // thread-safe function run
  atomic.getAndUpdate(_ + 1)

  atomic.accumulateAndGet(12, _ + _) // thread-safe accumulation
  atomic.getAndAccumulate(12, _ + _)
}
