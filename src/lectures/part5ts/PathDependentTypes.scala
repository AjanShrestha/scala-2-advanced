package lectures.part5ts

object PathDependentTypes extends App {

  private class Outer {
    class Inner

    object InnerObject

    type InnerType

    def print(i: Inner): Unit = println(i)

    def printGeneral(i: Outer#Inner): Unit = println(i)
  }

  private def aMethod: Int = {
    class HelperClass
    type HelperType = String // requires aliases
    // ...
    2
  }

  // using inner
  // defined per-instance
  private val o = new Outer
  // private val inner = new Inner
  // private val inner = new Outer.Inner
  private val inner = new o.Inner // o.Inner is a TYPE
  private val oo = new Outer
  // private val otherInner: oo.Inner = new o.Inner

  o.print(inner)
  // oo.print(inner)

  // path-dependent types

  // all the inner type has a supertype
  // Outer#Inner
  o.printGeneral(inner)
  oo.printGeneral(inner)

  // As a general use case,
  // objects created or managed by a specific instance
  // of an outer type cannot be accidentally or purposely
  // mixed or interchanged with instances created by
  // another outer type, then path-dependent type is the way to go

  /*
   * Exercise
   * DB keyed by Int or String, but maybe others
   *
   * hints:
   *  use path-dependent types
   *  abstract type members and/or type aliases
   */

  private trait ItemLike {
    type Key
  }

  private trait Item[K] extends ItemLike {
    type Key = K
  }

  private trait IntItem extends Item[Int]

  private trait StringItem extends Item[String]

  def get[ItemType <: ItemLike](key: ItemType#Key): ItemType = ???

  get[IntItem](42) // ok
  get[StringItem]("home") // ok

  // get[IntItem]("scala") // not ok
}
