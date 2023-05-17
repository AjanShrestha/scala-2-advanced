package lectures.part1as

import scala.annotation.tailrec

object Recap extends App {

  private val aCondition: Boolean = false
  private val aConditionedVal = if (aCondition) 42 else 65
  // instructions vs expressions

  // compiler infers types for us
  private val aCodeBlock = {
    if (aCondition) 54
    56
  }

  // Unit = void
  private val theUnit = println("Hello, Scala")

  // functions
  private def aFunction(x: Int): Int = x + 1

  // recursion: stack and tail
  @tailrec
  private def factorial(n: Int, accumulator: Int): Int =
    if (n <= 0) accumulator
    else factorial(n - 1, n * accumulator)

  // object-oriented programming
  private class Animal

  private class Dog extends Animal

  private val aDog: Animal = new Dog // subtyping polymorphism

  private trait Carnivore {
    def eat(a: Animal): Unit
  }

  private class Crocodile extends Animal with Carnivore {
    override def eat(a: Animal): Unit = println("crunch!")
  }

  // method notation
  private val aCroc = new Crocodile
  aCroc.eat(aDog)
  aCroc eat aDog // natural language

  // anonymous classes
  private val aCarnivore = new Carnivore {
    override def eat(a: Animal): Unit = println("roar!")
  }

  // generics
  private abstract class MyList[+A] // variance and variance problems in THIS course

  // singleton and companions
  object MyList

  // case classes
  case class Person(name: String, age: Int)

  // exceptions and try/catch/finally
  private val throwsException = throw new RuntimeException // type: Nothing
  private val aPotentialFailure = try {
    throw new RuntimeException()
  } catch {
    case e: Exception => "I caught an exception"
  } finally {
    println("some logs")
  }

  // packaging and imports

  // functional programming
  private val incrementer = new Function1[Int, Int] {
    override def apply(v1: Int): Int = v1 + 1
  }

  incrementer(1)

  private val anonymousIncrementer = (x: Int) => x + 1
  List(1, 2, 3).map(anonymousIncrementer) // HOF
  // map, flatMap, filter

  // for-comprehension
  private val pairs = for {
    num <- List(1, 2, 3) // if condition
    char <- List('a', 'b', 'c')
  } yield num + "-" + char

  // Scala collections: Seq, Arrays, Lists, Vectors, Maps, Tuples
  private val aMap = Map(
    "Daniel" -> 789,
    "Jess" -> 555
  )

  // "collections": Options, Try // Abstract computations
  private val anOption = Some(2)

  // pattern matching
  private val x = 2
  private val order = x match {
    case 1 => "first"
    case 2 => "second"
    case 3 => "third"
    case _ => x + "th"
  }

  private val bob = Person("Bob", 22)
  private val greeting = bob match {
    case Person(n, _) => s"Hi, my name is $n"
  }

  // all the patterns
}
