package lectures.part4implicits

import scala.language.implicitConversions

object ImplicitsIntro extends App {

  // how does this compile?
  private val pari = "Daniel" -> "555"
  // we know operator acts as method
  // first operand is the instance whose method this is
  // and the other operand is the argument
  // there is not "->" arrow method in String, Int class or any
  private val intPair = 1 -> 2
  // hovering and cmd + clicking on "->"
  // is a method of an implicit class
  // implicit final class ArrowAssoc[A](private val self: A) extends AnyVal {...}
  // implicit keyword will allow some kind of magic
  // that will turn the 1st argument into an ArrayAssoc instance
  // and it will call the "->" arrow method on it
  // with the argument and turn it into tuple

  case class Person(name: String) {
    def greet: String = s"Hi, my name is $name!"
  }

  implicit private def fromStringToPerson(str: String): Person = Person(str)
  // implicit allows to call greet method on Person class
  println("Peter".greet) // println(fromStringToPerson("Peter").greet)
  /*
   * how does this work?
   * greet method doesn't exist for String class
   * and normally compiler wouldn't compile this code
   * but it doesn't give up yet
   * it looks for all implicit classes, objects, values, and methods
   * that can helps in the compilation
   * it looks for anything that can turn String into something
   * that has greet method
   *
   * it just so happens that we have a Person Type
   * that has a greet method
   * and an implicit conversion from str to Person with greet method
   *
   * println(fromStringToPerson("Peter").greet)
   *
   * Compiler assumes there is only one implicit that matches
   * if there is multiple, then it will not compile
   */

  /*
  class A {
    def greet: Int = 2
  }
  implicit def fromStringToA(str: String): A = new A
  */

  // implicit parameters
  private def increment(x: Int)(implicit amount: Int): Int =
    x + amount

  implicit val defaultAmount: Int = 10

  increment(2)
  // NOT default args
  // here the compiler will find the implicit value
  // from it's search scope
  // Example: Future has implicit ExecutionContext
}
