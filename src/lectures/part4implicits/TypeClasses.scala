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
  private object PartialUserSerializer extends HTMLSerializer[User] {
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
    def serialize[T](value: T)(implicit serializer: HTMLSerializer[T]): String =
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
  println(HTMLSerializer.serialize(42))

  /*
   * advantage
   *  - compiler will fetch the correct implicit type class instance
   *    and inject it for us
   */
  println(HTMLSerializer.serialize(john))
  // after apply returns implicit type class instance
  println(HTMLSerializer[User].serialize(john))
  /*
   * advantage
   *  - access to the entire Type Class interface
   */

  // part 3
  // type enrichment for type classes
  // will allow us to invoke type class pattern
  // for any type we have an trait
  implicit class HTMLEnrichment[T](value: T) {
    def toHTML(implicit serializer: HTMLSerializer[T]): String =
      serializer.serialize(value)
  }

  println(john.toHtml)
  // println(new HTMLEnrichment[User](john).toHTML(UserSerializer)
  // COOL!
  /*
   * Solver our earlier problems
   *  - extend to new types (it can wrap to any type)
   *  - different implementation for the same type / choose implementation
   *    - import
   *    - passing it explicitly
   *  - super expressive!
   */

  println(2.toHTML)
  println(john.toHTML(PartialUserSerializer))

  /*
   *  type class pattern
   *    - type class itself with all the functionality to expose
   *        --- HTMLSerializer[T] {...{
   *    - type class instances (some of which are implicit)
   *        --- UserSerializer, IntSerializer
   *    - conversion with implicit classes
   *        --- HTMLEnrichment
   */

  // context bounds
  private def htmlBoilerplate[T](content: T)(implicit serializer: HTMLSerializer[T]): String =
    s"<html><body> ${content.toHTML(serializer)}</body></html>"

  // this is context bound
  private def htmlSugar[T: HTMLSerializer](content: T): String = {
    var serializer = implicitly[HTMLSerializer[T]]
    // use serializer
    s"<html><body> ${content.toHTML}</body></html>"
  }


  // implicitly
  case class Permissions(mask: String)

  implicit val defaultPermissions: Permissions = Permissions("0744")

  // in some other part of the code
  // what is the implicit value for permissions
  private val standardPerms = implicitly[Permissions]
}
