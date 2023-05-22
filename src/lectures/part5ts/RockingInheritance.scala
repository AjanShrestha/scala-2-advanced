package lectures.part5ts

object RockingInheritance extends App {

  // convenience
  private trait Writer[T] {
    def write(value: T): Unit
  }

  private trait Closeable {
    def close(status: Int): Unit
  }

  private trait GenericStream[T] {
    // some methods
    def foreach(f: T => Unit): Unit
  }

  // whenever we don't know who exactly mixes in with our specific traits
  // we can mix them all in a specific type as a parameter to a method
  def processStream[T](stream: GenericStream[T] with Writer[T] with Closeable): Unit = {
    stream.foreach(println)
    stream.close(0)
  }

  // diamond problem
  trait Animal {
    def name: String
  }

  private trait Lion extends Animal {
    override def name: String = "lion"
  }

  private trait Tiger extends Animal {
    override def name: String = "tiger"
  }

  // trait Mutant extends Lion with Tiger
  private class Mutant extends Lion with Tiger
  // Why don't we have a conflict here for name method?

  private val m = new Mutant
  println(m.name)
  /*
   * Mutant extends Animal with { override def name: String = "lion" }
   * with { override def name: String = "tiger" }
   *
   * LAST OVERRIDE GETS PICKED
   */

  // the super problem + type linearization
  // usually super accesses a member or method of parent trait/class
  // super has a wider significance in Scala

  trait Cold {
    def print: Unit = println("cold")
  }

  private trait Green extends Cold {
    override def print: Unit = {
      println("green")
      super.print
    }
  }

  private trait Blue extends Cold {
    override def print: Unit = {
      println("blue")
      super.print
    }
  }

  private class Red {
    def print: Unit = println("red")
  }

  private class White extends Red with Green with Blue {
    override def print: Unit = {
      println("white")
      super.print
    }
  }

  private val color = new White
  color.print

  // Type Linearization
  // White = AnyRef with <Red> with <Cold> with <Green> with <Blue> with <White>
  // super.method -> right to left
  // White -> super
  // Blue -> super
  // Green -> super
  // Cold
}
