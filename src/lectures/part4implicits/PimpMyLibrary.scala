package lectures.part4implicits

import scala.annotation.tailrec
import scala.language.implicitConversions

object PimpMyLibrary extends App {

  // 2.isPrime // How?
  // enrich existing library
  // must only take one argument
  implicit class RichInt(val value: Int) extends AnyVal {
    def isEven: Boolean = value % 2 == 0

    def sqrt: Double = Math.sqrt(value)

    def times(function: () => Unit): Unit = {
      @tailrec
      def timesAux(n: Int): Unit =
        if (n <= 0) ()
        else {
          function()
          timesAux(n - 1)
        }

      timesAux(value)
    }

    def *[T](list: List[T]): List[T] = {
      def concatenate(n: Int): List[T] = {
        if (n <= 0) List()
        else concatenate(n - 1) ++ list
      }

      concatenate(value)
    }
  }

  implicit class RicherInt(richInt: RichInt) {
    def isOdd: Boolean = richInt.value % 2 != 0
  }

  new RichInt(42).sqrt

  println(42.isEven) // new RichInt(42).isEven

  // type enrichment = pimping

  1 to 10

  import scala.concurrent.duration._

  3.seconds

  // compiler doesn't do multiple implicit searches.
  // 42.isOdd

  // Exercise
  /*
   * Enrich the String class
   *  - asInt
   *  - encrypt (Caesar's Cipher
   *     John -> Lnjp
   *
   * Keep enriching the Int class
   *  - times(function)
   *    3.times(() => ...)
   *  - *
   *    3 * List(1,2) => List(1,2,1,2,1,2)
   */

  implicit class RichString(val string: String) extends AnyVal {
    def asInt: Int = Integer.valueOf(string)

    def encrypt(cipherDistance: Int): String =
      string.map(c => (c + cipherDistance).asInstanceOf[Char])
  }

  println("3".asInt)
  println("John".encrypt(2))

  3.times(() => println("Scala Rocks!"))
  println(4 * List(1, 2))

  // "3" / 4
  // Just like JS -> Please don't
  implicit def stringToInt(string: String): Int =
    Integer.valueOf(string)

  println("6" / 2) // stringToInt("6") / 2

  // equivalent: implicit class RichAltInt(value: Int)
  class RichAltInt(value: Int)

  implicit def enrich(value: Int): RichAltInt = new RichAltInt(value)

  // danger zone
  // implicit methods are DISCOURAGED!!
  // tough to debug
  // tend to be part of library packages
  implicit def intToBoolean(i: Int): Boolean = i == 1

  /*
   *  if (n) do something
   *  else do something else
   */

  private val aConditionedValue = if (3) "OK" else "Something wrong"
  println(aConditionedValue)

  /*
   * Tips:
   *  - keep type enrichment to implicit classes and type classes
   *  - avoid implicit defs as much as possible
   *  - package implicits clearly, bring into scope only what you need
   *  - IF you need conversions, make them specific
   */
}
