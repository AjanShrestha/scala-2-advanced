package lectures.part5ts

object Variance extends App {

  trait Animal

  class Dog extends Animal

  class Cat extends Animal

  class Crocodile extends Animal

  // what is variance?
  // "inheritance" - type substitution of generics

  class Cage[T]
  // should a Cage[Cat] also inherit from Cage[Animal]

  // 1. yes - covariance
  private class CCage[+T]

  // general to specific
  private val ccage: CCage[Animal] = new CCage[Cat]

  // 2. no -invariance
  private class ICage[T]

  // private val icage: ICage[Animal] = new ICage[Cat]
  // it is incorrect
  // like
  // val x: Int = "hello

  // 3. hell no - opposite = contravariance
  private class XCage[-T]

  // specific to general
  private val xcage: XCage[Cat] = new XCage[Animal]

  class InvariantCage[T](val animal: T) // invariant

  // covariance positions
  class CovariantCage[+T](val animal: T) // COVARIANT POSITION

  // class ContravariantCage[-T](val animal: T)
  /*
   * val catCage: XCage[Cat] = new XCage[Animal](new Crocodile)
   */
  // class CovariantVariableCage[+T](var animal: T) // types of var are in CONTRAVARIANT POSITION
  /*
   * val ccage: CCage[Animal] = new CCage[Cat](new Cat)
   * ccage.animal = new Crocodile
   */
  // class ContravariantVariableCage[-T](var animal: T) // also in COVARIANT POSITION
  /*
   * val catCage: XCage[Cat] = new XCage[Animal](new Crocodile)
   */
  class InvariantVariableCage[T](var animal: T) // ok

  //  trait AnotherCovariantCage[+T] {
  //    def addAnimal(animal: T) // CONTRAVARIANT POSITION
  //  }
  /*
   * val ccage: CCage[Animal] = new CCage[Dog]
   * ccage.add(new Cat)
   */
  class AnotherContravariantCage[-T] {
    def addAnimal(animal: T) = true
  }

  val acc: AnotherContravariantCage[Cat] = new AnotherContravariantCage[Animal]
  // acc.addAnimal(new Dog)
  acc.addAnimal(new Cat)

  private class Kitty extends Cat

  acc.addAnimal(new Kitty)

  // we want to do it for covariant
  class MyList[+A] {
    // B is supertype of A
    def add[B >: A](element: B): MyList[B] = new MyList[B] // widening the type
  }

  private val emptyList = new MyList[Kitty]
  private val animals = emptyList.add(new Kitty)
  private val moreAnimals = animals.add(new Cat)
  private val evenMoreAnimals = moreAnimals.add(new Dog)
  // our goal: at all times we want to keep the property
  // that all the elements of the list have a common type

  // METHOD ARGUMENTS ARE IN CONTRAVARIANT POSITION.

  // return types
  private class PetShop[-T] {
    // def get(isItAPuppy: Boolean): T // METHOD RETURN TYPES ARE IN COVARIANT POSITION
    /*
     * val catShop = new PetShop[Animal] {
     *  def get(isItAPuppy: Boolean): Animal = new Cat
     * }
     *
     * val dogShop: PetShop[Dog] = catShop
     * dogShop.get(true) // EVIL CAT!
     */

    // solution
    // S is subtype of T
    def get[S <: T](isItAPuppy: Boolean, defaultAnimal: S): S = defaultAnimal
  }

  private val shop: PetShop[Dog] = new PetShop[Animal]
  //private val evilCat = shop.get(isItAPuppy = true, new Cat)

  private class TerraNova extends Dog

  private val bigFurry = shop.get(isItAPuppy = true, new TerraNova)

  /*
   * Big Rule
   *  - method arguments are in CONTRAVARIANT position
   *  - return types are in COVARIANT position
   */

  /*
   * Exercise
   * Design
   *  1. Invariant, covariant, contravariant
   *    Parking[T](things List[T]) {
   *      park(vehicle: T)
   *      impound(vehicles: List[T)
   *      checkVehicles(conditions: String): List[T]
   *    }
   *
   *  2. used someone else's API: IList[T]
   *  3. Parking = monad!
   *      - flatMap
   */
  private class Vehicle

  private class Bike extends Vehicle

  private class Car extends Vehicle

  private class IList[T]

  private class IParking[T](vehicles: List[T]) {
    def park(vehicle: T): IParking[T] = ???

    def impound(vehicles: List[T]): IParking[T] = ???

    def checkVehicles(conditions: String): List[T] = ???

    // used S coz of shadowing
    def flatMap[S](f: T => IParking[S]): IParking[S] = ???
  }

  private class CParking[+T](vehicles: List[T]) {
    def park[S >: T](vehicle: S): CParking[S] = ???

    def impound[S >: T](vehicles: List[S]): CParking[S] = ???

    def checkVehicles(conditions: String): List[T] = ???

    def flatMap[S](f: T => CParking[S]): CParking[S] = ???
  }

  private class XParking[-T](vehicles: List[T]) {
    def park(vehicle: T): XParking[T] = ???

    def impound(vehicles: List[T]): XParking[T] = ???

    def checkVehicles[S <: T](conditions: String): List[S] = ???

    // def flatMap[S](f: T => XParking[S]): XParking[S] = ???
    // def flatMap[S](f: Function1[T, XParking[S]]): XParking[S] = ???
    def flatMap[R <: T, S](f: R => XParking[S]): XParking[S] = ???
  }

  /*
   * Rule of thumb
   *  - use covariance = COLLECTION OF THINGS
   *  - use contravariance = GROUP OF ACTIONS
   */

  // IList
  private class CParking2[+T](vehicles: IList[T]) {
    def park[S >: T](vehicle: S): CParking2[S] = ???

    def impound[S >: T](vehicles: IList[S]): CParking2[S] = ???

    def checkVehicles[S >: T](conditions: String): IList[S] = ???
  }

  private class XParking2[-T](vehicles: List[T]) {
    def park(vehicle: T): XParking2[T] = ???

    def impound[S <: T](vehicles: IList[S]): XParking2[S] = ???

    def checkVehicles[S <: T](conditions: String): IList[S] = ???
  }

  // flatMap
}
