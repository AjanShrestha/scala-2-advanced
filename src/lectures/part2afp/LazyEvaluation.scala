package lectures.part2afp

object LazyEvaluation extends App {

  // lazy DELAYS the evaluation of values
  // evaluated once only when they are used for the first time
  lazy private val x: Int = {
    println("hello")
    42
  }
  println(x)
  println(x)

  // examples of implications:
  private def sideEffectCondition: Boolean = {
    println("Boo")
    true
  }

  private def simpleCondition: Boolean = false

  private lazy val lazyCondition = sideEffectCondition
  println(if (simpleCondition && lazyCondition) "yes" else "no")
  // but isn't this a short-circuit?
  // println(if (simpleCondition && sideEffectCondition) "yes" else "no")

  // in conjunction with call by name
  private def byNameMethod(n: => Int): Int = {
    // n + n + n + 1
    // CALL BY NEED
    lazy val t = n // only evaluated once
    t + t + t + 1
  }

  private def retrieveMagicValue: Int = {
    // side effect or a long computation
    println("waiting")
    Thread.sleep(1000)
    42
  }

  println(byNameMethod(retrieveMagicValue))
  // computed 3 times
  // common mistake
  // use lazy vals

  // filtering with lazy vals
  private def lessThan30(i: Int): Boolean = {
    println(s"$i is less than 30?")
    i < 30
  }

  private def greaterThan20(i: Int): Boolean = {
    println(s"$i is greater than 20?")
    i > 20
  }

  private val numbers = List(1, 25, 40, 5, 23)
  private val lt30 = numbers.filter(lessThan30) // List(1, 25, 3, 23)
  private val gt20 = lt30.filter(greaterThan20)
  println(gt20)

  // withFilter lazy vals under the hood
  private val lt30Lazy = numbers.withFilter(lessThan30)
  private val gt20Lazy = lt30Lazy.withFilter(greaterThan20)
  println
  gt20Lazy.foreach(println)

  // for-comprehensions use withFilter with guards
  for {
    a <- List(1, 2, 3) if a % 2 == 0 // use lazy vals
  } yield a + 1
  // List(1, 2, 3).withFilter(_ % 2 == 0).map(_ + 1) // List[Int]

  /*
    Exercise:
      implement a lazily evaluated, singly linked STREAM of elements.
        - STREAM -> special kind of collection
        - head is always evaluated and always available
        - tail is always lazily evaluated and always evaluated on demand

      naturals = MyStream.from(1)(x => x + 1) = stream of natural numbers (potentially infinite!)
      naturals.take(100).foreach(println) // lazily evaluated stream of the first 100 naturals (finite stream)
      naturals.foreach(println) // will crash - infinite!
      naturals.map(_ * 2) // stream of all even numbers (potentially infinite)
   */

  private abstract class MyStream[+A] {
    def isEmpty: Boolean

    def head: A

    def tail: MyStream[A]

    def #::[B >: A](element: B): MyStream[B] // prepend operator

    def ++[B >: A](anotherStream: MyStream[B]): MyStream[B] // concatenates two streams

    def foreach(f: A => Unit): Unit

    def map[B](f: A => B): MyStream[B]

    def flatMap[B](f: A => MyStream[B]): MyStream[B]

    def filter(predicate: A => Boolean): MyStream[A]

    def take(n: Int): MyStream[A] // takes the first n elements out of this stream
    // and returns the stream of those n numbers - Finite Stream

    def takeAsList(n: Int): List[A]
  }

  object MyStream {
    // generate stream based on start element and generator func
    // generator generate next value based on the prev value known in the stream
    def from[A](start: A)(generator: A => A): MyStream[A] = ???
  }
}
