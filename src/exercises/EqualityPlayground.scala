package exercises

import lectures.part4implicits.TypeClasses.User

object EqualityPlayground extends App {

  /*
   * Exercise: Equality
   */
  trait Equal[T] {
    def apply(a: T, b: T): Boolean
  }

  implicit object NameEquality extends Equal[User] {
    override def apply(a: User, b: User): Boolean =
      a.name == b.name
  }

  object FullEquality extends Equal[User] {
    override def apply(a: User, b: User): Boolean =
      a.name == b.name && a.email == b.email
  }

  // Exercise
  /*
   * implement the TC pattern for the Equality tc
   */
  private object Equal {
    def apply[T](a: T, b: T)(implicit equalizer: Equal[T]): Boolean =
      equalizer.apply(a, b)
  }

  private val john = User("John", 32, "john@rockthejvm.com")
  private val anotherJohn = User("John", 45, "aniotherJohn@rtjvm.com")
  println(Equal(john, anotherJohn))
  // AD-HOC polymorphism
  // if 2 distinct or unrelated types have equalizers implemented
  // then we call the Equal as polymorphism
}
