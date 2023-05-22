package lectures.part4implicits

import java.{util => ju}
import scala.language.implicitConversions

object ScalaJavaConversions extends App {

  // deprecated
  // import collection.JavaConverters

  import scala.jdk.CollectionConverters._

  private val javaSet: ju.Set[Int] = new ju.HashSet[Int]()
  (1 to 5).foreach(javaSet.add)
  println(javaSet)

  private val scalaSet = javaSet.asScala

  /*
   * Iterator
   * Iterable
   * ju.List - collection.mutable.Buffer
   * ju.Set - collection.mutable.Set
   * ju.Map - collection.mutable.Map
   */

  import collection.mutable._

  private val numbersBuffer = ArrayBuffer[Int](1, 2, 3)
  private val juNumbersBuffer = numbersBuffer.asJava
  // reference check
  println(juNumbersBuffer.asScala eq numbersBuffer)
  // not all conversion are equal
  private val numbers = List(1, 2, 3) // Mutable
  private val juNumbers = numbers.asJava // Immutable
  private val backToScala = juNumbers.asScala
  println(backToScala eq numbers) //  false
  println(backToScala == numbers) // true

  // juNumbers.add(7)

  // Exercise
  /*
   * Create a Scala-Java Optional-Option
   *  .asScala
   */
  class ToScala[T](value: => T) {
    def asScala: T = value
  }

  implicit def asScalaOptional[T](o: ju.Optional[T]): ToScala[Option[T]] =
    new ToScala[Option[T]](
      if (o.isPresent) Some(o.get)
      else None
    )

  private val juOptional: ju.Optional[Int] = ju.Optional.of(2)
  private val scalaOption = juOptional.asScala
  println(scalaOption)
}
