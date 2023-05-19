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

  // aThread.start() // gives the signal to the JVM to start a JVM thread
  // create a JVM thread =>(runs on top of) OS thread
  // runs on separate JVM thread than the main application
  runnable.run() // doesn't do anything in parallel!
  // aThread.join() // blocks until aThread finishes running

  private val threadHello = new Thread(() =>
    (1 to 5).foreach(_ => println("hello")))
  private val threadGoodbye = new Thread(() =>
    (1 to 5).foreach(_ => println("goodbye")))
  // threadHello.start()
  // threadGoodbye.start()
  // different runs produce different results!

  // threads are expensive to start and kill
  // executors
  private val pool = Executors.newFixedThreadPool(10)
  // pool.execute(() => println("something in the thread pool"))
  // executed by 1 of the n(10) threads managed by the Thread Pool
  // no need to worry about start and stop
  // Thread Pool manages the thread

  //  pool.execute(() => {
  //    Thread.sleep(1000)
  //    println("done after 1 second")
  //  })
  //
  //  pool.execute(() => {
  //    Thread.sleep(1000)
  //    println("almost done")
  //    Thread.sleep(1000)
  //    println("done after 2 seconds")
  //  })

  // ThreadPool APIs
  // pool.shutdown()
  // pool.execute(() => println("should not appear")) // throws an exception in the calling thread

  // pool.shutdownNow() // interrupts any running pool immediately
  // println(pool.isShutdown) // true

  // concurrency problem in JVM
  private def runInParallel: Unit = {
    var x = 0

    val thread1 = new Thread(() => {
      x = 1
    })
    val thread2 = new Thread(() => {
      x = 2
    })
    thread1.start()
    thread2.start()
    println(x)
  }

  // for (_ <- 1 to 100) runInParallel
  // race condition

  private class BankAccount(var amount: Int) {
    override def toString: String = "" + amount
  }

  private def buy(account: BankAccount, thing: String, price: Int): Unit = {
    account.amount -= price // account.amount = account.amount - price
    //    println("I've bought " + thing)
    //    println("my account is now " + account)
  }

  //  for (_ <- 1 to 10000) {
  //    val account = new BankAccount(50000)
  //    val thread1 = new Thread(() => buy(account, "shoes", 3000))
  //    val thread2 = new Thread(() => buy(account, "iPhone12", 4000))
  //
  //    thread1.start()
  //    thread2.start()
  //    Thread.sleep(10)
  //    if (account.amount != 43000) println("AHA: " + account.amount)
  //    // println()
  //  }

  /*
    thread1 (shoes): 50000
      - account = 50000 - 3000 = 47000
    thread2 (iPhone12): 50000
      - account = 50000 - 4000 = 46000 // overwrites the memory of account.amount
   */
  // race condition is bad

  // option #1: use synchronized()
  // powerful and more used one
  // allows to put in more expression in block
  // control over what is put in
  private def buySafe(account: BankAccount, thing: String, price: Int): Unit = {
    account.synchronized {
      // no two threads can evaluate this at the same time
      account.amount -= price
      println("I've bought " + thing)
      println("my account is now " + account)
    }
  }

  // option #2: use @volatile

  /*
   * 1. Construct 50 "inception" threads
   *      Thread1 -> thread2 -> thread3 -> ...
   *      println("hello from thread #3")
   *      in REVERSE ORDER
   */

  private def inception(n: Int): Unit =
    if (n == 0) return
    else {
      val thread = new Thread(() => println(s"hello from thread #$n"))
      inception(n - 1)
      thread.start()
    }

  // inception(50)

  private def inceptionThread(maxThreads: Int, i: Int = 1): Thread = {
    new Thread(() => {
      if (i < maxThreads) {
        val newThread = inceptionThread(maxThreads, i + 1)
        newThread.start()
        newThread.join()
      }
      println(s"Hello from thread $i")
    })
  }

  // inceptionThread(50).start()

  /*
   * 2.
   */
  var x = 0
  private val threads = (1 to 100).map(_ => new Thread(() => x += 1))
  threads.foreach(_.start())
  // 1. What is the biggest value possible for x? // 100 -> When run in sequential
  // 2. What is the SMALLEST value possible for x? // 1
  /*
   * thread1: x = 0
   * thread2: x = 0
   * ...
   * thread100: x = 0
   *
   * for all threads: x = 1 and write it back to x
   */
  // threads.foreach(_.join())
  // println(x)

  /*
   * 3. sleep fallacy
   */
  private var message = ""
  private val awesomeThread = new Thread(() => {
    Thread.sleep(1000)
    message = "Scala is awesome"
  })
  message = "Scala sucks"
  awesomeThread.start()
  Thread.sleep(2000)
  // Thread.sleep(200)
  awesomeThread.join() // wait for the awesome thread to join
  // on join it allows awesomeThread to finish
  println(message)
  // What's the value of message? // Scala is awesome (almost always)
  // Is it guaranteed? NO!
  // Why? Why not?
  /*
   * (main thread)
   *  message = "Scala sucks"
   *  awesomeThread.start() (
   *    sleep() - relieves execution
   *  )
   *  (awesome thread)
   *    sleep() - relieves execution
   *  (OS gives the CPU to some important thread - takes CPU for more than 2 seconds)
   *  (OS gives the CPU to the MAIN thread)
   *    println("Scala sucks") -> Issue
   *  (OS gives the CPU to awesome thread)
   *    message = "Scala is awesome"
   */
  // sleep guarantees that the execution will be halted
  // for at least that moment of period
  // it does not guarantee the execution will begin immediately after the pause period
  // it relieves(yield) the execution to OS at least for that millisecond
  // sleeping doesn't guarantee the order of execution

  // How do we fix this?
  // synchronizing does NOT work
  //  coz it is useful only for concurrent modification
  //  i.e. 2 threads are trying to modify/access the same resource at the same time
  // here it is a sequential problem
  //  solution is to have threads join
}
