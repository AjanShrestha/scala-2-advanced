package lectures.part4implicits

object OrganizingImplicits extends App {

  // Example: Sorting collections
  // trait Ordering that takes implicit parameters
  println(List(1, 4, 5, 3, 2).sorted)
  // sorted takes implicit Ordering value

  // Where does scala look for that?
  // In a package -> scala.Predef
  // It is automatically imported

  implicit val reverseOrdering: Ordering[Int] =
    Ordering.fromLessThan(_ > _)
  println(List(1, 4, 5, 3, 2).sorted)
  // here the reverseOrdering will take precedence
  // over the ordering value defined in Predef

  /*
  implicit val normalOrdering: Ordering[Int] =
    Ordering.fromLessThan(_ < _)
  println(List(1, 4, 5, 3, 2).sorted)
  */
  // issue as compiler is confused with second implicit ordering

  /*
   * Potential Implicits (used as implicit parameters):
   *  - val/var
   *  - object
   *  - accessor methods = defs with no parentheses
   */

  // Exercise
  // Implicit Sorting for Person
  case class Person(name: String, age: Int)

  val person = List(
    Person("Steve", 30),
    Person("Amy", 22),
    Person("John", 66)
  )

  /*
     * Implicit scope
     *  - normal scope = LOCAL SCOPE
     *  - imported scope
     *  - companions of all types involved in the method signature
     *    Eg: def sorted[B >: A](implicit ord: Ordering[B]): List[C]
     *    - List
     *    - Ordering
     *    - all the types involved = A or any supertype
     */

  /*
   * Best practices
   *
   * #1
   *  - if there is a single possible value for it
   *  - and you can edit the code for the type
   *  -> then define the implicit in the companion
   * #2
   *  - if there are many possible values for it
   *  - but a single good one
   *  - and you can edit the code for the type
   *  -> then define the good implicit in the companion
   * #3
   *  - make the user import if there are multiple good implicit
   */

  /*
  object Person {
    implicit def alphabeticOrdering: Ordering[Person] =
    // Ordering.fromLessThan((p1, p2) => p1.name < p2.name)
      Ordering.fromLessThan((a, b) => a.name.compareTo(b.name) < 0)
  }
  */

  private object AlphabeticNameOrdering {
    implicit def alphabeticOrdering: Ordering[Person] =
      Ordering.fromLessThan((a, b) => a.name.compareTo(b.name) < 0)
  }

  private object AgeOrdering {
    implicit val ageOrdering: Ordering[Person] =
      Ordering.fromLessThan((a, b) => a.age < b.age)
  }

  /*
  // this will take precedence
  implicit val ageOrdering: Ordering[Person] =
    Ordering.fromLessThan((a, b) => a.age < b.age)
   */

  import AlphabeticNameOrdering._
  //import AgeOrdering._
  println(person.sorted)

  // Exercise
  /*
   * Add Ordering by 3 different criteria
   *  - totalPrice = most used(50%)
   *  - by unit count = 25%
   *  - by unit price = 25%
   */
  case class Purchase(nUnits: Int, unitPrice: Double)

  object Purchase {
    implicit val totalPriceOrdering: Ordering[Purchase] =
      Ordering.fromLessThan((a, b) =>
        a.nUnits * a.unitPrice < b.nUnits * b.unitPrice)
  }

  object UnitCountOrdering {
    implicit val unitCountOrdering: Ordering[Purchase] =
      Ordering.fromLessThan(_.nUnits < _.nUnits)
  }

  object UnitPriceOrdering {
    implicit val unitPriceOrdering: Ordering[Purchase] =
      Ordering.fromLessThan(_.unitPrice < _.unitPrice)
  }
}
