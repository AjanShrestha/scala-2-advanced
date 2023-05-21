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
    def serializeToHTML(value: Any): Unit = value match {
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

  implicit private object UserSerializer extends HTMLSerializer[User] {
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


  // part 2
  // Implicit Type class instances
  // by implicit values and parameters
  private object HTMLSerializer {
    def serializer[T](value: T)(implicit serializer: HTMLSerializer[T]): String =
      serializer.serialize(value)

    // an even better design
    // makes the compiler surface out the implicit value
    def apply[T](implicit serializer: HTMLSerializer[T]): HTMLSerializer[T] =
      serializer
  }

  // private object IntSerializer extends HTMLSerializer[Int] {
  implicit private object IntSerializer extends HTMLSerializer[Int] {
    override def serialize(value: Int): String =
      s"<div style='color=blue;'>$value</div>"
  }

  // println(HTMLSerializer.serializer(42)(IntSerializer))
  println(HTMLSerializer.serializer(42))

  /*
   * advantage
   *  - compiler will fetch the correct implicit type class instance
   *    and inject it for us
   */
  println(HTMLSerializer.serializer(john))
  // after apply returns implicit type class instance
  println(HTMLSerializer[User].serialize(john))
  /*
   * advantage
   *  - access to the entire Type Class interface
   */
}
