package lectures.part1as

import scala.util.Try

object DarkSugars extends App {

  // syntax sugar #1 methods with single param
  private def singleArgMethod(arg: Int): String = s"$arg little ducks..."

  private val description = singleArgMethod {
    // write some complex code
    42
  }

  private val aTryInstance = Try { // java's try {...}
    throw new RuntimeException
  }

  List(1, 2, 3).map { x =>
    x + 1
  }

  // syntax sugar #2: single abstract method
  // Instances/trait with a single method can be reduced to lambdas
  private trait Action {
    def act(x: Int): Int
  }

  private val anInstance: Action = new Action {
    override def act(x: Int): Int = x + 1
  }

  private val aFunkyInstance: Action = (x: Int) => x + 1 // magic

  // example: Runnable
  // In Java
  private val aThread = new Thread(new Runnable {
    override def run(): Unit = println("Hello, Java")
  })

  // In Scala
  private val aSweeterThread = new Thread(() => println("Sweet, Scala!"))

  // This pattern also works for some classes
  // that have some members/methods implemented
  // but one method unimplemented
  private abstract class AnAbstractType {
    def implemented: Int = 23

    def f(a: Int): Unit
  }

  private val anAbstractInstance: AnAbstractType =
    (a: Int) => println("sweet")

  // syntax sugar #3: the :: and #:: methods are special
  private val prependedList = 2 :: List(3, 4)
  // 2.::(List(3,4,)) Infix methods
  // there is no :: method on Int
  // Compiler writes as
  // List(3,4).::(2)
  // ?!

  // scala spec: last char decides associativity of method
  // ends with : it's right associative
  // if not then it's left associative
  1 :: 2 :: 3 :: List(4, 5)
  List(4, 5).::(3).::(2).::(1) // equivalent

  private class MyStream[T] {
    def -->:(value: T): MyStream[T] = this // actual implementation here
  }

  private val myStream = 1 -->: 2 -->: 3 -->: new MyStream[Int]

  // syntax sugar #4: multi-word method naming
  private class TeenGirl(name: String) {
    def `and then said`(gossip: String): Unit =
      println(s"$name said $gossip")
  }

  private val lilly = new TeenGirl(name = "Lilly")
  lilly `and then said` "Scala is so sweet!"

  // syntax sugar #5: infix types (Generics)
  private class Composite[A, B]

  // private val composite: Composite[Int, String] = ???
  private val composite: Int Composite String = ???

  private class -->[A, B]

  private val towards: Int --> String = ???

  // syntax sugar #6: update() is very special, much like apply
  private val anArray = Array(1, 2, 3)
  anArray(2) = 7 // rewritten to anArray.update(2, 7)
  // used in mutable collections
  // remember apply() AND update()

  // syntax sugar #7: setters for mutable containers
  private class Mutable {
    private var internalMember: Int = 0 // private for OO encapsulation

    def member: Int = internalMember // "getter"

    def member_=(value: Int): Unit =
      internalMember = value // "setter"
  }

  private val aMutableContainer = new Mutable
  aMutableContainer.member = 42 // rewritten as aMutableContainer.member_=(42)
}
