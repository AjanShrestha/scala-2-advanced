package lectures.part3concurrency

import java.util.concurrent.Executors

object Intro extends App {

  /*
    interface Runnable {
      public void run()
    }
   */
  // JVM threads
  private val runnable = new Runnable {
    override def run(): Unit = println("Running in parallel")
  }
  private val aThread = new Thread(runnable)

  aThread.start() // gives the signal to the JVM to start a JVM thread
  // create a JVM thread =>(runs on top of) OS thread
  // runs on separate JVM thread than the main application
  runnable.run() // doesn't do anything in parallel!
  aThread.join() // blocks until aThread finishes running

  private val threadHello = new Thread(() =>
    (1 to 5).foreach(_ => println("hello")))
  private val threadGoodbye = new Thread(() =>
    (1 to 5).foreach(_ => println("goodbye")))
  threadHello.start()
  threadGoodbye.start()
  // different runs produce different results!

  // threads are expensive to start and kill
  // executors
  private val pool = Executors.newFixedThreadPool(10)
  pool.execute(() => println("something in the thread pool"))
  // executed by 1 of the n(10) threads managed by the Thread Pool
  // no need to worry about start and stop
  // Thread Pool manages the thread

  pool.execute(() => {
    Thread.sleep(1000)
    println("done after 1 second")
  })

  pool.execute(() => {
    Thread.sleep(1000)
    println("almost done")
    Thread.sleep(1000)
    println("done after 2 seconds")
  })

  // ThreadPool APIs
   pool.shutdown()
  // pool.execute(() => println("should not appear")) // throws an exception in the calling thread

  // pool.shutdownNow() // interrupts any running pool immediately
  println(pool.isShutdown) // true
}
