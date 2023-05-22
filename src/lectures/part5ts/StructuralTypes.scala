package lectures.part5ts

import scala.language.reflectiveCalls

object StructuralTypes extends App {

  // Type Refinements
  // Compile-Time Duck Typing

  // structural types
  private type JavaCloseable = java.io.Closeable

  private class HipsterCloseable {
    def close(): Unit = println("yeah yeah I'm closing")

    def closeSilently(): Unit = println("not making a sound")
  }

  // def closeQuietly(closeable: JavaCloseable OR HipsterCloseable) // ?!

  private type UnifiedCloseable = {
    def close(): Unit
  } // STRUCTURAL TYPE

  private def closeQuietly(unifiedCloseable: UnifiedCloseable): Unit =
    unifiedCloseable.close()

  closeQuietly(new JavaCloseable {
    override def close(): Unit = ???
  })
  closeQuietly(new HipsterCloseable)


  // TYPE REFINEMENTS


  private type AdvancedCloseable = JavaCloseable {
    def closeSilently(): Unit
  }

  private class AdvancedJavaCloseable extends JavaCloseable {
    override def close(): Unit = println("Java closes")

    def closeSilently(): Unit = println("Java closes silently")
  }

  private def closeShh(advancedCloseable: AdvancedCloseable): Unit =
    advancedCloseable.closeSilently()

  closeShh(new AdvancedJavaCloseable)
  // closeShh(new HipsterCloseable)

  // using structural types as standalone types
  def altClose(closeable: {
    def close(): Unit
  }): Unit = closeable.close()

  // type-checking = duck typing

  private type SoundMaker = {
    def makeSound(): Unit
  }

  private class Dog {
    def makeSound(): Unit = println("bark!")
  }

  private class Car {
    def makeSound(): Unit = println("vroom!")
  }

  private val dog: SoundMaker = new Dog
  private val car: SoundMaker = new Car
  // static duck typing
  // compiler is fine as long as the
  // types on the right hand side
  // confirm to the structure defined on the left hand side

  // duck test
  // if something looks like a duck
  // and swims like a duck
  // and fly's like a duck
  // then it can be treated as a duck
  // usually present in dynamic language

  // CAVEAT: based on reflection
  // big impact on performance

  // Exercise
  trait CBL[+T] {
    def head: T

    def tail: CBL[T]
  }

  private class Human {
    def head: Brain = new Brain
  }

  private class Brain {
    override def toString: String = "BRAINZ!"
  }

  def f[T](somethingWithAHead: {
    def head: T
  }): Unit = println(somethingWithAHead.head)

  /*
   * f is compatible with a CBL and with a Human? Yes.
   */

  private case object CBNil extends CBL[Nothing] {
    override def head: Nothing = ???

    override def tail: CBL[Nothing] = ???
  }

  private case class CBCons[T](override val head: T, override val tail: CBL[T]) extends CBL[T]

  f(CBCons(2, CBNil))
  f(new Human) // ?! T = Brain !!

  // 2.
  private object HeadEqualizer {
    private type Headable[T] = {def head: T}

    def ===[T](a: Headable[T], b: Headable[T]): Boolean =
      a.head == b.head
  }

  /*
   * f is compatible with a CBL and with a Human? Yes.
   */
  private val brainzList = CBCons(new Brain, CBNil)
  private val stringsList = CBCons("Brainz", CBNil)
  HeadEqualizer.===(brainzList, new Human)
  // problem:
  HeadEqualizer.===(new Human, stringsList) // not type safe
  // reason: structural equalization relies on reflection
  // compiler erases the type parameter "T"
}
