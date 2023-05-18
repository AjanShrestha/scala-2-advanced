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

  // infix patterns
  private case class Or[A, B](a: A, b: B) // Either

  private val either = Or(2, "two")
  private val humanDescription = either match {
    // case Or(number, string) => s"$number is written as $string"
    case number Or string => s"$number is written as $string"
  }
  println(humanDescription)

  // decomposing sequences
  private val vararg = numbers match {
    case List(1, _*) => "starting with 1"
  }

  // Custom vararg match with unapplySeq
  private abstract class MyList[+A] {
    def head: A = ???

    def tail: MyList[A] = ???
  }

  private case object Empty extends MyList[Nothing]

  private case class Cons[+A](
                               override val head: A,
                               override val tail: MyList[A]
                             ) extends MyList[A]

  private object MyList {
    def unapplySeq[A](list: MyList[A]): Option[Seq[A]] =
      if (list == Empty) Some(Seq.empty)
      else unapplySeq(list.tail).map(list.head +: _)
  }

  private val myList: MyList[Int] = Cons(1, Cons(2, Cons(3, Empty)))
  private val decomposed = myList match {
    case MyList(1, 2, _*) => "starting with 1, 2"
    case _ => "something else"
  }
  println(decomposed)

  // custom return type for unapply
  // requires 2 methods implemented
  // isEmpty: Boolean, get: something
  private abstract class Wrapper[T] {
    def isEmpty: Boolean

    def get: T
  }

  private object PersonWrapper {
    def unapply(person: Person): Wrapper[String] = new Wrapper[String] {
      def isEmpty: Boolean = false

      def get: String = person.name
    }
  }

  println(bob match {
    case PersonWrapper(n) => s"This person's name is $n"
    case _ => "An alien"
  })
}
