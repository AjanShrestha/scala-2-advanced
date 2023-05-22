package lectures.part5ts

object TypeMembers extends App {

  class Animal

  class Dog extends Animal

  class Cat extends Animal

  private class AnimalCollection {
    type AnimalType // abstract type member
    type BoundedAnimal <: Animal // upper bound Animal
    type SuperBoundedAnimal >: Dog <: Animal
    type AnimalC = Cat // aliases
    // abstract type member help the compiler
    // do sub type inference for us
  }

  private val ac = new AnimalCollection
  // private val dog: ac.AnimalType = ???
  // private val cat: ac.BoundedAnimal = new Cat
  private val pup: ac.SuperBoundedAnimal = new Dog
  private val cat: ac.AnimalC = new Cat

  private type CatAlias = Cat
  private val anotherCat: CatAlias = new Cat

  // alternative to generics
  trait MyList {
    type T

    def add(element: T): MyList
  }

  private class NonEmptyList(value: Int) extends MyList {
    override type T = Int

    override def add(element: Int): MyList = ???
  }

  // .type
  private type CatsType = cat.type
  private val newCat: CatsType = cat
  // new CatsType // only association no new type

  // Exercise
  /*
   * enforce a type to be applicable to SOME TYPES only
   */
  // LOCKED
  trait MList {
    type A

    def head: A

    def tail: MList
  }

  private trait ApplicableToNumbers {
    type A <: Number
  }

  // NOT OK
  /*
  private class CustomList(hd: String, tl: CustomList) extends MList with ApplicableToNumbers {
    type A = String

    def head: A = hd

    def tail: CustomList = tl
  }
  */

  // OK
  class IntList(hd: Int, tl: IntList) extends MList {
    override type A = Int

    def head: Int = hd

    def tail: IntList = tl
  }

  // Number
  // type members and type member constraints (bounds)
}
