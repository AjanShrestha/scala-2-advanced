package lectures.part1as

object AdvancedPatternMatching extends App {

  private val numbers = List(1)
  private val description = numbers match {
    case head :: Nil => println(s"the only element is $head.")
    case _ => "?!"
  }

  /*
    - constants
    - wildcards
    - case classes
    - tuples
    - some special magic like above
   */

  private class Person(val name: String, val age: Int)

  // Even though Person is not a case class
  // we can pattern match using a singleton object
  // add the unapply method and send a param of the class type
  // return is Option with the type you would want to match
  // could be single or a tuple of values
  // return the values of the return type wrapped in Some
  private object Person {
    def unapply(person: Person): Option[(String, Int)] = {
      if (person.age < 21) None // Poor API Design
      else Some(person.name, person.age)
    }

    // overloading for better PM
    def unapply(age: Int): Option[String] =
      Some(if (age < 21) "minor" else "major")
  }

  private val bob = new Person(name = "Bob", age = 25)
  private val greeting = bob match {
    case Person(n, a) => s"Hi, my name is $n and I ma $a yo."
  }
  println(greeting)

  private val legalStatus = bob.age match {
    case Person(status) => s"My legal status is $status"
  }
  println(legalStatus)

  /*
    Exercise.
   */
  /*
  private object even {
    def unapply(arg: Int): Option[Boolean] =
      if (arg % 2 == 0) Some(true)
      else None
  }

  private object singleDigit {
    def unapply(arg: Int): Option[Boolean] =
      if (arg > -10 && arg < 10) Some(true)
      else None
  }
  */

  // Language supported cleaner way to write the code
  private object even {
    def unapply(arg: Int): Boolean = arg % 2 == 0
  }

  private object singleDigit {
    def unapply(arg: Int): Boolean = arg > -10 && arg < 10
  }

  private val n: Int = 8
  private val mathProperty = n match {
    case singleDigit() => "single digit"
    case even() => "an even number"
    case _ => "no property"
  }
  println(mathProperty)

  // Quick way to write test for PM
  // is to define singleton object with unapply
  // which return Boolean
  // advantage: reusable
  // disadvantage: lots of condition then gets verbose
}
