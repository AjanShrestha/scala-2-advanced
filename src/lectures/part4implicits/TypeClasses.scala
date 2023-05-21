package lectures.part4implicits

object TypeClasses extends App {

  // TypeClass is a trait that takes a type and
  // describes the operations that can be applied to that type

  trait HTMLWritable {
    def toHtml: String
  }

  // option 1 - case classes
  case class User(
                   name: String,
                   age: Int,
                   email: String
                 ) extends HTMLWritable {
    override def toHtml: String =
      s"<div>$name ($age yo) <a href=$email/> </div>"
  }

  private val john = User("John", 32, "john@rockthejvm.com")
  john.toHtml
  /*
   * disadvantages
   *  1. it only works for the types WE write
   *  2. ONE implementation out a quite a number
   *    - what about anonymous user?
   *    ...
   */

  // option 2 - pattern matching
  object HTMLSerializerPM {
    def serializeToHTML(value: Any) = value match {
      case User(n, a, e) =>
      // case java.util.Date =>
      case _ =>
    }
  }

  /*
   * disadvantages
   *  1. lost type safety
   *  2. need to modify the code every time
   *  3. still ONE implementation for each given type
   */

  // option 3
  trait HTMLSerializer[T] {
    def serialize(value: T): String
  }

  private object UserSerializer extends HTMLSerializer[User] {
    override def serialize(user: User): String =
      s"<div>${user.name} (${user.age} yo) <a href=${user.email}/> </div>"
  }

  println(UserSerializer.serialize(john))

  /*
   * advantages
   *  1. we can define serializer for other types
   *  2. we can define MULTIPLE serializers for a certain type
   *  3. gives type safety
   */

  // other type

  import java.util.Date

  object DateSerializer extends HTMLSerializer[Date] {
    override def serialize(date: Date): String =
      s"<div>${date.toString}</div>"
  }

  // multiple serializers
  object PartialUserSerializer extends HTMLSerializer[User] {
    override def serialize(user: User): String =
      s"<div>${user.name} </div>"
  }

  // TYPE CLASS - HTMLSerializer
  // A type class specifies a set of operations
  // that can be applied to a given type
  // Example: serialize to a type T
  // TYPE CLASS INSTANCES - UserSerializer, DateSerializer, PartialUserSerializer
  // All that extends the Type Class
  // must implement the operations

  // TYPE CLASS
  trait MyTypeClassTemplate[T] {
    def action(value: T): String
  }

  /*
   * Exercise: Equality
   */
  trait Equal[T] {
    def apply(a: T, b: T): Boolean
  }

  object NameEquality extends Equal[User] {
    override def apply(a: User, b: User): Boolean =
      a.name == b.name
  }

  object FullEquality extends Equal[User] {
    override def apply(a: User, b: User): Boolean =
      a.name == b.name && a.email == b.email
  }
}
