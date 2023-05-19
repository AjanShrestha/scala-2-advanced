package lectures.part3concurrency

import scala.collection.mutable
import scala.util.Random

object ThreadCommunication extends App {

  // Problem: Enforce a certain order of execution in Thread
  /*
   *  the producer-consumer problem
   *
   *  Scenario
   *  - a small container that wraps a single value
   *  - in parallel we have 2 threads running
   *  - producer whose sole purpose is setting a value in container
   *  - consumer whose sole purpose is to extract the value out of the container
   *  - problem
   *      - both are running in parallel and
   *      - don't know when each other has finished working
   *
   *    producer -> [ x ] -> consumer
   *
   *  solution
   *    - guarantee threads to run in a certain order
   *    - here consumer is forced to wait for producer to fill in
   */

  private class SimpleContainer {
    private var value: Int = 0

    def isEmpty: Boolean = value == 0

    def set(newValue: Int): Unit = value = newValue

    // consuming method
    def get: Int = {
      val result = value
      value = 0
      result
    }
  }

  private def naiveProdCons(): Unit = {
    val container = new SimpleContainer

    val consumer = new Thread(() => {
      println("[consumer] waiting...")
      while (container.isEmpty) {
        println("[consumer] actively waiting...")
      }
      println("[consumer] I have consumed " + container.get)
    })

    val producer = new Thread(() => {
      println("[producer] computing...")
      Thread.sleep(500)
      val value = 42
      println("[producer] I have produced, after long work, the value " + value)
      container.set(value)
    })

    consumer.start()
    producer.start()
  }

  // naiveProdCons()

  /*
   * Synchronized
   *  Entering a synchronized expression on an object locks the object:
   *
   *  val someObject = "hello"
   *  someObject.synchronized { // <- lock the object's monitor
   *    // code                 // <- any other thread trying to run this will block
   *  }                         // <- release the lock
   *  "monitor"
   *    - Data structure that is internally used by JVM
   *    - keeps tracks of which object is locked by which Thread
   *
   *  Only "AnyRefs" can have synchronized blocks.
   *
   * General Principles:
   *  - make no assumptions about who gets the lock first
   *  - keep locking to a minimum
   *  - maintain thread safety at ALL times in parallel applications
   */

  /*
   *  wait() and notify()
   *
   *  - wait() -ing on an object's monitor suspends you (the thread) indefinitely
   *
   *  Example:
   *    // thread 1
   *    val someObject = "hello"
   *    someObject.synchronized {   // <- lock the object's monitor
   *      // ... code part 1
   *      someObject.wait()         // <- release the lock and... wait (suspend at this point)
   *      // ... code part 2        // <- when allowed to proceed, lock the monitor again and continue
   *    }
   *
   *    // thread 2
   *    someObject.synchronized {   // <- lock the object's monitor
   *      // ... code part 1
   *      someObject.notify()       // <- signal ONE sleeping thread they may continue after acquiring lock
   *                                //    Which one? - no control. JVM & OS decides
   *      // ... more code
   *    }                           // <- but only after I'm done and unlock the monitor
   *
   *    Use notifyAll() to awaken ALL threads
   *
   *  ** Waiting and notifying only work in synchronized expressions.
   */

  private def smartProdCons(): Unit = {
    val container = new SimpleContainer

    val consumer = new Thread(() => {
      println("[consumer] waiting...")
      container.synchronized {
        container.wait()
      }

      // container must have some value
      println("[consumer] I have consumed " + container.get)
    })

    val producer = new Thread(() => {
      println("[producer] Hard at work...")
      Thread.sleep(500)
      val value = 42

      container.synchronized {
        println("[producer] I'm producing " + value)
        container.set(value)
        container.notify()
      }
    })

    consumer.start()
    producer.start()
  }

  // smartProdCons()

  /*
   * Case
   *  Instead of simple container we have a buffer
   *  - Producer can produce values (continuously)
   *  - Consumer can consume values (continuously)
   *
   *  producer -> [ ? ? ? ] -> consumer
   *
   *  Complication
   *  - both the producer and consumer may block each other
   *    - if the buffer if full, producer must block until
   *        consumer has finished extracting values from the buffer
   *    - vice-versa
   */

  private def prodConsLargeBuffer(): Unit = {
    val buffer: mutable.Queue[Int] = new mutable.Queue[Int]
    val capacity = 3
    val CONSUMER_IDLE_TIME = 500 // ms
    val PRODUCER_IDLE_TIME = 500 // ms

    val consumer = new Thread(() => {
      val random = new Random()

      while (true) {
        buffer.synchronized {
          if (buffer.isEmpty) {
            println("[consumer] buffer empty, waiting...")
            buffer.wait()
          }

          // there must be at least ONE value in the buffer
          val x = buffer.dequeue()
          println("[consumer] consumed " + x)

          // hey producer, there's empty space available, are you lazy?!
          buffer.notify()
        }
        Thread.sleep(random.nextInt(CONSUMER_IDLE_TIME))
      }
    })

    val producer = new Thread(() => {
      val random = new Random()
      var i = 0 // sequencer

      while (true) {
        buffer.synchronized {
          if (buffer.size == capacity) {
            println("[producer] buffer is full, waiting...")
            buffer.wait()
          }

          // there must be at least ONE EMPTY SPACE in the buffer
          println("[producer] producing " + i)
          buffer.enqueue(i)

          // hey consumer, new food for you!
          buffer.notify()
          i += 1
        }
        Thread.sleep(random.nextInt(PRODUCER_IDLE_TIME))
      }
    })

    consumer.start()
    producer.start()
  }

  // prodConsLargeBuffer()

  /*
   *  Prod-cons, Level 3
   *
   *  Case
   *    - limited buffer
   *    - multiple producers
   *    - multiple consumers
   *
   *  producer1 -> [ ? ? ? ] -> consumer1
   *  producer1 ----^     ^---- consumer2
   *
   */

  private class Consumer(
                          id: Int,
                          buffer: mutable.Queue[Int]
                        ) extends Thread {
    private final val CONSUMER_IDLE_TIME = 250

    override def run(): Unit = {
      val random = new Random()

      while (true) {
        buffer.synchronized {
          /*
           * producer produces value, two Cons are waiting
           * notifies ONE consumer, notifies on buffer
           * notifies the other consumer
           */
          while (buffer.isEmpty) {
            println(s"[consumer $id] buffer empty, waiting...")
            buffer.wait()
          }

          // there must be at least ONE value in the buffer
          val x = buffer.dequeue()
          println(s"[consumer $id] consumed " + x)

          buffer.notify()
          // buffer.notifyAll() // same as notify
        }
        Thread.sleep(random.nextInt(CONSUMER_IDLE_TIME))
      }
    }
  }

  private class Producer(
                          id: Int,
                          buffer: mutable.Queue[Int],
                          capacity: Int
                        ) extends Thread {
    private final val PRODUCER_IDLE_TIME = 500

    override def run(): Unit = {
      val random = new Random()
      var i = 0 // sequencer

      while (true) {
        buffer.synchronized {
          while (buffer.size == capacity) {
            println(s"[producer $id] buffer is full, waiting...")
            buffer.wait()
          }

          // there must be at least ONE EMPTY SPACE in the buffer
          println(s"[producer $id] producing " + i)
          buffer.enqueue(i)

          buffer.notify()
          // buffer.notifyAll() // same as notify
          i += 1
        }
        Thread.sleep(random.nextInt(PRODUCER_IDLE_TIME))
      }
    }
  }

  private def multiProdCons(nConsumers: Int, nProducers: Int): Unit = {
    val buffer: mutable.Queue[Int] = new mutable.Queue[Int]
    val capacity = 20

    (1 to nConsumers)
      .foreach(i => new Consumer(i, buffer).start())
    (1 to nProducers)
      .foreach(i => new Producer(i, buffer, capacity).start())
  }

  // multiProdCons(3, 6)

  // Exercises
  /*
   * 1. think of an example where notifyAll acts in a different way than notify?
   * 2. create a deadlock
   *      - threads are locked i.e. waiting for resource to free up
   * 3. create a livelock
   *      - threads yield execution to each other such that no body can continue
   *      - threads are active but cannot continue
   */

  // 1- notifyAll
  private def testNotifyAll(): Unit = {
    val bell = new Object

    (1 to 10).foreach(i => new Thread(() => {
      bell.synchronized {
        println(s"[thread $i] waiting...")
        bell.wait()
        println(s"[thread $i] hooray!")
      }
    }).start())

    new Thread(() => {
      Thread.sleep(2000)
      bell.synchronized {
        println("[announcer] Rock'n roll!")
        bell.notify()
        // bell.notifyAll()
      }
    }).start()
  }

  // testNotifyAll()

  // 2 - deadlock
  case class Friend(name: String) {
    def bow(other: Friend): Unit = {
      this.synchronized {
        println(s"$this: I am bowing to my friend $other")
        other.rise(this)
        println(s"$this: my friend $other has risen")
      }
    }

    def rise(other: Friend): Unit = {
      this.synchronized {
        println(s"$this: I am rising to my friend $other")
      }
    }

    // 3
    private var side = "right"

    private def switchSide(): Unit = {
      if (side == "right") side = "left"
      else side = "right"
    }

    def pass(other: Friend): Unit = {
      while (this.side == other.side) {
        println(s"$this: Oh, but please, $other, feel free to pass")
        switchSide()
        Thread.sleep(1000)
      }
    }
  }

  private val sam = Friend("Sam")
  private val pierre = Friend("Pierre")

  // new Thread(() => sam.bow(pierre)).start() // sam's lock,    |   then pierre's lock
  // new Thread(() => pierre.bow(sam)).start() // pierre's lock, |   then sam's lock

  // 3 - livelock
  new Thread(() => sam.pass(pierre)).start()
  new Thread(() => pierre.pass(sam)).start()
}
