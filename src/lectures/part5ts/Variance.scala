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
}
