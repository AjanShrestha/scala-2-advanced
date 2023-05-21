package lectures.part4implicits

import java.util.Date

object JSONSerialization extends App {

  /*
   * Users, posts, feeds
   * Serialize to JSON
   */
  case class User(name: String, age: Int, email: String)

  case class Post(content: String, createdAt: Date)

  case class Feed(user: User, posts: List[Post])

  /*
   * Steps
   *  1. intermediate data types: Int, String, List, Date
   *      JSON serializable data types
   *  2. type classes for conversion to intermediate data types
   *  3. serialize to JSON
   */

  sealed trait JSONValue { // intermediate data type
    def stringify: String
  }

  private final case class JSONString(value: String) extends JSONValue {
    override def stringify: String =
      "\"" + value + "\""
  }

  private final case class JSONNumber(value: Int) extends JSONValue {
    override def stringify: String =
      value.toString
  }

  private final case class JSONArray(values: List[JSONValue]) extends JSONValue {
    override def stringify: String =
      values.map(_.stringify).mkString("[", ",", "]")
  }

  private final case class JSONObject(values: Map[String, JSONValue]) extends JSONValue {
    /*
     * {
     *  "name": "John",
     *  "age": 22,
     *  "friends": [ ... ],
     *  "latestPosts": {
     *    "content": "Scala Rocks",
     *    "date": ...
     *  }
     * }
     */
    override def stringify: String =
      values.map {
        case (key, value) => "\"" + key + "\":" + value.stringify
      }.mkString("{", ",", "}")
  }

  private val data = JSONObject(Map(
    "user" -> JSONString("Daniel"),
    "posts" -> JSONArray(List(
      JSONString("Scala Rocks!"),
      JSONNumber(453)
    ))
  ))
  println(data.stringify)

  // type class
  /*
   * 3 fundamental requirements
   *  1. type class
   *  2. type class instances (implicit)
   *  3. pimp library to use type class instances
   */

  // 2.1 - type class
  trait JSONConvertor[T] {
    def convert(value: T): JSONValue
  }

  // 2.3 - conversion (pimping)
  implicit class JSONOps[T](value: T) {
    def toJSON(implicit converter: JSONConvertor[T]): JSONValue =
      converter.convert(value)
  }

  // 2.2 - type class instances
  implicit object StringConverter extends JSONConvertor[String] {
    override def convert(value: String): JSONValue =
      JSONString(value)
  }

  implicit object IntConverter extends JSONConvertor[Int] {
    override def convert(value: Int): JSONValue =
      JSONNumber(value)
  }

  // custom data types
  implicit object UserConverter extends JSONConvertor[User] {
    override def convert(user: User): JSONValue =
      JSONObject(Map(
        "name" -> JSONString(user.name),
        "age" -> JSONNumber(user.age),
        "email" -> JSONString(user.email)
      ))
  }

  implicit object PostConverter extends JSONConvertor[Post] {
    override def convert(post: Post): JSONValue =
      JSONObject(Map(
        "content" -> JSONString(post.content),
        "created" -> JSONString(post.createdAt.toString)
      ))
  }

  implicit object FeedConverter extends JSONConvertor[Feed] {
    override def convert(feed: Feed): JSONValue =
      JSONObject(Map(
        // "user" -> UserConverter.convert(feed.user),
        "user" -> feed.user.toJSON,
        // "posts" -> JSONArray(feed.posts.map(PostConverter.convert)) // TODO
        "posts" -> JSONArray(feed.posts.map(_.toJSON))
      ))
  }

  // call stringify on result
  private val now = new Date(System.currentTimeMillis())
  private val john = User("John", 34, "john@rockthejvm.com")
  private val feed = Feed(john, List(
    Post("hello", now),
    Post("look at this cute puppy", now)
  ))
  println(feed.toJSON)
  println(feed.toJSON.stringify)
}
